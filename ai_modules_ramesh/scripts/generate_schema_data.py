#!/usr/bin/env python3
"""
Generate synthetic AI Behaviour data in the team's JSON schema.

- Adheres to nested schema (medications array, vitals object, adls object, etc.)
- Observation blocks: 6-hour windows (00-06, 06-12, 12-18, 18-24)
- Date range: default 60 days from 2025-06-01
- Patients: default 10, IDs within P0001-P0020
- Ages: 60-90
- Writes JSONL to data/raw/<basename>.jsonl
"""

import argparse
import json
import os
import random
from datetime import datetime, timedelta
from typing import Dict, Any, List, Tuple

import numpy as np


def parse_args():
    p = argparse.ArgumentParser()
    p.add_argument("--patients", type=int, default=10, help="Number of patients (max 10 recommended)")
    p.add_argument("--start-date", type=str, default="2025-06-01", help="YYYY-MM-DD UTC start date")
    p.add_argument("--days", type=int, default=60, help="Number of days to generate")
    p.add_argument("--block-hours", type=int, default=6, help="Hours per observation block (must divide 24)")
    p.add_argument("--seed", type=int, default=42, help="Random seed")
    p.add_argument("--basename", type=str, default="ai_behaviour_synthetic", help="Output base filename (no ext)")
    # Probabilities for state distribution
    p.add_argument("--p-normal", type=float, default=0.70, help="Probability of Normal block")
    p.add_argument("--p-uncomfortable", type=float, default=0.20, help="Probability of Uncomfortable block")
    p.add_argument("--p-sick", type=float, default=0.10, help="Probability of Sick block")
    return p.parse_args()


# -------------------------- Constants & Pools --------------------------

SYMPTOMS = [
    "fever", "cough", "shortness of breath", "chest pain", "headache",
    "dizziness", "nausea", "fatigue", "confusion", "back pain"
]
ADL_TERMS = ["bathing", "feeding", "toileting", "dressing", "ambulation", "grooming"]
BEHAVIOURS = ["slept well", "restless", "cooperative", "refused medication", "wandered", "isolated"]
EMOTIONS = ["calm", "anxious", "agitated", "sad", "optimistic", "confused"]
MEDS = [
    ("Acetaminophen", "500mg"),
    ("Furosemide", "20mg"),
    ("Metformin", "500mg"),
    ("Amlodipine", "5mg"),
    ("Atorvastatin", "10mg"),
    ("Aspirin", "81mg"),
    ("Omeprazole", "20mg"),
]
MED_COMPLIANCE = ["Taken", "Missed", "Delayed"]


# -------------------------- Helpers --------------------------

def iso_utc(dt: datetime) -> str:
    """Ensure explicit +00:00 suffix (UTC)."""
    return dt.isoformat() + "+00:00"


def choose_state(p_normal: float, p_uncomfortable: float, p_sick: float) -> str:
    x = random.random()
    if x < p_normal:
        return "Normal"
    if x < p_normal + p_uncomfortable:
        return "Uncomfortable"
    return "Sick"


def vitals_for_state(state: str) -> Tuple[int, int, float, int, int]:
    """
    Sample vitals conditioned on 'state' to be realistic.
    Returns: (HR, SpO2, TempC, SBP, DBP)
    """
    if state == "Normal":
        hr = int(np.clip(np.random.normal(76, 8), 55, 98))
        spo2 = int(np.clip(np.random.normal(97, 1), 94, 100))
        temp = round(float(np.clip(np.random.normal(36.7, 0.3), 36.0, 37.8)), 1)
        sbp = int(np.clip(np.random.normal(125, 10), 100, 145))
        dbp = int(np.clip(np.random.normal(78, 7), 60, 90))
    elif state == "Uncomfortable":
        # borderline/early deviations
        hr = int(np.clip(np.random.normal(92, 10), 75, 110))
        spo2 = int(np.clip(np.random.normal(94, 2), 90, 97))
        temp = round(float(np.clip(np.random.normal(37.6, 0.5), 36.8, 38.3)), 1)
        sbp = int(np.clip(np.random.normal(145, 12), 110, 155))
        dbp = int(np.clip(np.random.normal(88, 8), 65, 100))
    else:  # Sick
        # clear abnormalities in at least one dimension
        # randomly pick a primary abnormal axis
        axis = random.choice(["tachy", "hypox", "fever", "hypot", "hypert"])
        hr = int(np.clip(np.random.normal(105, 12), 95, 140)) if axis == "tachy" else int(np.clip(np.random.normal(96, 12), 70, 130))
        spo2 = int(np.clip(np.random.normal(90, 2), 85, 92)) if axis == "hypox" else int(np.clip(np.random.normal(95, 3), 85, 100))
        temp = round(float(np.clip(np.random.normal(38.4, 0.5), 37.8, 39.5)), 1) if axis == "fever" else round(float(np.clip(np.random.normal(36.9, 0.6), 35.5, 39.2)), 1)
        if axis == "hypot":
            sbp = int(np.clip(np.random.normal(90, 5), 70, 100))
            dbp = int(np.clip(np.random.normal(55, 5), 45, 70))
        elif axis == "hypert":
            sbp = int(np.clip(np.random.normal(165, 10), 150, 190))
            dbp = int(np.clip(np.random.normal(100, 8), 95, 120))
        else:
            sbp = int(np.clip(np.random.normal(138, 20), 95, 180))
            dbp = int(np.clip(np.random.normal(85, 15), 55, 110))
    return hr, spo2, temp, sbp, dbp


def sample_adls(state: str) -> Dict[str, Any]:
    """
    ADL metrics influenced lightly by state.
    """
    mult = {"Normal": 1.0, "Uncomfortable": 0.8, "Sick": 0.6}[state]
    steps = max(0, int(np.random.normal(1800 * mult, 600)))
    calories = max(800, int(np.random.normal(1900 * mult, 350)))
    sleep = round(max(0, np.random.normal(3.0 + (0.5 if state != "Normal" else 0), 0.8)), 1)
    water = max(300, int(np.random.normal(1500 * mult, 500)))
    meals_skipped = 1 if (state != "Normal" and random.random() < 0.35) else 0
    exercise = max(0, int(np.random.normal(15 * mult, 8)))
    return {
        "stepsTaken": steps,
        "calorieIntake": calories,
        "sleepHours": sleep,
        "waterIntakeMl": water,
        "mealsSkipped": meals_skipped,
        "exerciseMinutes": exercise,
    }


def build_note(state: str, hr: int, spo2: int, temp: float, sbp: int, dbp: int,
               adl_terms: List[str], symptoms: List[str], behaviours: List[str]) -> str:
    status_phrase = {
        "Normal": "remains stable",
        "Uncomfortable": "is uncomfortable but stable",
        "Sick": "is clinically unwell",
    }[state]
    parts = [f"Patient {status_phrase}.",
             f"HR {hr} bpm, SpO2 {spo2}%, Temp {temp}°C, BP {sbp}/{dbp}."]
    if symptoms:
        parts.append(f"Reports {', '.join(symptoms)}.")
    if adl_terms:
        if len(adl_terms) == 1:
            parts.append(f"Assisted with {adl_terms[0]}.")
        else:
            parts.append(f"Assisted with {', '.join(adl_terms[:-1])} and {adl_terms[-1]}.")
    if behaviours:
        parts.append(f"Behaviour noted: {', '.join(behaviours)}.")
    return " ".join(parts)


def make_medications(block_start: datetime, count: int = 0) -> List[Dict[str, str]]:
    """Create medication events within the 6h block."""
    meds = []
    for _ in range(count):
        name, dose = random.choice(MEDS)
        offset_min = random.randint(5, 5 + 5 * 60)  # within ~5h55m
        scheduled = block_start + timedelta(minutes=offset_min)
        compliance = random.choices(MED_COMPLIANCE, weights=[0.75, 0.15, 0.10], k=1)[0]
        meds.append({
            "name": name,
            "dose": dose,
            "scheduledTime": iso_utc(scheduled),
            "complianceStatus": compliance
        })
    return meds


def main():
    args = parse_args()
    random.seed(args.seed)
    np.random.seed(args.seed)

    # sanity for probabilities
    total_p = args.p_normal + args.p_uncomfortable + args.p_sick
    if abs(total_p - 1.0) > 1e-6:
        raise ValueError("p-normal + p-uncomfortable + p-sick must sum to 1.0")

    # patients: P0001-P0020 range for AI Behaviour project
    max_pool = [f"P{str(i).zfill(4)}" for i in range(1, 21)]
    if args.patients > len(max_pool):
        raise ValueError(f"patients must be <= {len(max_pool)} for this project")
    patient_ids = random.sample(max_pool, k=args.patients)
    patients = [{"patientId": pid, "age": random.randint(60, 90), "gender": random.choice(["Male", "Female"])}
                for pid in patient_ids]

    start_date = datetime.strptime(args.start_date, "%Y-%m-%d")
    if 24 % args.block_hours != 0:
        raise ValueError("--block-hours must divide 24")
    blocks_per_day = 24 // args.block_hours

    # Prepare output path
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    raw_dir = os.path.join(project_root, "data", "raw")
    os.makedirs(raw_dir, exist_ok=True)
    out_path = os.path.join(raw_dir, f"{args.basename}.jsonl")

    rows = 0
    with open(out_path, "w", encoding="utf-8") as f:
        for p in patients:
            for day in range(args.days):
                for b in range(blocks_per_day):
                    block_start = start_date + timedelta(days=day, hours=b * args.block_hours)
                    block_end = block_start + timedelta(hours=args.block_hours)

                    state = choose_state(args.p_normal, args.p_uncomfortable, args.p_sick)
                    hr, spo2, temp, sbp, dbp = vitals_for_state(state)

                    # meds count: fewer on Normal, more chance on Uncomfortable/Sick
                    med_n = random.choices([0, 1, 2], weights=[0.6, 0.3, 0.1] if state == "Normal"
                                           else [0.4, 0.4, 0.2] if state == "Uncomfortable"
                                           else [0.3, 0.5, 0.2], k=1)[0]
                    meds = make_medications(block_start, med_n)

                    adl_terms = random.sample(ADL_TERMS, k=random.choice([0, 1, 2]))
                    behaviours = random.sample(BEHAVIOURS, k=random.choice([0, 1]))
                    emotions = random.sample(EMOTIONS, k=random.choice([1, 1, 2]))

                    # adl metrics
                    adls_obj = sample_adls(state)

                    note = build_note(state, hr, spo2, temp, sbp, dbp, adl_terms, [], behaviours)

                    # Optional fields policy: keep keys, leave values empty/null when not used
                    # emotionAnalysis provided but lightweight; models can overwrite later
                    emotion_analysis = {
                        "tags": emotions,
                        "confidenceScores": {tag: round(random.uniform(0.5, 0.95), 2) for tag in emotions}
                    }

                    # entitiesExtracted: simple seed (models will replace later)
                    entities_extracted = {
                        "symptoms": [],                  # empty; models will fill
                        "vitals": [f"HR {hr} bpm", f"SpO2 {spo2}%", f"Temp {temp}°C", f"BP {sbp}/{dbp}"],
                        "procedures": [],
                        "medications": [m["name"] for m in meds] if meds else []
                    }

                    # baselineStats: keep keys with nulls (as per instructions)
                    baseline_stats = {
                        "avgHeartRate": None,
                        "avgSpo2": None,
                        "avgSleepHours": None,
                        "avgCalorieIntake": None,
                        "avgScreenTimeMinutes": None,
                        "avgBathroomVisits": None,
                        "usualDietCompliance": None
                    }

                    clinical_summary = {
                        "Normal": f"Stable; no acute symptoms; independent or minimal assistance.",
                        "Uncomfortable": f"Uncomfortable; monitor vitals; review hydration/rest.",
                        "Sick": f"Clinically unwell; abnormal vitals present; escalate observation."
                    }[state]

                    # alerts: optional array; include a code for Sick, leave empty otherwise
                    alerts = []
                    if state == "Sick":
                        # pick one or two alert codes
                        candidates = ["ALERT_TACHY", "ALERT_HYPOX", "ALERT_FEVER", "ALERT_BP_ABNORM"]
                        alerts = random.sample(candidates, k=random.choice([1, 1, 2]))

                    obj: Dict[str, Any] = {
                        "patientId": p["patientId"],                     # MANDATORY
                        "age": p["age"],                                 # MANDATORY
                        "gender": p["gender"],                           # MANDATORY

                        "observationStart": iso_utc(block_start),        # MANDATORY
                        "observationEnd": iso_utc(block_end),            # MANDATORY

                        "nursingNote": note,                             # MANDATORY

                        "medications": meds,                             # MANDATORY (array; can be empty)

                        "vitals": {                                      # MANDATORY
                            "heartRate": {"value": hr, "unit": "bpm"},
                            "spo2": {"value": spo2, "unit": "%"},
                            "temperature": {"value": temp, "unit": "°C"},
                            "bloodPressure": {
                                "systolic": sbp,
                                "diastolic": dbp,
                                "unit": "mmHg"
                            }
                        },

                        "adls": adls_obj,                                # MANDATORY (6 metrics)

                        "behaviourTags": behaviours,                      # OPTIONAL (keep key)

                        "emotionAnalysis": emotion_analysis,              # OPTIONAL (keep key)

                        "clinicalSummary": clinical_summary,              # MANDATORY

                        "entitiesExtracted": entities_extracted,          # OPTIONAL (keep key)

                        "baselineStats": baseline_stats,                  # OPTIONAL (keys with nulls)

                        "alerts": alerts                                  # OPTIONAL (array; possibly empty)
                    }

                    f.write(json.dumps(obj, ensure_ascii=False) + "\n")
                    rows += 1

    print(f"Saved JSONL: {out_path}")
    print(f"Patients: {len(patients)} | Days: {args.days} | Blocks/day: {blocks_per_day} | Rows: {rows}")


if __name__ == "__main__":
    main()
