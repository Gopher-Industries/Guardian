#!/usr/bin/env python3
import os, json, argparse, numpy as np

os.environ["USE_TF"] = "0"
os.environ["USE_JAX"] = "0"

from datasets import Dataset
from transformers import (AutoTokenizer, AutoModelForSequenceClassification,
                          TrainingArguments, Trainer)
import evaluate

def load_jsonl_for_cls(path):
    rows=[]
    with open(path, "r", encoding="utf-8") as f:
        for l in f:
            d = json.loads(l)
            note = d.get("nursingNote","")
            cs = (d.get("clinicalSummary") or "").lower()
            label = 2 if "unwell" in cs else 1 if "uncomfortable" in cs else 0
            rows.append({"text": note, "label": label})
    return Dataset.from_list(rows)

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--train", default="data/processed/train.jsonl")
    ap.add_argument("--dev",   default="data/processed/dev.jsonl")
    ap.add_argument("--model", default="distilbert-base-uncased")
    ap.add_argument("--out",   default="models/hf_textcat")
    ap.add_argument("--epochs", type=int, default=3)
    ap.add_argument("--bsz", type=int, default=16)
    args = ap.parse_args()

    train = load_jsonl_for_cls(args.train)
    dev   = load_jsonl_for_cls(args.dev)

    tok = AutoTokenizer.from_pretrained(args.model)
    def tok_fn(ex): return tok(ex["text"], truncation=True)
    train = train.map(tok_fn, batched=True)
    dev   = dev.map(tok_fn, batched=True)

    model = AutoModelForSequenceClassification.from_pretrained(args.model, num_labels=3)
    acc = evaluate.load("accuracy")
    f1  = evaluate.load("f1")

    def compute_metrics(p):
        preds = np.argmax(p.predictions, axis=1)
        return {"accuracy": acc.compute(references=p.label_ids, predictions=preds)["accuracy"],
                "f1_macro": f1.compute(references=p.label_ids, predictions=preds, average="macro")["f1"]}

    args_tr = TrainingArguments(
        output_dir=args.out,
        per_device_train_batch_size=args.bsz,
        per_device_eval_batch_size=max(2*args.bsz, 32),
        num_train_epochs=args.epochs,
        eval_strategy="epoch",
        save_strategy="epoch",
        load_best_model_at_end=True,
        metric_for_best_model="f1_macro",
        greater_is_better=True,
        logging_steps=50
    )

    trainer = Trainer(model=model, args=args_tr, train_dataset=train, eval_dataset=dev,
                      tokenizer=tok, compute_metrics=compute_metrics)
    trainer.train()
    trainer.save_model(os.path.join(args.out, "model-best"))
    tok.save_pretrained(os.path.join(args.out, "model-best"))
    print("Saved:", os.path.join(args.out, "model-best"))

if __name__ == "__main__":
    main()
