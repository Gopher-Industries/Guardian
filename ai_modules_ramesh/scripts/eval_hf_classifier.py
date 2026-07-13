#!/usr/bin/env python3
import os
os.environ["USE_TF"]="0"; os.environ["USE_JAX"]="0"; os.environ["TRANSFORMERS_NO_TF"]="1"

import json, torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from sklearn.metrics import classification_report

labels = ["Normal","Uncomfortable","Sick"]
model_dir = "models/hf_textcat/model-best"
tok = AutoTokenizer.from_pretrained(model_dir, local_files_only=True)
model = AutoModelForSequenceClassification.from_pretrained(model_dir, local_files_only=True)

def load_jsonl(path):
    X,y=[],[]
    with open(path,"r",encoding="utf-8") as f:
        for l in f:
            d=json.loads(l); X.append(d["nursingNote"])
            cs=(d.get("clinicalSummary") or "").lower()
            y.append(2 if "unwell" in cs else 1 if "uncomfortable" in cs else 0)
    return X,y

X,y = load_jsonl("data/processed/test.jsonl")
preds=[]
for t in X:
    x = tok(t, return_tensors="pt", truncation=True)
    with torch.no_grad():
        logits = model(**x).logits
    preds.append(int(logits.argmax(-1)))
print(classification_report(y, preds, target_names=labels, digits=3))
