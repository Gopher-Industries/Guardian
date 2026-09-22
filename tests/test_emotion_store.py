"""Tests for persistent emotion-analysis storage."""

import tempfile
import unittest
from pathlib import Path

from emotion_history import create_analysis_record
from emotion_store import get_analysis, list_patient_analyses, save_analysis


class EmotionStoreTests(unittest.TestCase):

    def setUp(self):
        self.temp_dir = tempfile.TemporaryDirectory()
        self.db_path = Path(self.temp_dir.name) / "test.sqlite3"

    def tearDown(self):
        self.temp_dir.cleanup()

    def test_saved_analysis_survives_new_database_connection(self):
        record = create_analysis_record(
            "synthetic-patient-1",
            {"timeline": [
                {"time": 0, "emotion": "happy", "confidence": 0.8}
            ]},
        )

        save_analysis(record, self.db_path)
        retrieved = get_analysis(record["analysis_id"], self.db_path)

        self.assertEqual(retrieved, record)

    def test_patient_history_excludes_other_patients(self):
        first = create_analysis_record(
            "synthetic-patient-1", {"timeline": []}
        )
        second = create_analysis_record(
            "synthetic-patient-2", {"timeline": []}
        )

        save_analysis(first, self.db_path)
        save_analysis(second, self.db_path)

        history = list_patient_analyses(
            "synthetic-patient-1", self.db_path
        )

        self.assertEqual(len(history), 1)
        self.assertEqual(history[0]["analysis_id"], first["analysis_id"])

    def test_missing_analysis_returns_none(self):
        self.assertIsNone(
            get_analysis("nonexistent-analysis", self.db_path)
        )


if __name__ == "__main__":
    unittest.main()
