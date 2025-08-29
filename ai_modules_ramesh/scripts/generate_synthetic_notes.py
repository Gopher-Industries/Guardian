#!/usr/bin/env python3
"""
Synthetic nursing note generator (Excel/CSV)
- Matches the team spreadsheet columns
- Saves output into project data/raw/ folder
- Usage (defaults shown):
    python scripts/generate_synthetic_notes.py --patients 3 --days 4 --block-hours 6 --seed 42
"""

import argparse
import json
import os
import random
from datetime import datetime, timedelta

import numpy as np
import pandas as pd


def parse_args():
    p = argparse.ArgumentParser()
    p.add_argument("--patients", type=int, default=3, help="Number of patients to simulate")
    p.add_argument("--days", type=int, default=4, help="Days per patient")
    p.add_argument("--block-hours", type=int, default=6, dest="block_hours",
                   help="Hours per observation block (e.g., 6 -> four blocks/day)")
    p.add_argument("--start-date", type=str, default="2025-06-01",
                   help="Start date YYYY-MM-DD (UTC assumed)")
    p.add_argument("--seed", type=int, default=42, help="Random seed for reproducibility")
    p.add_argument("--basename", type=str, default="synthetic_nursing_notes_generated",
                   help="Base name for output files (no extension)")
    return p.parse_args()


# ------------------------- helpers -------------------------

SYMPTOMS = [
    "fever", "cough", "shortness of breath", "chest pain", "headache",
    "dizziness", "nausea", "fatigue", "confusion", "back pain"
]
ADLS = ["bathing", "feeding", "toileting", "dressing", "ambulation", "grooming"]
EMOTIONS = ["calm", "anxious", "agitated", "sad", "optimistic", "confused"]
BEHAVIOURS = ["slept well", "restless", "cooperative", "refused medication", "wandered", "isolated"]
MEDS = [
    ("Acetaminophen", "500mg"),
    ("Furosemide", "20mg"),
    ("Metformin", "500mg"),
    ("Amlodipine", "5mg"),
    ("Atorvastatin", "10mg"),
    ("Aspirin", "81mg"),
    ("Omeprazole", "20mg"),
]


def iso_utc(dt: datetime) -> str:
    return dt.isoformat() + "+00:00"


def generate_vitals():
    state = "Normal"
    hr = int(np.random.normal(76, 10))
    spo2 = int(np.random.normal(96, 2))
    temp = round(np.random.normal(36.8, 0.4), 1)
    systolic = int(np.random.normal(125, 12))
    diastolic = int(np.random.normal(78, 8))

    if random.random() < 0.20:
        which = random.choice(["tachy", "hypox", "fever", "hypot", "hypert"])
        state = "Sick"
        if which == "tachy":
            hr = random.randint(101, 130)
        elif which == "hypox":
            spo2 = random.randint(88, 92)
        elif which == "fever":
            temp = round(random.uniform(38.0, 39.5), 1)
        elif which == "hypot":
            systolic = random.randint(85, 95)
            diastolic = random.randint(50, 60)
        elif which == "hypert":
            systolic = random.randint(150, 180)
            diastolic = random.randint(95, 110)

    return hr, spo2, temp, systolic, diastolic, state


def build_note(hr, spo2, temp, sys, dia, adls, symptoms, behaviors):
    parts = []
    stable = (hr < 100 and spo2 >= 93 and temp < 38.0 and 95 < sys < 150)
    parts.append(f"Patient {'remains stable' if stable else 'is uncomfortable'}.")
    parts.append(f"HR {hr} bpm, SpO2 {spo2}%, Temp {temp}°C, BP {sys}/{dia}.")
    if symptoms:
        parts.append(f"Reports {', '.join(symptoms)}.")
    if adls:
        if len(adls) == 1:
            parts.append(f"Assisted with {adls[0]}.")
        else:
            parts.append(f"Assisted with {', '.join(adls[:-1])} and {adls[-1]}.")
    if behaviors:
        parts.append(f"Behaviour noted: {', '.join(behaviors)}.")
    return " ".join(parts)


def main():
    args = parse_args()
    random.seed(args.seed)
    np.random.seed(args.seed)

    # demo patients
    patients = []
    for i in range(args.patients):
        pid = f"P{str(i+1).zfill(4)}"
        age = random.choice([60, 65, 70, 72, 75, 80, 84])
        gender = random.choice(["Male", "Female"])
        patients.append({"patientId": pid, "age": age, "gender": gender})

    start_date = datetime.strptime(args.start_date, "%Y-%m-%d")
    blocks_per_day = 24 // args.block_hours
    total_blocks = blocks_per_day * args.days

    rows = []
    for p in patients:
        for b in range(total_blocks):
            start = start_date + timedelta(hours=b * args.block_hours)
            end = start + timedelta(hours=args.block_hours)

            hr, spo2, temp, sys, dia, state = generate_vitals()

            adls = random.sample(ADLS, k=random.choice([0, 1, 2]))
            symptoms = random.sample(SYMPTOMS, k=random.choice([0, 1, 2]))
            behaviors = random.sample(BEHAVIOURS, k=random.choice([0, 1]))
            emotions = random.sample(EMOTIONS, k=random.choice([1, 1, 2]))
            meds = random.sample(MEDS, k=random.choice([0, 1, 1, 2]))

            meds_str = ", ".join([f"{name}(Taken)({dose})" for name, dose in meds]) if meds else ""
            note = build_note(hr, spo2, temp, sys, dia, adls, symptoms, behaviors)

            steps = max(0, int(np.random.normal(1200, 600)))
            cals = max(800, int(np.random.normal(1800, 400)))
            sleep = round(max(0, np.random.normal(2.5, 0.8)), 1)
            water = max(300, int(np.random.normal(1400, 500)))
            meals_skipped = random.choice([0, 0, 0, 1])
            exercise = max(0, int(np.random.normal(12, 8)))
            bathroom = max(0, int(np.random.normal(3, 1)))

            entities = {
                "vitals": [f"HR {hr} bpm", f"SpO2 {spo2}%", f"Temp {temp}°C", f"BP {sys}/{dia}"],
                "symptoms": symptoms,
                "adls": adls,
                "medications": [name for name, _ in meds],
            }
            behaviour_tags = "; ".join(behaviors) if behaviors else ""
            emotion_tags = "; ".join(emotions)
            clinical_summary = (
                f"{'Sick' if state == 'Sick' else 'Stable'}; "
                f"{' & '.join(symptoms) if symptoms else 'no acute symptoms'}; "
                f"assistance: {', '.join(adls) if adls else 'independent'}."
            )

            rows.append({
                "patientId": p["patientId"],
                "age": p["age"],
                "gender": p["gender"],
                "observationStart": iso_utc(start),
                "observationEnd": iso_utc(end),
                "nursingNote": note,
                "medications": meds_str,
                "heartRate": f"{hr} bpm",
                "spo2": f"{spo2}%",
                "temperature": f"{temp}°C",
                "bloodPressure": f"{sys}/{dia}",
                "stepsTaken": steps,
                "calorieIntake": cals,
                "sleepHours": sleep,
                "waterIntakeMl": water,
                "mealsSkipped": meals_skipped,
                "exerciseMinutes": exercise,
                "bathroomVisits": bathroom,
                "behaviourTags": behaviour_tags,
                "emotionTags": emotion_tags,
                "clinicalSummary": clinical_summary,
                "entitiesExtracted": json.dumps(entities),
                "baselineStats": "",
                "alerts": state,
            })

    df = pd.DataFrame(rows)

    # Save directly into project data/raw/
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    raw_dir = os.path.join(project_root, "data", "raw")
    os.makedirs(raw_dir, exist_ok=True)

    csv_path = os.path.join(raw_dir, f"{args.basename}.csv")
    xlsx_path = os.path.join(raw_dir, f"{args.basename}.xlsx")

    df.to_csv(csv_path, index=False)
    print(f"Saved CSV: {csv_path}")

    try:
        import openpyxl  # optional
        df.to_excel(xlsx_path, index=False)
        print(f"Saved XLSX: {xlsx_path}")
    except Exception as e:
        print(f"(Skipped XLSX export — install 'openpyxl' to enable) Reason: {e}")

    print(f"Rows: {len(df)} | Patients: {args.patients} | Days: {args.days} | Blocks/day: {blocks_per_day}")


if __name__ == "__main__":
    main()
