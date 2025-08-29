#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Train a Hugging Face seq2seq summariser with ROUGE evaluation.

Works with transformers 4.55.x.
- Uses Seq2SeqTrainingArguments (so predict_with_generate works)
- Uses tokenizer(..., text_target=...) to avoid deprecation warning
- Lets you pass dataset files/columns via CLI args to match your data
"""

import argparse
import os
from typing import Dict, Any

import numpy as np

from datasets import load_dataset, load_from_disk
from transformers import (
    AutoTokenizer,
    AutoModelForSeq2SeqLM,
    DataCollatorForSeq2Seq,
    Seq2SeqTrainer,
    Seq2SeqTrainingArguments,
)
import evaluate


def parse_args():
    p = argparse.ArgumentParser()
    # Model & output
    p.add_argument("--model_name", type=str, default="facebook/bart-base",
                   help="Seq2Seq model checkpoint (e.g., facebook/bart-base, google/pegasus-xsum, t5-small)")
    p.add_argument("--output_dir", type=str, default="./models/hf_summariser",
                   help="Where to save checkpoints")
    # Data inputs (choose either CSV/JSON files or a load_from_disk dir)
    p.add_argument("--train_file", type=str, default="", help="Path to train file (csv or json/jsonl)")
    p.add_argument("--valid_file", type=str, default="", help="Path to validation file (csv or json/jsonl)")
    p.add_argument("--dataset_format", type=str, default="csv", choices=["csv", "json"],
                   help="Format of input files if using train_file/valid_file")
    p.add_argument("--load_from_disk_dir", type=str, default="",
                   help="If set, will load a datasets.DatasetDict from this directory instead of files")
    # Column names
    p.add_argument("--text_column", type=str, default="text",
                   help="Source text column name")
    p.add_argument("--summary_column", type=str, default="summary",
                   help="Target summary column name")
    # Tokenisation lengths
    p.add_argument("--max_source_length", type=int, default=512)
    p.add_argument("--max_target_length", type=int, default=128)
    # Training hparams
    p.add_argument("--train_batch_size", type=int, default=8)
    p.add_argument("--eval_batch_size", type=int, default=8)
    p.add_argument("--num_epochs", type=float, default=3)
    p.add_argument("--logging_steps", type=int, default=50)
    p.add_argument("--save_strategy", type=str, default="epoch", choices=["no", "steps", "epoch"])
    p.add_argument("--eval_strategy", type=str, default="epoch", choices=["no", "steps", "epoch"])
    p.add_argument("--eval_steps", type=int, default=None,
                   help="Only used if eval_strategy=steps")
    p.add_argument("--generation_max_length", type=int, default=128)
    p.add_argument("--generation_num_beams", type=int, default=4)
    p.add_argument("--seed", type=int, default=42)
    return p.parse_args()


def load_data(args):
    """
    Returns a datasets.DatasetDict with 'train' and 'validation' splits.
    """
    if args.load_from_disk_dir:
        ds = load_from_disk(args.load_from_disk_dir)
        # Expect 'train' and 'validation' present
        if "validation" not in ds and "val" in ds:
            ds = ds.rename_columns({"val": "validation"})
        return ds

    if not args.train_file or not args.valid_file:
        raise ValueError("Provide --train_file and --valid_file, or use --load_from_disk_dir")

    if args.dataset_format == "csv":
        ds = load_dataset("csv", data_files={"train": args.train_file, "validation": args.valid_file})
    else:
        # json / jsonl
        ds = load_dataset("json", data_files={"train": args.train_file, "validation": args.valid_file})
    return ds


def build_preprocess(tokenizer, args):
    text_col = args.text_column
    summ_col = args.summary_column

    def preprocess(batch: Dict[str, Any]):
        # Inputs
        model_inputs = tokenizer(
            batch[text_col],
            max_length=args.max_source_length,
            truncation=True,
        )
        # Targets (no as_target_tokenizer; use text_target to avoid deprecation)
        with_target = tokenizer(
            batch[summ_col],
            max_length=args.max_target_length,
            truncation=True,
            text_target=True,
        )
        model_inputs["labels"] = with_target["input_ids"]
        return model_inputs

    return preprocess


def build_metrics(tokenizer):
    rouge = evaluate.load("rouge")  # requires `pip install rouge_score`

    def postprocess_text(preds, labels):
        # Decode IDs to strings; replace -100 with pad_token_id for labels before decoding
        preds = tokenizer.batch_decode(preds, skip_special_tokens=True)
        labels = tokenizer.batch_decode(labels, skip_special_tokens=True)
        # Strip
        preds = [p.strip() for p in preds]
        labels = [l.strip() for l in labels]
        return preds, labels

    def compute_metrics(eval_pred):
        preds, labels = eval_pred
        if isinstance(preds, tuple):
            preds = preds[0]
        # Replace -100 in the labels as we can't decode them
        labels = np.where(labels != -100, labels, tokenizer.pad_token_id)

        decoded_preds, decoded_labels = postprocess_text(preds, labels)
        result = rouge.compute(
            predictions=decoded_preds,
            references=decoded_labels,
            use_stemmer=True,
        )
        # Convert to percentages
        result = {k: round(v * 100, 4) for k, v in result.items()}
        return result

    return compute_metrics


def main():
    args = parse_args()

    tokenizer = AutoTokenizer.from_pretrained(args.model_name, use_fast=True)
    model = AutoModelForSeq2SeqLM.from_pretrained(args.model_name)

    ds = load_data(args)
    preprocess = build_preprocess(tokenizer, args)

    # Map/tokenise
    cols = [args.text_column, args.summary_column]
    tokenised = ds.map(preprocess, batched=True, remove_columns=[c for c in ds["train"].column_names if c in cols])

    # Data collator for seq2seq
    data_collator = DataCollatorForSeq2Seq(tokenizer=tokenizer, model=model)

    # Metrics
    compute_metrics = build_metrics(tokenizer)

    # Training args — note: your env exposes `eval_strategy` (not evaluation_strategy)
    targs = Seq2SeqTrainingArguments(
        output_dir=args.output_dir,
        per_device_train_batch_size=args.train_batch_size,
        per_device_eval_batch_size=args.eval_batch_size,
        num_train_epochs=args.num_epochs,
        logging_steps=args.logging_steps,
        save_strategy=args.save_strategy,
        eval_strategy=args.eval_strategy,
        eval_steps=args.eval_steps,
        predict_with_generate=True,
        generation_max_length=args.generation_max_length,
        generation_num_beams=args.generation_num_beams,
        load_best_model_at_end=True,
        metric_for_best_model="rougeLsum",
        greater_is_better=True,
        seed=args.seed,
        report_to=None,   # set to ["tensorboard"] if you want TB logs
    )

    trainer = Seq2SeqTrainer(
        model=model,
        args=targs,
        train_dataset=tokenised["train"],
        eval_dataset=tokenised["validation"],
        data_collator=data_collator,
        tokenizer=tokenizer,
        compute_metrics=compute_metrics,
    )

    trainer.train()
    metrics = trainer.evaluate()
    print("Eval metrics:", metrics)

    # Save final artefacts
    trainer.save_model(args.output_dir)
    tokenizer.save_pretrained(args.output_dir)


if __name__ == "__main__":
    main()
