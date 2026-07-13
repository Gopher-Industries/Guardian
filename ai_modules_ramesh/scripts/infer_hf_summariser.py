#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import json
import os
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM


def build_prompt(raw_text: str, use_t5_prefix: bool = True) -> str:
    """
    T5/mT5 often behaves better with a 'summarize:' prefix.
    We'll add it in front of whatever text you pass in unless disabled.
    """
    txt = raw_text.strip()
    if use_t5_prefix and not txt.lower().startswith("summarize:"):
        txt = "summarize: " + txt
    return txt


def main():
    parser = argparse.ArgumentParser(description="Run inference with a trained summariser model")
    parser.add_argument("--model", required=True, help="Path to trained summariser model")
    parser.add_argument("--text", type=str, help="Single input text to summarise")
    parser.add_argument("--jsonl", type=str, help="Path to JSONL with 'input_text' (and optional 'target_text')")
    parser.add_argument("--out_csv", type=str, help="Save batch results to CSV")
    parser.add_argument("--no_t5_prefix", action="store_true", help="Do NOT prepend 'summarize:' prefix")
    parser.add_argument("--max_new_tokens", type=int, default=64)
    parser.add_argument("--num_beams", type=int, default=4)
    parser.add_argument("--min_new_tokens", type=int, default=8)
    parser.add_argument("--no_repeat_ngram_size", type=int, default=3)
    args = parser.parse_args()

    tokenizer = AutoTokenizer.from_pretrained(args.model, use_fast=True)
    model = AutoModelForSeq2SeqLM.from_pretrained(
        args.model,
        use_safetensors=True,
        dtype="auto"  # newer kw; ignored by older versions
    )

    # Try to build a denylist for T5 sentinels <extra_id_0..99>.
    # Only pass it to generate() if we actually resolve real IDs.
    extra_tokens = [f"<extra_id_{i}>" for i in range(100)]
    ids = tokenizer.convert_tokens_to_ids(extra_tokens)
    bad_words_ids = [[i] for i in ids if isinstance(i, int) and i >= 0 and i != tokenizer.unk_token_id]
    use_bad_words = len(bad_words_ids) > 0

    import re
    SENTINEL_RE = re.compile(r"<extra_id_\d+>")
    LEADING_NOISE_RE = re.compile(r"^(briefly:|summary:|summarize:)\s*", flags=re.I)

    def summarise(raw_text: str) -> str:
        # Remove any trailing “Summarise briefly:” from the user text before we build the prompt
        cleaned = re.sub(r"(?im)\n?summari[sz]e?\s+briefly:?\s*$", "", raw_text).strip()

        # T5-style prefix
        prompt = build_prompt(cleaned, use_t5_prefix=not args.no_t5_prefix)

        enc = tokenizer(prompt, return_tensors="pt", truncation=True, max_length=512)
        out = model.generate(
            **enc,
            max_new_tokens=args.max_new_tokens,
            min_new_tokens=args.min_new_tokens,
            num_beams=args.num_beams,
            no_repeat_ngram_size=args.no_repeat_ngram_size,
            repetition_penalty=1.1,
            length_penalty=1.0,
            early_stopping=True,
            decoder_start_token_id=tokenizer.pad_token_id,
            eos_token_id=tokenizer.eos_token_id,
            pad_token_id=tokenizer.pad_token_id,
        )
        text = tokenizer.decode(out[0], skip_special_tokens=True)

        # after decode
        text = SENTINEL_RE.sub("", text)
        text = LEADING_NOISE_RE.sub("", text)
        # NEW: strip leading punctuation / casing artifacts
        text = text.lstrip(" .,:;-").strip()
        # normalize spaces and ensure trailing period
        text = " ".join(text.split())
        if text and text[-1] not in ".!?":
            text += "."
        return text



    # Single text
    if args.text:
        print("Input:\n", args.text)
        print("\nSummary:\n", summarise(args.text))

    # Batch JSONL
    if args.jsonl:
        rows = []
        with open(args.jsonl, "r", encoding="utf-8") as f:
            for line in f:
                if not line.strip():
                    continue
                d = json.loads(line)
                inp = d.get("input_text", "")
                tgt = d.get("target_text", "")
                pred = summarise(inp)
                rows.append((inp, tgt, pred))

        if args.out_csv:
            import csv
            with open(args.out_csv, "w", newline="", encoding="utf-8") as g:
                w = csv.writer(g)
                w.writerow(["input_text", "target_text", "prediction"])
                w.writerows(rows)
            print(f"✅ Saved predictions to {args.out_csv}")
        else:
            for inp, tgt, pred in rows[:5]:
                print("\nInput:", inp)
                if tgt:
                    print("Target:", tgt)
                print("Prediction:", pred)


if __name__ == "__main__":
    os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
    main()
