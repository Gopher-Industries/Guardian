#!/usr/bin/env python3
import sys, torch
import os
os.environ["USE_TF"] = "0"
os.environ["USE_JAX"] = "0"

from transformers import AutoTokenizer, AutoModelForSequenceClassification

label_map = {0:"Normal", 1:"Uncomfortable", 2:"Sick"}
model_dir = "models/hf_textcat/model-best"

tok = AutoTokenizer.from_pretrained(model_dir, local_files_only=True)
model = AutoModelForSequenceClassification.from_pretrained(model_dir, local_files_only=True)

text = " ".join(sys.argv[1:]) or "Patient remains stable. HR 78 bpm, SpO2 98%."
inputs = tok(text, return_tensors="pt", truncation=True)
with torch.no_grad():
    logits = model(**inputs).logits
probs = logits.softmax(-1).squeeze().tolist()
pred = label_map[int(logits.argmax(-1))]
print(text)
print({"Normal": round(probs[0],3), "Uncomfortable": round(probs[1],3), "Sick": round(probs[2],3)})
print("pred:", pred)
