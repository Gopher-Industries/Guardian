
"""
Guardian Monitor Backend Bridge
--------------------------------
Keeps guardian_pipeline_final.py unchanged.

What this script does:
1. Runs guardian_pipeline_final.py as-is
2. Reads predictions_baseline.csv
3. Logs into the Guardian backend
4. Sends Medium/High alerts automatically
5. Optionally fetches alerts back for confirmation

How to use:
- Put this file in the same folder as guardian_pipeline_final.py
- Make sure requests, pandas are installed
- Run: python3 guardian_backend_bridge.py

Notes:
- Update EMAIL/PASSWORD only if your backend credentials change
- Adjust SEND_LEVELS if you only want High alerts
"""

import os
import sys
import subprocess
from pathlib import Path

import pandas as pd
import requests

# ── Backend Configuration ─────────────────────────────────────────────────────

BASE_URL = "https://guardian-backend-ashen.vercel.app/api/v1"
API_EMAIL = "nurse@guardian.com"
API_PASSWORD = "Password123!"
SEND_LEVELS = {"High", "Medium"}   # Change to {"High"} if needed
FETCH_ALERTS_AFTER_SEND = True

# ── Paths ─────────────────────────────────────────────────────────────────────

HERE = Path(__file__).resolve().parent
PIPELINE_FILE = HERE / "guardian_pipeline_final.py"
PREDICTIONS_FILE = HERE / "predictions_baseline.csv"


def run_pipeline() -> None:
    """Run the existing Guardian Monitor pipeline unchanged."""
    if not PIPELINE_FILE.exists():
        raise FileNotFoundError(
            f"Could not find {PIPELINE_FILE.name} in {HERE}. "
            "Put this bridge file in the same folder as guardian_pipeline_final.py."
        )

    print(f"[BRIDGE] Running existing pipeline: {PIPELINE_FILE.name}")
    result = subprocess.run(
        [sys.executable, str(PIPELINE_FILE)],
        cwd=str(HERE),
        capture_output=False,
        text=True
    )
    if result.returncode != 0:
        raise RuntimeError(f"Pipeline failed with exit code {result.returncode}")


def get_auth_headers():
    """Login and return Bearer auth headers."""
    print("[BRIDGE] Logging into backend...")
    login_res = requests.post(
        f"{BASE_URL}/auth/login",
        json={
            "email": API_EMAIL,
            "password": API_PASSWORD
        },
        timeout=20
    )

    print(f"[BRIDGE] Login status: {login_res.status_code}")
    try:
        login_json = login_res.json()
    except Exception:
        raise RuntimeError(f"Login response was not JSON: {login_res.text}")

    if login_res.status_code != 200:
        raise RuntimeError(f"Login failed: {login_json}")

    token = login_json.get("token")
    if not token:
        raise RuntimeError(f"Login succeeded but no token returned: {login_json}")

    return {"Authorization": f"Bearer {token}"}


def build_alert_message(row: pd.Series) -> tuple[str, str]:
    """
    Convert a pipeline row into:
    - alert_type
    - message
    """
    final_alert = str(row.get("final_alert", "unknown")).strip()
    patient_id = row.get("subject_id", "unknown")
    explanation = str(row.get("explanation", "No explanation provided.")).strip()
    anomaly_type = str(row.get("anomaly_type", "none")).strip()

    alert_type = f"{final_alert.lower()}_alert"

    if anomaly_type and anomaly_type != "none":
        message = f"Patient {patient_id}: {explanation} (Anomaly: {anomaly_type})"
    else:
        message = f"Patient {patient_id}: {explanation}"

    return alert_type, message


def send_alerts(headers) -> int:
    """Read predictions_baseline.csv and POST selected alerts."""
    if not PREDICTIONS_FILE.exists():
        raise FileNotFoundError(
            f"{PREDICTIONS_FILE.name} not found. "
            "Run guardian_pipeline_final.py first or let this bridge run it."
        )

    df = pd.read_csv(PREDICTIONS_FILE)

    required_cols = {"subject_id", "final_alert", "explanation"}
    missing = required_cols - set(df.columns)
    if missing:
        raise ValueError(f"Predictions file is missing required columns: {missing}")

    sent_count = 0
    candidates = df[df["final_alert"].isin(SEND_LEVELS)].copy()

    print(f"[BRIDGE] Alerts eligible for send: {len(candidates)}")

    for _, row in candidates.iterrows():
        alert_type, message = build_alert_message(row)

        res = requests.post(
            f"{BASE_URL}/alerts",
            headers=headers,
            json={
                "alert_type": alert_type,
                "message": message
            },
            timeout=20
        )

        print(f"[BRIDGE] POST /alerts -> {res.status_code}")

        try:
            payload = res.json()
        except Exception:
            payload = res.text

        print(f"[BRIDGE] Response: {payload}")

        if res.status_code in (200, 201):
            sent_count += 1

    return sent_count


def fetch_alerts(headers):
    """Fetch alerts from backend for confirmation."""
    print("[BRIDGE] Fetching alerts from backend...")
    res = requests.get(f"{BASE_URL}/alerts", headers=headers, timeout=20)
    print(f"[BRIDGE] GET /alerts -> {res.status_code}")
    try:
        print(res.json())
    except Exception:
        print(res.text)


def main():
    print("=" * 72)
    print("GUARDIAN MONITOR — BACKEND ALERT BRIDGE")
    print("=" * 72)

    run_pipeline()
    headers = get_auth_headers()
    sent = send_alerts(headers)
    print(f"[BRIDGE] Successful sends: {sent}")

    if FETCH_ALERTS_AFTER_SEND:
        fetch_alerts(headers)

    print("[BRIDGE] Done.")


if __name__ == "__main__":
    main()
