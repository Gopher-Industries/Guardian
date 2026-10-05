import unittest

from emotion_history import create_analysis_record


class EmotionHistoryTests(unittest.TestCase):

    def test_creates_valid_record(self):
        record = create_analysis_record(
            "patient-123",
            {
                "timeline": [
                    {"time": 0, "emotion": "happy", "confidence": 0.8},
                    {"time": 1, "emotion": "happy", "confidence": 0.6},
                ]
            },
        )

        self.assertEqual(record["patient_id"], "patient-123")
        self.assertEqual(record["dominant_emotion"], "happy")
        self.assertEqual(record["average_confidence"], 0.7)
        self.assertEqual(record["frames_analysed"], 2)
        self.assertEqual(record["emotion_counts"], {"happy": 2})
        self.assertIn("analysis_id", record)
        self.assertIn("created_at", record)

    def test_rejects_missing_patient(self):
        with self.assertRaises(ValueError):
            create_analysis_record("", {"timeline": []})

    def test_rejects_invalid_emotion(self):
        with self.assertRaises(ValueError):
            create_analysis_record(
                "patient-123",
                {
                    "timeline": [
                        {"time": 0, "emotion": "unknown", "confidence": 0.8}
                    ]
                },
            )

    def test_rejects_invalid_confidence(self):
        with self.assertRaises(ValueError):
            create_analysis_record(
                "patient-123",
                {
                    "timeline": [
                        {"time": 0, "emotion": "happy", "confidence": 1.5}
                    ]
                },
            )

    def test_handles_no_detected_faces(self):
        record = create_analysis_record(
            "patient-123",
            {"timeline": []},
        )

        self.assertIsNone(record["dominant_emotion"])
        self.assertEqual(record["frames_analysed"], 0)
        self.assertEqual(record["emotion_counts"], {})


if __name__ == "__main__":
    unittest.main()
