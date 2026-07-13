# Guardian Monitor AI - Phase 1 & 2

Run Order
# If not done yet:
python scripts/split_schema_jsonl.py

# Classifier
python scripts/train_hf_classifier.py
python scripts/infer_hf_classifier.py "Patient is uncomfortable; HR 98, SBP 148."

# Summariser
python scripts/train_hf_summariser.py
python scripts/infer_hf_summariser.py

# (Optional) Eval
python scripts/eval_hf_classifier.py
python scripts/eval_hf_summariser.py
