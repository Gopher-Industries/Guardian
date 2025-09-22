import argparse, csv, json, os, sys, requests, random
from datetime import datetime, timedelta, timezone

UTC = timezone.utc

# ----- Desired distribution -----
# Neutral(Calm) 50, Happy 20, Sad 10, Worried 10, Angry 5, Confused 5
WEIGHTS = [
    ("Calm",     50),  # neutral
    ("Happy",    20),  # positive
    ("Sad",      10),  # negative
    ("Worried",  10),  # negative
    ("Angry",     5),  # negative
    ("Confused",  5),  # negative
]
LABELS = [w[0] for w in WEIGHTS]
CUMWEIGHTS = []
_total = 0
for _, w in WEIGHTS:
    _total += w
    CUMWEIGHTS.append(_total)

def sample_emotion(rnd: random.Random) -> str:
    x = rnd.uniform(0, CUMWEIGHTS[-1])
    for label, cum in zip(LABELS, CUMWEIGHTS):
        if x <= cum:
            return label
    return LABELS[-1]

def polarity_for_label(label: str) -> str:
    if label in ("Calm",): return "neutral"
    if label in ("Happy",): return "positive"
    return "negative"

# ----- JSON Schema (nested vitals + ADLs) -----
SCHEMA = {
  "type": "object",
  "additionalProperties": False,
  "required": [
    "patient_id","age","gender","observationStart","observationEnd",
    "nursing_note","emotion_polarity","emotion_label","medications","vitals","adls"
  ],
  "properties": {
    "patient_id": {"type":"string"},
    "age": {"type":"integer"},
    "gender": {"type":"string"},
    "observationStart": {"type":"string"},
    "observationEnd": {"type":"string"},
    "nursing_note": {"type":"string"},
    "emotion_polarity": {"type":"string", "enum":["positive","neutral","negative"]},
    "emotion_label": {"type":"string", "enum":["Calm","Happy","Sad","Worried","Angry","Confused"]},
    "medications": {
      "type":"array",
      "items":{
        "type":"object",
        "additionalProperties": False,
        "required":["name","dose","scheduledTime","complianceStatus"],
        "properties":{
          "name":{"type":"string"},
          "dose":{"type":"string"},
          "scheduledTime":{"type":"string"},
          "complianceStatus":{"type":"string","enum":["Taken","Missed","Delayed"]}
        }
      }
    },
    "vitals":{
      "type":"object",
      "additionalProperties": False,
      "required":["heartRate","spo2","temperature","bloodPressure"],
      "properties":{
        "heartRate":{
          "type":"object",
          "required":["value","unit"],
          "additionalProperties": False,
          "properties":{"value":{"type":"integer"},"unit":{"type":"string"}}
        },
        "spo2":{
          "type":"object",
          "required":["value","unit"],
          "additionalProperties": False,
          "properties":{"value":{"type":"integer"},"unit":{"type":"string"}}
        },
        "temperature":{
          "type":"object",
          "required":["value","unit"],
          "additionalProperties": False,
          "properties":{"value":{"type":"number"},"unit":{"type":"string"}}
        },
        "bloodPressure":{
          "type":"object",
          "required":["systolic","diastolic","unit"],
          "additionalProperties": False,
          "properties":{
            "systolic":{"type":"integer"},
            "diastolic":{"type":"integer"},
            "unit":{"type":"string"}
          }
        }
      }
    },
    "adls":{
      "type":"object",
      "additionalProperties": False,
      "required":["stepsTaken","calorieIntake","sleepHours","waterIntakeMl","mealsSkipped","exerciseMinutes"],
      "properties":{
        "stepsTaken":{"type":"integer"},
        "calorieIntake":{"type":"integer"},
        "sleepHours":{"type":"number"},
        "waterIntakeMl":{"type":"integer"},
        "mealsSkipped":{"type":"integer"},
        "exerciseMinutes":{"type":"integer"}
      }
    }
  }
}

def iso_z(dt: datetime) -> str:
    """Return ISO-8601 string with literal 'Z' for UTC."""
    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=UTC)
    return dt.astimezone(UTC).isoformat().replace("+00:00", "Z")

def project_paths():
    here = os.path.abspath(os.getcwd())
    out_dir = os.path.join(here, "outputs")
    os.makedirs(out_dir, exist_ok=True)
    return out_dir

def windows_for_day_utc(day_date):
    base = datetime(day_date.year, day_date.month, day_date.day, tzinfo=UTC)
    for h0,h1 in [(0,6),(6,12),(12,18),(18,24)]:
        yield base + timedelta(hours=h0), base + timedelta(hours=h1)

def emotion_hint(label: str, start_dt: datetime, end_dt: datetime) -> str:
    # Provide compact guidance the model can follow for vitals/ADLs/behavior
    wnd = f"{start_dt.hour:02d}-{end_dt.hour:02d}Z"
    if label == "Worried":
        return f"TargetEmotion=Worried. Sometimes elevate HR to 90-100 bpm and BP to 135-150/80-95; note restlessness or repeated questions in {wnd}; ADLs steps may be moderate-high from pacing; intake variable."
    if label == "Angry":
        return "TargetEmotion=Angry. Consider HR 90-105 and BP 140-160/85-100; show resistance to care or meds, raised voice; ADLs reduced by lack of cooperation; if Missed/Delayed, explain briefly."
    if label == "Sad":
        return "TargetEmotion=Sad. Keep vitals near baseline; depict low energy: low steps, low intake, maybe skipped meal; document supportive interactions."
    if label == "Confused":
        return "TargetEmotion=Confused. Vitals usually normal; describe disorientation, exit-seeking or bed-exit attempts; emphasize redirection and safety measures."
    if label == "Happy":
        return "TargetEmotion=Happy. Polarity positive; show engagement, pleasant affect, good participation; routine meds taken; vitals within goal."
    return "TargetEmotion=Calm. Polarity neutral; routine care, stable vitals, ordinary ADLs; avoid repetitive phrasing."

def make_prompt(demo, start_dt, end_dt, label: str):
    return f"""
Generate exactly ONE JSON object describing a nursing note for the patient below and the EXACT UTC time window.
- Copy demographics EXACTLY.
- observationStart={iso_z(start_dt)}, observationEnd={iso_z(end_dt)} (UTC Z)
- All medication scheduledTime must be within this window (UTC Z).
- Use the exact field names and nesting from policy (vitals nested with units; adls uses stepsTaken, calorieIntake, sleepHours, waterIntakeMl, mealsSkipped, exerciseMinutes; medications has no 'route').
- {emotion_hint(label, start_dt, end_dt)}
Demographics:
patient_id={demo['patient_id']}
age={demo['age']}
gender={demo['gender']}
"""

def call_ollama(model, prompt):
    url = "http://localhost:11434/api/generate"
    payload = {
        "model": model,
        "prompt": prompt,
        "format": {"type":"object", **SCHEMA},
        "options": {"temperature": 0.2, "num_ctx": 4096, "repeat_penalty": 1.05},
        "stream": False
    }
    r = requests.post(url, json=payload, timeout=180)
    r.raise_for_status()
    data = r.json()
    text = data.get("response","").strip()
    return json.loads(text)

def in_range(v, lo, hi): return lo <= v <= hi

def validate_object(obj, demo, start_dt, end_dt):
    issues = []

    # Demographics
    if obj.get("patient_id") != demo["patient_id"]: issues.append("patient_id mismatch")
    if obj.get("age") != demo["age"]: issues.append("age mismatch")
    if obj.get("gender") != demo["gender"]: issues.append("gender mismatch")

    # Exact window strings (must be Z)
    if obj.get("observationStart") != iso_z(start_dt): issues.append("observationStart not exact Z")
    if obj.get("observationEnd")   != iso_z(end_dt):   issues.append("observationEnd not exact Z")

    # Med times inside window & Z, no extra fields
    try:
        for m in obj.get("medications", []):
            if set(m.keys()) - {"name","dose","scheduledTime","complianceStatus"}:
                issues.append("medications contains extra fields")
                break
            if not all(k in m for k in ["name","dose","scheduledTime","complianceStatus"]):
                issues.append("medications missing required field")
                break
            if not m["scheduledTime"].endswith("Z"):
                issues.append("med scheduledTime not Z")
            try:
                t = datetime.fromisoformat(m["scheduledTime"].replace("Z","+00:00"))
                if not (start_dt <= t <= end_dt):
                    issues.append("med scheduledTime outside window")
            except Exception:
                issues.append("med scheduledTime parse error")
    except Exception:
        issues.append("medications parse error")

    # Emotion mapping
    pol = obj.get("emotion_polarity"); lab = obj.get("emotion_label")
    if pol == "positive" and lab not in ["Calm","Happy"]: issues.append("emotion mismatch")
    if pol == "neutral"  and lab not in ["Calm"]: issues.append("emotion mismatch")
    if pol == "negative" and lab not in ["Sad","Worried","Angry","Confused"]: issues.append("emotion mismatch")

    # Vitals ranges + units
    v = obj.get("vitals", {})
    try:
        if v.get("heartRate",{}).get("unit") != "bpm": issues.append("hr unit not bpm")
        if v.get("spo2",{}).get("unit") != "%": issues.append("spo2 unit not %")
        if v.get("temperature",{}).get("unit") != "°C": issues.append("temp unit not °C")
        if v.get("bloodPressure",{}).get("unit") != "mmHg": issues.append("bp unit not mmHg")

        hr = int(v.get("heartRate",{}).get("value", -1))
        sp = int(v.get("spo2",{}).get("value", -1))
        tc = float(v.get("temperature",{}).get("value", -999))
        bp = v.get("bloodPressure",{})
        sys = int(bp.get("systolic", -1)); dia = int(bp.get("diastolic", -1))

        if not in_range(hr,55,110): issues.append("hr out of range")
        if not in_range(sp,90,100): issues.append("spo2 out of range")
        if not in_range(tc,36.0,37.9): issues.append("temp out of range")
        if not in_range(sys,90,160): issues.append("bp systolic out of range")
        if not in_range(dia,55,100): issues.append("bp diastolic out of range")
    except Exception:
        issues.append("vitals parse error")

    # ADLs plausibility & variability checks
    a = obj.get("adls", {})
    try:
        need = ["stepsTaken","calorieIntake","sleepHours","waterIntakeMl","mealsSkipped","exerciseMinutes"]
        if any(k not in a for k in need):
            issues.append("adls missing fields")
        else:
            steps = int(a["stepsTaken"])
            cals = int(a["calorieIntake"])
            sleep = float(a["sleepHours"])
            water = int(a["waterIntakeMl"])
            meals = int(a["mealsSkipped"])
            ex = int(a["exerciseMinutes"])
            # Loose plausibility ranges per 6h window
            if not in_range(steps,0,2000): issues.append("adls stepsTaken implausible")
            if not in_range(cals,0,800): issues.append("adls calorieIntake implausible")
            if not in_range(sleep,0.0,4.5): issues.append("adls sleepHours implausible")
            if not in_range(water,0,1000): issues.append("adls waterIntakeMl implausible")
            if not in_range(meals,0,1): issues.append("adls mealsSkipped implausible")
            # Allow a bit more exercise during day
            if not in_range(ex,0,30): issues.append("adls exerciseMinutes implausible")
    except Exception:
        issues.append("adls parse error")

    return issues

def main():
    ap = argparse.ArgumentParser(description="Generate JSONL nurse notes (UTC Z format) with emotion balancing")
    ap.add_argument("--patients", required=True, help="Path to patients.csv (columns: patient_id,age,gender)")
    ap.add_argument("--model", default="nurse-notes", help="Ollama model name")
    ap.add_argument("--date", default="2025-06-01", help="Start date (YYYY-MM-DD) in UTC")
    ap.add_argument("--days", type=int, default=1, help="How many days (4 windows per day)")
    ap.add_argument("--seed", type=int, default=7, help="RNG seed for reproducible emotion mix")
    ap.add_argument("--out", default="outputs/notes.jsonl", help="Output JSONL")
    ap.add_argument("--errors", default="outputs/notes.errors.jsonl", help="Errors JSONL")
    args = ap.parse_args()

    rnd = random.Random(args.seed)

    os.makedirs("outputs", exist_ok=True)
    print(f"Writing: {os.path.abspath(args.out)}")
    print(f"Errors : {os.path.abspath(args.errors)}")

    try:
        y,m,d = map(int, args.date.split("-"))
        start_day = datetime(y,m,d,tzinfo=UTC).date()
    except Exception as e:
        print(f"Bad --date {args.date}: {e}", file=sys.stderr)
        sys.exit(1)

    # Track distribution (for a quick summary)
    counts = {k:0 for k,_ in WEIGHTS}

    with open(args.patients, newline="", encoding="utf-8") as f_in, \
         open(args.out, "a", encoding="utf-8") as f_out, \
         open(args.errors, "a", encoding="utf-8") as f_err:

        reader = csv.DictReader(f_in)
        for row in reader:
            demo = {"patient_id": row["patient_id"], "age": int(row["age"]), "gender": row["gender"]}
            print(f"\nPatient {demo['patient_id']} (age {demo['age']}, {demo['gender']})")

            for day_offset in range(args.days):
                day_date = (start_day + timedelta(days=day_offset))
                for start_dt, end_dt in windows_for_day_utc(day_date):
                    # Pick a target label with your desired weights
                    label = sample_emotion(rnd)
                    counts[label] += 1

                    prompt = make_prompt(demo, start_dt, end_dt, label)
                    try:
                        obj = call_ollama(args.model, prompt)
                        issues = validate_object(obj, demo, start_dt, end_dt)

                        # Also enforce that the returned label matches our target (soft check → errors file if not)
                        if obj.get("emotion_label") != label or obj.get("emotion_polarity") != polarity_for_label(label):
                            issues.append(f"emotion mismatch vs target ({label})")

                        if issues:
                            f_err.write(json.dumps({
                                "patient_id": demo["patient_id"],
                                "window": [iso_z(start_dt), iso_z(end_dt)],
                                "issues": issues,
                                "target_emotion": label,
                                "object": obj
                            }, ensure_ascii=False) + "\n")
                            print(f"  {iso_z(start_dt)}–{iso_z(end_dt)}: ERR ({', '.join(issues)})")
                        else:
                            f_out.write(json.dumps(obj, ensure_ascii=False) + "\n")
                            print(f"  {iso_z(start_dt)}–{iso_z(end_dt)}: OK [{label}]")
                    except Exception as e:
                        f_err.write(json.dumps({
                            "patient_id": demo["patient_id"],
                            "window": [iso_z(start_dt), iso_z(end_dt)],
                            "target_emotion": label,
                            "error": str(e)
                        }, ensure_ascii=False) + "\n")
                        print(f"  {iso_z(start_dt)}–{iso_z(end_dt)}: EXC ({e})")

    # Print a small distribution summary to the console
    total = sum(counts.values())
    if total:
        print("\nEmotion targets sampled this run:")
        for label, _ in WEIGHTS:
            pct = (counts[label] / total) * 100
            print(f"  {label:8s} : {counts[label]:3d} ({pct:4.1f}%)")
