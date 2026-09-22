"""Local SQLite storage for synthetic emotion-analysis development."""

import json
import sqlite3
from pathlib import Path


DEFAULT_DB_PATH = Path(__file__).resolve().parent / "emotion_history.sqlite3"


def _connect(db_path):
    connection = sqlite3.connect(str(db_path))
    connection.row_factory = sqlite3.Row
    return connection


def initialize_database(db_path=DEFAULT_DB_PATH):
    """Create the analysis table if it does not exist."""
    with _connect(db_path) as connection:
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS emotion_analyses (
                analysis_id TEXT PRIMARY KEY,
                patient_id TEXT NOT NULL,
                created_at TEXT NOT NULL,
                record_json TEXT NOT NULL
            )
            """
        )
        connection.execute(
            """
            CREATE INDEX IF NOT EXISTS idx_emotion_patient_created
            ON emotion_analyses (patient_id, created_at DESC)
            """
        )


def save_analysis(record, db_path=DEFAULT_DB_PATH):
    """Save an already-validated analysis record."""
    required = {"analysis_id", "patient_id", "created_at"}
    if not isinstance(record, dict) or not required.issubset(record):
        raise ValueError("A validated analysis record is required")

    initialize_database(db_path)

    with _connect(db_path) as connection:
        connection.execute(
            """
            INSERT INTO emotion_analyses
                (analysis_id, patient_id, created_at, record_json)
            VALUES (?, ?, ?, ?)
            """,
            (
                record["analysis_id"],
                record["patient_id"],
                record["created_at"],
                json.dumps(record, allow_nan=False),
            ),
        )

    return record["analysis_id"]


def get_analysis(analysis_id, db_path=DEFAULT_DB_PATH):
    """Return one analysis, or None if it does not exist."""
    initialize_database(db_path)

    with _connect(db_path) as connection:
        row = connection.execute(
            """
            SELECT record_json
            FROM emotion_analyses
            WHERE analysis_id = ?
            """,
            (analysis_id,),
        ).fetchone()

    return json.loads(row["record_json"]) if row else None


def list_patient_analyses(patient_id, db_path=DEFAULT_DB_PATH):
    """Return a patient's analyses, newest first."""
    initialize_database(db_path)

    with _connect(db_path) as connection:
        rows = connection.execute(
            """
            SELECT record_json
            FROM emotion_analyses
            WHERE patient_id = ?
            ORDER BY created_at DESC, analysis_id DESC
            """,
            (patient_id,),
        ).fetchall()

    return [json.loads(row["record_json"]) for row in rows]
