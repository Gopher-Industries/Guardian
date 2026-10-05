"""Create validated emotion-analysis records."""

from datetime import datetime, timezone
from uuid import uuid4
import math

EMOTIONS = {
    "angry", "disgusted", "fearful", "happy",
    "neutral", "sad", "surprised",
}


def create_analysis_record(patient_id, analysis):
    """Build an analysis record without saving it to a database."""
    if not isinstance(patient_id, str) or not patient_id.strip():
        raise ValueError("A patient ID is required")

    if not isinstance(analysis, dict):
        raise ValueError("Analysis must be an object")

    timeline = analysis.get("timeline")
    if not isinstance(timeline, list):
        raise ValueError("Analysis timeline must be a list")

    validated_timeline = []

    for item in timeline:
        if not isinstance(item, dict):
            raise ValueError("Invalid timeline entry")

        emotion = item.get("emotion")
        time = item.get("time")
        confidence = item.get("confidence")

        if emotion not in EMOTIONS:
            raise ValueError("Invalid emotion")

        if (
            isinstance(time, bool)
            or not isinstance(time, (int, float))
            or not math.isfinite(time)
            or time < 0
        ):
            raise ValueError("Invalid timeline time")

        if (
            isinstance(confidence, bool)
            or not isinstance(confidence, (int, float))
            or not math.isfinite(confidence)
            or not 0 <= confidence <= 1
        ):
            raise ValueError("Invalid confidence")

        validated_timeline.append({
            "time": time,
            "emotion": emotion,
            "confidence": confidence,
        })

    counts = {
        emotion: sum(
            entry["emotion"] == emotion
            for entry in validated_timeline
        )
        for emotion in EMOTIONS
    }

    counts = {
        emotion: count
        for emotion, count in sorted(counts.items())
        if count > 0
    }

    dominant = max(counts, key=counts.get) if counts else None

    average = (
        round(
            sum(entry["confidence"] for entry in validated_timeline)
            / len(validated_timeline),
            3,
        )
        if validated_timeline
        else 0
    )

    return {
        "analysis_id": str(uuid4()),
        "patient_id": patient_id.strip(),
        "created_at": datetime.now(timezone.utc).isoformat(),
        "dominant_emotion": dominant,
        "average_confidence": average,
        "emotion_counts": counts,
        "timeline": validated_timeline,
        "frames_analysed": len(validated_timeline),
    }
