"""Lightweight tests for the Flask emotion API."""

import ast
import io
import unittest
from pathlib import Path

from flask import Flask, jsonify, request

from emotion_history import create_analysis_record


def create_test_app():
    """Load the existing route without importing the AI dependencies."""
    source = Path(__file__).resolve().parents[1] / "emotion_api.py"
    tree = ast.parse(source.read_text(encoding="utf-8"))

    route = next(
        node
        for node in tree.body
        if isinstance(node, ast.FunctionDef)
        and node.name == "analyse_video"
    )

    app = Flask(__name__)

    namespace = {
        "app": app,
        "request": request,
        "jsonify": jsonify,
        "create_analysis_record": create_analysis_record,
    }

    route_module = ast.Module(body=[route], type_ignores=[])
    exec(compile(route_module, str(source), "exec"), namespace)

    return app


class EmotionApiTests(unittest.TestCase):

    def setUp(self):
        self.client = create_test_app().test_client()

    def test_rejects_missing_video(self):
        response = self.client.post("/analyse-video")

        self.assertEqual(response.status_code, 400)
        self.assertEqual(
            response.get_json()["error"],
            "No video file was uploaded",
        )

    def test_rejects_missing_patient_id(self):
        response = self.client.post(
            "/analyse-video",
            data={
                "video": (
                    io.BytesIO(b"synthetic-test-video"),
                    "test.mp4",
                )
            },
            content_type="multipart/form-data",
        )

        self.assertEqual(response.status_code, 400)
        self.assertEqual(
            response.get_json()["error"],
            "No patient was selected",
        )


    def test_successful_analysis_returns_analysis_id(self):
        from unittest.mock import patch
        import numpy as np

        class FakeCapture:
            def __init__(self, path):
                self.frames = 0

            def isOpened(self):
                return True

            def get(self, property_id):
                return 30

            def read(self):
                if self.frames == 0:
                    self.frames += 1
                    return True, np.zeros((10, 10, 3), dtype=np.uint8)
                return False, None

            def release(self):
                pass

        class FakeCV2:
            CAP_PROP_FPS = 5
            VideoCapture = FakeCapture

        # The route was extracted without importing the AI dependencies.
        # Supply only the collaborators needed for this synthetic test.
        view = self.client.application.view_functions["analyse_video"]
        namespace = view.__globals__

        with patch.dict(
            namespace,
            {
                "cv2": FakeCV2,
                "detect_face": lambda frame: object(),
                "predict_emotion": lambda face: ("happy", 0.8),
                "os": __import__("os"),
                "tempfile": __import__("tempfile"),
                "Counter": __import__("collections").Counter,
            },
        ):
            response = self.client.post(
                "/analyse-video",
                data={
                    "patient_id": "synthetic-patient-123",
                    "video": (
                        io.BytesIO(b"synthetic-test-video"),
                        "test.mp4",
                    ),
                },
                content_type="multipart/form-data",
            )

        self.assertEqual(response.status_code, 200)
        result = response.get_json()
        self.assertEqual(result["patient_id"], "synthetic-patient-123")
        self.assertEqual(result["dominant_emotion"], "happy")
        self.assertEqual(result["frames_analysed"], 1)
        self.assertIn("analysis_id", result)
        self.assertTrue(result["analysis_id"])

if __name__ == "__main__":
    unittest.main()
