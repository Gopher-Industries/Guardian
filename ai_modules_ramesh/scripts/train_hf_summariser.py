#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import os
import inspect
import numpy as np

from datasets import load_dataset
from transformers import (
    AutoTokenizer,
    AutoModelForSeq2SeqLM,
    DataCollatorForSeq2Seq,
    Seq2SeqTrainingArguments,
    Seq2SeqTrainer,
    set_seed,
)
import evaluate


def parse_args():
    ap = argparse.ArgumentParser(description="Train a HF summariser (Seq2Seq)")

    # Model & IO
    ap.add_argument("--model_name", default="google/mt5-small", type=str)
    ap.add_argument("--output_dir", default="models/hf_summariser/model-best", type=str)
    ap.add_argument("--train_file", required=True, type=str)
    ap.add_argument("--valid_file", required=True, type=str)
    ap.add_argument("--dataset_format", choices=["json", "csv"], default="json")

    # Columns
    ap.add_argument("--text_column", default="input_text", type=str)
    ap.add_argument("--summary_column", default="target_text", type=str)

    # Lengths
    ap.add_argument("--max_source_length", type=int, default=256)
    ap.add_argument("--max_target_length", type=int, default=64)

    # Training
    ap.add_argument("--train_batch_size", type=int, default=8)
    ap.add_argument("--eval_batch_size", type=int, default=8)
    ap.add_argument("--num_epochs", type=int, default=2)
    ap.add_argument("--learning_rate", type=float, default=5e-5)
    ap.add_argument("--weight_decay", type=float, default=0.0)
    ap.add_argument("--warmup_ratio", type=float, default=0.0)
    ap.add_argument("--logging_steps", type=int, default=50)

    # Strategies (may not exist in older transformers)
    ap.add_argument("--save_strategy", choices=["no", "steps", "epoch"], default="steps")
    ap.add_argument("--eval_strategy", choices=["no", "steps", "epoch"], default="steps")
    ap.add_argument("--eval_steps", type=int, default=100)
    ap.add_argument("--save_total_limit", type=int, default=2)

    # Generation
    ap.add_argument("--generation_max_length", type=int, default=64)
    ap.add_argument("--generation_num_beams", type=int, default=4)

    # Misc
    ap.add_argument("--seed", type=int, default=42)
    ap.add_argument("--fp16", action="store_true")
    ap.add_argument("--bf16", action="store_true")

    return ap.parse_args()


def build_training_args(args):
    """
    Build Seq2SeqTrainingArguments but only pass kwargs supported by the local transformers version.
    """
    # Desired kwargs
    wanted = dict(
        output_dir=args.output_dir,
        per_device_train_batch_size=args.train_batch_size,
        per_device_eval_batch_size=args.eval_batch_size,
        gradient_accumulation_steps=1,
        learning_rate=args.learning_rate,
        weight_decay=args.weight_decay,
        warmup_ratio=args.warmup_ratio,
        num_train_epochs=args.num_epochs,
        logging_steps=args.logging_steps,
        predict_with_generate=True,
        generation_max_length=args.generation_max_length,
        generation_num_beams=args.generation_num_beams,
        load_best_model_at_end=True,
        metric_for_best_model="rougeLsum",
        greater_is_better=True,
        fp16=args.fp16,
        bf16=args.bf16,
        report_to="none",
        save_total_limit=args.save_total_limit,
        # These may not exist on older versions:
        evaluation_strategy=args.eval_strategy,
        save_strategy=args.save_strategy,
        eval_steps=args.eval_steps if args.eval_strategy == "steps" else None,
        save_steps=args.eval_steps if args.save_strategy == "steps" else None,
    )

    # Keep only kwargs that exist in local signature
    sig = inspect.signature(Seq2SeqTrainingArguments.__init__)
    allowed = set(sig.parameters.keys())
    filtered = {k: v for k, v in wanted.items() if (k in allowed and v is not None)}

    try:
        return Seq2SeqTrainingArguments(**filtered)
    except TypeError:
        # As a fallback, drop strategy keys entirely (older HF)
        for k in ("evaluation_strategy", "save_strategy", "eval_steps", "save_steps"):
            filtered.pop(k, None)
        return Seq2SeqTrainingArguments(**filtered)


def main():
    args = parse_args()
    set_seed(args.seed)
    os.makedirs(args.output_dir, exist_ok=True)

    # Load data
    data_files = {"train": args.train_file, "validation": args.valid_file}
    if args.dataset_format == "json":
        ds = load_dataset("json", data_files=data_files)
    else:
        ds = load_dataset("csv", data_files=data_files)

    # Tokenizer & model
    tokenizer = AutoTokenizer.from_pretrained(args.model_name, use_fast=True)
    # Use safetensors to avoid torch.load(.bin) restriction (CVE-2025-32434)
    # Use dtype="auto" if supported, else fallback to torch_dtype="auto"
    model_kwargs = dict(use_safetensors=True, low_cpu_mem_usage=True)
    try:
        model = AutoModelForSeq2SeqLM.from_pretrained(args.model_name, dtype="auto", **model_kwargs)
    except TypeError:
        model = AutoModelForSeq2SeqLM.from_pretrained(args.model_name, torch_dtype="auto", **model_kwargs)

    text_col = args.text_column
    sum_col = args.summary_column

    def preprocess(batch):
        inputs = batch[text_col]
        targets = batch[sum_col]
        model_inputs = tokenizer(inputs, max_length=args.max_source_length, truncation=True)
        # label tokenization (compatible with both old/new APIs)
        labels = tokenizer(text_target=targets, max_length=args.max_target_length, truncation=True)
        model_inputs["labels"] = labels["input_ids"]
        return model_inputs

    tokenized_train = ds["train"].map(preprocess, batched=True, remove_columns=ds["train"].column_names)
    tokenized_eval = ds["validation"].map(preprocess, batched=True, remove_columns=ds["validation"].column_names)

    data_collator = DataCollatorForSeq2Seq(tokenizer=tokenizer, model=model)

    # Metrics
    rouge = evaluate.load("rouge")

    def postprocess_text(preds, labels):
        preds = [p.strip() for p in preds]
        labels = [l.strip() for l in labels]
        return preds, labels

    def compute_metrics(eval_pred):
        preds, labels = eval_pred
        if isinstance(preds, tuple):
            preds = preds[0]
        decoded_preds = tokenizer.batch_decode(preds, skip_special_tokens=True)
        labels = np.where(labels != -100, labels, tokenizer.pad_token_id)
        decoded_labels = tokenizer.batch_decode(labels, skip_special_tokens=True)
        decoded_preds, decoded_labels = postprocess_text(decoded_preds, decoded_labels)
        result = rouge.compute(predictions=decoded_preds, references=decoded_labels, use_stemmer=True)
        return {
            "rouge1": result["rouge1"],
            "rouge2": result.get("rouge2", 0.0),
            "rougeL": result["rougeL"],
            "rougeLsum": result["rougeLsum"],
        }

    training_args = build_training_args(args)

    trainer = Seq2SeqTrainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_train,
        eval_dataset=tokenized_eval,
        tokenizer=tokenizer,
        data_collator=data_collator,
        compute_metrics=compute_metrics,
    )

    # Train
    trainer.train()

    # Try evaluation at end (covers older HF where in-flight eval wasn't configured)
    try:
        metrics = trainer.evaluate()
        print("Final eval metrics:", metrics)
    except Exception as e:
        print("Eval skipped at end due to:", repr(e))

    # Save best/final
    trainer.save_model(args.output_dir)
    tokenizer.save_pretrained(args.output_dir)
    print("✅ Training complete. Model saved to:", args.output_dir)


if __name__ == "__main__":
    os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
    main()
