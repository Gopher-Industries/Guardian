#!/usr/bin/env python3
"""
Validate spreadsheet rows for Guardian Monitor AI.

Checks:
- Required columns exist
- entitiesExtracted is valid JSON with expected keys
- Basic vitals parsing & sanity bounds
- (Optional) alerts vs vitals heuristic warnings

Exit code:
- 0 = OK (no errors; warnings may still print)
- 1 = Errors found
"""

import argparse
import json
import sys
import os
import re

import pandas as pd


REQUIRED_COLS = [
    "patientId", "age", "gender",
    "observationStart", "observationEnd",
    "nursingNote", "medications",
    "heartRate", "spo2", "temperature", "bloodPressure",
    "stepsTaken", "calorieIntake", "sleepHours", "waterIntakeMl",
    "mealsSkipped", "exerciseMinutes", "bathroomVisits",
    "behaviourTags", "emotionTags", "clinicalSummary",
    "entitiesExtracted", "baselineStats", "alerts",
]

# Simple bounds to catch obvious outliers/typos
VITAL_BOUNDS = {
    "HR": (30, 200),         # bpm
    "SpO2": (70, 100),       # %
    "TempC": (32.0, 42.0),   # °C
    "SBP": (60, 240),        # mmHg
    "DBP": (30, 150),        # mmHg
}

# Heuristic "sick" rules (very basic; adjust to your needs)
def vitals_to_state(hr, spo2, temp_c, sbp, dbp):
    if hr is not None and hr >= 100: return "Sick"
    if spo2 is not None and spo2 < 93: return "Sick"
    if temp_c is not None and temp_c >= 38.0: return "Sick"
    if sbp is not None and (sbp < 95 or sbp >= 150): return "Sick"
    if dbp is not None and (dbp < 60 or dbp >= 95): return "Sick"
    return "Normal"


def parse_hr(s):
    # "85 bpm" -> 85
    if not isinstance(s, str): return None
    m = re.search(r"(\d+)\s*bpm", s, re.I)
    return int(m.group(1)) if m else None


def parse_spo2(s):
    # "97%" -> 97
    if not isinstance(s, str): return None
    m = re.search(r"(\d+)\s*%", s)
    return int(m.group(1)) if m else None


def parse_temp(s):
    # "36.8°C" or "36.8 C" -> 36.8
    if s is None: return None
    if isinstance(s, (int, float)): return float(s)
    m = re.search(r"(\d+(?:\.\d+)?)", str(s))
    return float(m.group(1)) if m else None


def parse_bp(s):
    # "120/80" -> (120, 80)
    if not isinstance(s, str): return (None, None)
    m = re.search(r"(\d{2,3})\s*/\s*(\d{2,3})", s)
    if not m: return (None, None)
    return int(m.group(1)), int(m.group(2))


def load_table(path: str) -> pd.DataFrame:
    ext = os.path.splitext(path)[1].lower()
    if ext in [".xlsx", ".xls"]:
        return pd.read_excel(path)
    elif ext == ".csv":
        return pd.read_csv(path)
    else:
        raise ValueError("Unsupported file type. Use .csv or .xlsx")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--path", required=True, help="Path to CSV/XLSX")
    ap.add_argument("--strict", action="store_true", help="Treat warnings as errors")
    args = ap.parse_args()

    try:
        df = load_table(args.path)
    except Exception as e:
        print(f"[ERROR] Failed to read file: {e}")
        sys.exit(1)

    errors = 0
    warnings = 0

    # 1) Columns
    missing = [c for c in REQUIRED_COLS if c not in df.columns]
    if missing:
        print(f"[ERROR] Missing required columns: {missing}")
        sys.exit(1)

    # 2) Row checks
    for idx, row in df.iterrows():
        row_tag = f"row={idx+1}"

        # entitiesExtracted JSON check
        ee_raw = row.get("entitiesExtracted", "")
        try:
            ee = json.loads(ee_raw) if isinstance(ee_raw, str) and ee_raw.strip() else {}
            if not isinstance(ee, dict):
                raise ValueError("entitiesExtracted must be a JSON object")
            # expected keys (not strictly required, but recommended)
            for k in ["vitals", "symptoms", "adls", "medications"]:
                if k not in ee:
                    print(f"[WARN] {row_tag}: entitiesExtracted missing key '{k}'")
                    warnings += 1
        except Exception as e:
            print(f"[ERROR] {row_tag}: invalid JSON in entitiesExtracted -> {e}")
            errors += 1
            continue

        # 3) Vitals parsing + bounds
        hr = parse_hr(row.get("heartRate"))
        spo2 = parse_spo2(row.get("spo2"))
        temp_c = parse_temp(row.get("temperature"))
        sbp, dbp = parse_bp(row.get("bloodPressure"))

        # Bounds checks
        if hr is not None and not (VITAL_BOUNDS["HR"][0] <= hr <= VITAL_BOUNDS["HR"][1]):
            print(f"[ERROR] {row_tag}: heartRate out of bounds: {row.get('heartRate')}")
            errors += 1
        if spo2 is not None and not (VITAL_BOUNDS["SpO2"][0] <= spo2 <= VITAL_BOUNDS["SpO2"][1]):
            print(f"[ERROR] {row_tag}: SpO2 out of bounds: {row.get('spo2')}")
            errors += 1
        if temp_c is not None and not (VITAL_BOUNDS["TempC"][0] <= temp_c <= VITAL_BOUNDS["TempC"][1]):
            print(f"[ERROR] {row_tag}: temperature out of bounds: {row.get('temperature')}")
            errors += 1
        if sbp is not None and not (VITAL_BOUNDS["SBP"][0] <= sbp <= VITAL_BOUNDS["SBP"][1]):
            print(f"[ERROR] {row_tag}: systolic BP out of bounds: {row.get('bloodPressure')}")
            errors += 1
        if dbp is not None and not (VITAL_BOUNDS["DBP"][0] <= dbp <= VITAL_BOUNDS["DBP"][1]):
            print(f"[ERROR] {row_tag}: diastolic BP out of bounds: {row.get('bloodPressure')}")
            errors += 1

        # 4) Heuristic alert check (non-blocking)
        inferred = vitals_to_state(hr, spo2, temp_c, sbp, dbp)
        label_alert = str(row.get("alerts") or "").strip()
        if label_alert and label_alert not in ["Normal", "Sick"]:
            print(f"[WARN] {row_tag}: alerts should be 'Normal' or 'Sick', got '{label_alert}'")
            warnings += 1
        if label_alert and inferred != label_alert:
            print(f"[WARN] {row_tag}: alerts='{label_alert}' but vitals suggest '{inferred}'")
            warnings += 1

        # 5) Minimal text sanity
        note = str(row.get("nursingNote") or "").strip()
        if not note:
            print(f"[WARN] {row_tag}: empty nursingNote")
            warnings += 1

    if errors:
        print(f"\nSummary: {errors} error(s), {warnings} warning(s)")
        sys.exit(1 if (errors or args.strict) else 0)
    else:
        print(f"\nSummary: {errors} error(s), {warnings} warning(s) — OK")
        sys.exit(1 if args.strict and warnings else 0)


if __name__ == "__main__":
    main()
