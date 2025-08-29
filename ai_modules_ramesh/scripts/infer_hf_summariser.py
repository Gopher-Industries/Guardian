#!/usr/bin/env python3
import sys, json

import os
os.environ["USE_TF"] = "0"
os.environ["USE_JAX"] = "0"

from transformers import AutoTokenizer, AutoModelForSeq2SeqLM

model_dir = "models/hf_summariser/model-best"
tok = AutoTokenizer.from_pretrained(model_dir)
model = AutoModelForSeq2SeqLM.from_pretrained(model_dir)

if len(sys.argv) > 1:
    # free-form single note + vitals (already fused)
    inp = sys.argv[1]
else:
    # demo: build prompt from a small schema snippet
    d = {
      "nursingNote": "Patient is clinically unwell with cough and dizziness.",
      "vitals": {"heartRate":{"value":110},"spo2":{"value":90},"temperature":{"value":38.6},
                 "bloodPressure":{"systolic":165,"diastolic":102}}
    }
    v=d["vitals"]
    inp = (f"Note: {d['nursingNote']}\n"
           f"Vitals: HR={v['heartRate']['value']}, SpO2={v['spo2']['value']}, "
           f"Temp={v['temperature']['value']}°C, BP={v['bloodPressure']['systolic']}/{v['bloodPressure']['diastolic']}\n"
           f"Summarise briefly:")

x = tok(inp, return_tensors="pt", truncation=True)
y = model.generate(**x, max_new_tokens=64)
print(tok.decode(y[0], skip_special_tokens=True))
