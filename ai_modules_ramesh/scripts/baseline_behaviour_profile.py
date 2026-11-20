#!/usr/bin/env python3
import json, argparse, os
from statistics import mean

def read_jsonl(p):
    with open(p, "r", encoding="utf-8") as f:
        for line in f: yield json.loads(line)

def rb_state(v):
    hr=v["heartRate"]["value"]; spo2=v["spo2"]["value"]
    t=v["temperature"]["value"]; bp=v["bloodPressure"]
    sbp, dbp = bp["systolic"], bp["diastolic"]
    # simple thresholds
    if hr>=100 or spo2<93 or t>=38.0 or sbp<95 or sbp>=150 or dbp<60 or dbp>=95:
        return "Sick"
    # light discomfort if borderline
    if 95<=spo2<94 or 37.5<=t<38.0 or 140<=sbp<150 or 90<=dbp<95:
        return "Uncomfortable"
    return "Normal"

def summarise(d):
    v, a = d["vitals"], d["adls"]
    state = rb_state(v)
    vitals = f"HR {v['heartRate']['value']} bpm, SpO2 {v['spo2']['value']}%, Temp {v['temperature']['value']}°C, BP {v['bloodPressure']['systolic']}/{v['bloodPressure']['diastolic']}."
    adl = f"Steps {a['stepsTaken']}, sleep {a['sleepHours']}h, water {a['waterIntakeMl']}ml, mealsSkipped {a['mealsSkipped']}."
    tail = "Stable overall." if state=="Normal" else ("Monitor closely." if state=="Uncomfortable" else "Escalate observation.")
    return state, f"{vitals} {adl} {tail}"

ap = argparse.ArgumentParser()
ap.add_argument("--input", default="data/processed/dev.jsonl")
ap.add_argument("--out", default="data/exports/baseline_profiles.jsonl")
args = ap.parse_args()

os.makedirs(os.path.dirname(args.out), exist_ok=True)
out = open(args.out, "w", encoding="utf-8")
n=0
for d in read_jsonl(args.input):
    state, summary = summarise(d)
    d_out = {"patientId": d["patientId"], "observationStart": d["observationStart"],
             "rbState": state, "rbSummary": summary}
    out.write(json.dumps(d_out, ensure_ascii=False)+"\n")
    n+=1
out.close()
print(f"Wrote baseline profiles: {args.out} ({n} rows)")
