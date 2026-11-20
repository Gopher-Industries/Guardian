#!/usr/bin/env python3
import os, json, random, argparse
from collections import defaultdict

def read_jsonl(p):
    with open(p, "r", encoding="utf-8") as f:
        for line in f: yield json.loads(line)

def write_jsonl(items, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        for d in items: f.write(json.dumps(d, ensure_ascii=False) + "\n")

ap = argparse.ArgumentParser()
ap.add_argument("--input", default="data/raw/ai_behaviour_synthetic.jsonl")
ap.add_argument("--outdir", default="data/processed")
ap.add_argument("--seed", type=int, default=42)
ap.add_argument("--splits", type=str, default="0.8,0.1,0.1")
args = ap.parse_args()

random.seed(args.seed)
p_train, p_dev, p_test = map(float, args.splits.split(","))
items = list(read_jsonl(args.input))

# stratify by a coarse label using vitals (Normal/Uncomfortable/Sick)
by_state = defaultdict(list)
for d in items:
    # infer from clinicalSummary keyword (since generator wrote it)
    if "unwell" in d["clinicalSummary"]: state = "Sick"
    elif "uncomfortable" in d["clinicalSummary"]: state = "Uncomfortable"
    else: state = "Normal"
    by_state[state].append(d)

train, dev, test = [], [], []
for state, arr in by_state.items():
    random.shuffle(arr)
    n = len(arr)
    n_train, n_dev = int(n*p_train), int(n*p_dev)
    train += arr[:n_train]
    dev   += arr[n_train:n_train+n_dev]
    test  += arr[n_train+n_dev:]

write_jsonl(train, f"{args.outdir}/train.jsonl")
write_jsonl(dev,   f"{args.outdir}/dev.jsonl")
write_jsonl(test,  f"{args.outdir}/test.jsonl")

print(f"Wrote: {len(train)} train, {len(dev)} dev, {len(test)} test")
