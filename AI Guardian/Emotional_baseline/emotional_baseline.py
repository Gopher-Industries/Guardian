#!/usr/bin/env python3
# emotional_baseline.py
"""
Sprint 2 — NLP Feature Extraction & Baseline Training

- Strips label tokens twice (custom preprocessor + stopwords)
- Deduplicates texts after scrubbing
- Group-aware split (GroupShuffleSplit) + GroupKFold CV
- Features: BoW(1), TF-IDF(1–2), TF-IDF(1–3), optional lexicon counts
- Models: Logistic Regression & Linear SVM (class_weight="balanced")
- Tripwire: raises if any label token appears in vocabulary
- Saves: sprint2_results.csv, sprint2_cv_results.csv, sprint2_cv_summary.csv,
  confusion_matrix.png, feature_preview.txt, (optional) lexicon_preview.csv,
  sprint2_report.md, settings.json
"""

from __future__ import annotations
import argparse, json, re, sys, warnings
from collections import Counter
from pathlib import Path
from typing import Callable, Iterable, List, Tuple

import numpy as np
import pandas as pd
from scipy.sparse import csr_matrix, hstack

from sklearn.model_selection import GroupShuffleSplit, GroupKFold
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer, ENGLISH_STOP_WORDS
from sklearn.linear_model import LogisticRegression
from sklearn.svm import LinearSVC
from sklearn.metrics import accuracy_score, f1_score, classification_report, confusion_matrix
import sklearn

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

warnings.filterwarnings("ignore", category=UserWarning)
SEED = 42

# ---------- tiny lexicons ----------
POS_WORDS = {
    "good","great","excellent","happy","calm","relief","improved","better",
    "comfortable","normal","stable","smiling","cooperative","ok","fine","relaxed"
}
NEG_WORDS = {
    "bad","sad","angry","pain","fever","cough","dizzy","tired","weak","fatigue","anxious",
    "vomit","nausea","diarrhea","bleeding","distress","agitated","worse","uncomfortable",
    "sick","breathless","headache","crying","depressed"
}
EMOTION_LEX = {
    "anger":   {"angry","annoyed","furious","irate","mad","rage","frustrated"},
    "joy":     {"joy","happy","pleased","delighted","glad","cheerful","smiling"},
    "sadness": {"sad","down","unhappy","depressed","blue","tearful","crying"},
    "fear":    {"afraid","scared","fear","anxious","terrified","nervous","worried"},
    "surprise":{"surprised","startled","amazed","shocked","astonished","wow"},
    "disgust": {"disgust","disgusted","gross","nauseous","repulsed","sickened"},
}
EMOTION_KEYS = list(EMOTION_LEX.keys())

# ---------- leakage prevention ----------
DEFAULT_LABEL_WORDS = {
    "normal","normally","normality",
    "sick","sickness",
    "uncomfortable","uncomfort","comfort","comfortable",
}

def make_label_scrubber(extra_words: str | None) -> tuple[Callable[[str], str], list[str]]:
    words = set(DEFAULT_LABEL_WORDS)
    if extra_words:
        for w in re.split(r"[, ]+", extra_words.strip()):
            if w:
                words.add(w.lower())
    pat = re.compile(r"\b(" + "|".join(map(re.escape, sorted(words))) + r")\b")
    def strip_label_words(text: str) -> str:
        return pat.sub(" ", str(text).lower())
    return strip_label_words, sorted(words)

def assert_no_label_tokens(vec, label_tokens):
    vocab = set(vec.get_feature_names_out())
    leaks = sorted(vocab.intersection(set(label_tokens)))
    if leaks:
        raise RuntimeError(f"Label leakage detected in vocabulary: {leaks}")

# ---------- feature helpers ----------
def build_lexicon_features(texts: Iterable[str]) -> csr_matrix:
    rows = []
    for t in texts:
        toks = re.findall(r"[a-zA-Z']+", str(t).lower())
        tok_count = max(len(toks), 1)
        bag = Counter(toks)
        pos = sum(bag.get(w, 0) for w in POS_WORDS)
        neg = sum(bag.get(w, 0) for w in NEG_WORDS)
        emo_counts = [sum(bag.get(w, 0) for w in EMOTION_LEX[k]) for k in EMOTION_KEYS]
        emo_rates  = [c / tok_count for c in emo_counts]
        rows.append([pos, neg, pos/tok_count, neg/tok_count] + emo_counts + emo_rates)
    return csr_matrix(np.asarray(rows, dtype=float))

def plot_confusion(y_true, y_pred, labels, out_path: Path):
    cm = confusion_matrix(y_true, y_pred, labels=labels)
    plt.figure(figsize=(6, 5))
    plt.imshow(cm, interpolation="nearest")
    plt.title("Confusion Matrix (best model)")
    plt.xticks(range(len(labels)), labels, rotation=45, ha="right")
    plt.yticks(range(len(labels)), labels)
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            plt.text(j, i, cm[i, j], ha="center", va="center")
    plt.xlabel("Predicted"); plt.ylabel("True")
    plt.tight_layout(); plt.savefig(out_path); plt.close()

def preview_vectorizer(vec, X_sample, out_txt: Path, header: str):
    fn = np.array(vec.get_feature_names_out())
    if X_sample.shape[0] == 0: return
    nz = X_sample[0].nonzero()[1]
    vals = X_sample[0, nz].toarray().ravel()
    pairs = sorted(zip(fn[nz], vals), key=lambda x: -x[1])[:25]
    with open(out_txt, "a", encoding="utf-8") as f:
        f.write(f"\n--- {header} ---\n")
        for w, v in pairs:
            f.write(f"{w}: {v:.4f}\n")
        f.write(f"Vocab size: {len(fn)}\n")

def df_to_md(df: pd.DataFrame) -> str:
    try:
        return df.to_markdown(index=False)   # needs 'tabulate'; falls back if missing
    except Exception:
        return df.to_string(index=False)

# ---------- training ----------
def fit_and_score(Xtr, Xte, ytr, yte, feature_name: str):
    rows = []
    for name, model in [
        ("LogReg",   LogisticRegression(max_iter=2000, class_weight="balanced", solver="lbfgs")),
        ("LinearSVM", LinearSVC(class_weight="balanced")),
    ]:
        model.fit(Xtr, ytr)
        pred = model.predict(Xte)
        rows.append({
            "Features": feature_name, "Model": name,
            "Accuracy": accuracy_score(yte, pred),
            "F1_macro": f1_score(yte, pred, average="macro"),
            "_pred": pred, "_model": model,
        })
    return rows

def run_group_cv(texts: pd.Series, labels: pd.Series, groups: pd.Series,
                 vec_builders: List[tuple[str, Callable[[], object]]],
                 with_lexicon: bool, n_splits: int, preproc: Callable[[str], str],
                 stripped_tokens: list[str]) -> pd.DataFrame:
    uniq = int(pd.Series(groups).nunique())
    n_splits = max(2, min(n_splits, uniq))
    gkf = GroupKFold(n_splits=n_splits)
    rows = []
    for feat_name, make_vec in vec_builders:
        for fold, (tr, te) in enumerate(gkf.split(texts, labels, groups=groups), 1):
            Xtr_text, Xte_text = texts.iloc[tr], texts.iloc[te]
            ytr, yte = labels.iloc[tr], labels.iloc[te]
            vec = make_vec()
            Xtr = vec.fit_transform(Xtr_text)
            assert_no_label_tokens(vec, stripped_tokens)
            Xte = vec.transform(Xte_text)
            if with_lexicon:
                Xtr = hstack([Xtr, build_lexicon_features(Xtr_text.apply(preproc))]).tocsr()
                Xte = hstack([Xte, build_lexicon_features(Xte_text.apply(preproc))]).tocsr()
            for name, model in [
                ("LogReg",   LogisticRegression(max_iter=2000, class_weight="balanced", solver="lbfgs")),
                ("LinearSVM", LinearSVC(class_weight="balanced")),
            ]:
                model.fit(Xtr, ytr)
                pred = model.predict(Xte)
                rows.append({
                    "Fold": fold, "Features": feat_name, "Model": name,
                    "Accuracy": accuracy_score(yte, pred),
                    "F1_macro": f1_score(yte, pred, average="macro"),
                })
    return pd.DataFrame(rows)

# ---------- main ----------
def main():
    p = argparse.ArgumentParser()
    p.add_argument("--csv", required=True, help="Path to dataset CSV")
    p.add_argument("--text_col", default="text", help="Text column")
    p.add_argument("--label_col", default="label", help="Label column")
    p.add_argument("--group_col", default="", help="Optional grouping column (e.g., patient_id)")
    p.add_argument("--test_size", type=float, default=0.2)
    p.add_argument("--min_df", type=int, default=2)
    p.add_argument("--n_splits", type=int, default=5)
    p.add_argument("--with_lexicon", action="store_true")
    p.add_argument("--extra_label_words", default="", help="Comma/space separated extra tokens to strip")
    p.add_argument("--out_dir", default="sprint2_out")
    args = p.parse_args()

    out = Path(args.out_dir); out.mkdir(parents=True, exist_ok=True)

    # Load & basic checks
    df = pd.read_csv(args.csv)
    if args.text_col not in df.columns or args.label_col not in df.columns:
        print(f"Columns present: {list(df.columns)}", file=sys.stderr)
        sys.exit(f"Missing text_col='{args.text_col}' or label_col='{args.label_col}' in CSV")

    keep = [args.text_col, args.label_col]
    if args.group_col and args.group_col in df.columns:
        keep.append(args.group_col)
    data = df[keep].copy()

    data[args.text_col] = data[args.text_col].fillna("").astype(str)
    data = data[data[args.label_col].notna()]
    data[args.label_col] = data[args.label_col].astype(str)
    if data[args.label_col].nunique() < 2:
        sys.exit("Need at least 2 label classes.")

    # Scrubber + stopwords (LIST, not set)
    strip_label_words, stripped_set = make_label_scrubber(args.extra_label_words)
    custom_stop = list(ENGLISH_STOP_WORDS.union(stripped_set))

    # Normalize for dedup & grouping
    data["_text_norm"] = data[args.text_col].apply(strip_label_words)
    before = len(data)
    data = data.drop_duplicates(subset=["_text_norm"]).reset_index(drop=True)
    print(f"Deduped texts: {before} -> {len(data)}")

    # Groups
    if args.group_col and args.group_col in data.columns:
        groups = data[args.group_col].astype(str)
        group_note = f"groups by '{args.group_col}'"
    else:
        groups = data["_text_norm"]
        group_note = "groups by normalized text"
    groups = groups.astype("category").cat.codes

    # Group-aware single split
    gss = GroupShuffleSplit(n_splits=1, test_size=args.test_size, random_state=SEED)
    tr_idx, te_idx = next(gss.split(data[args.text_col], data[args.label_col], groups=groups))
    X_tr, X_te = data[args.text_col].iloc[tr_idx], data[args.text_col].iloc[te_idx]
    y_tr, y_te = data[args.label_col].iloc[tr_idx], data[args.label_col].iloc[te_idx]

    # Vectorizers
    bow = CountVectorizer(preprocessor=strip_label_words, lowercase=False,
                          stop_words=custom_stop, ngram_range=(1,1), min_df=args.min_df)
    tfidf12 = TfidfVectorizer(preprocessor=strip_label_words, lowercase=False,
                              stop_words=custom_stop, ngram_range=(1,2), min_df=args.min_df)
    tfidf13 = TfidfVectorizer(preprocessor=strip_label_words, lowercase=False,
                              stop_words=custom_stop, ngram_range=(1,3), min_df=args.min_df)

    # Feature preview file
    feat_prev = out / "feature_preview.txt"
    feat_prev.write_text("Sample nonzero features for first test doc\n", encoding="utf-8")

    results = []

    # A) BoW
    Xtr = bow.fit_transform(X_tr)
    assert_no_label_tokens(bow, stripped_set)
    Xte = bow.transform(X_te)
    preview_vectorizer(bow, Xte[:1], feat_prev, "BoW (1-gram)")
    if args.with_lexicon:
        Xtr = hstack([Xtr, build_lexicon_features(X_tr.apply(strip_label_words))]).tocsr()
        Xte = hstack([Xte, build_lexicon_features(X_te.apply(strip_label_words))]).tocsr()
    results += fit_and_score(Xtr, Xte, y_tr, y_te, f"BoW(1){' + lex' if args.with_lexicon else ''}")

    # B) TF-IDF (1–2)
    Xtr = tfidf12.fit_transform(X_tr)
    assert_no_label_tokens(tfidf12, stripped_set)
    Xte = tfidf12.transform(X_te)
    preview_vectorizer(tfidf12, Xte[:1], feat_prev, "TF-IDF (1–2)")
    if args.with_lexicon:
        Xtr = hstack([Xtr, build_lexicon_features(X_tr.apply(strip_label_words))]).tocsr()
        Xte = hstack([Xte, build_lexicon_features(X_te.apply(strip_label_words))]).tocsr()
    results += fit_and_score(Xtr, Xte, y_tr, y_te, f"TFIDF(1–2){' + lex' if args.with_lexicon else ''}")

    # C) TF-IDF (1–3)
    Xtr = tfidf13.fit_transform(X_tr)
    assert_no_label_tokens(tfidf13, stripped_set)
    Xte = tfidf13.transform(X_te)
    preview_vectorizer(tfidf13, Xte[:1], feat_prev, "TF-IDF (1–3)")
    if args.with_lexicon:
        Xtr = hstack([Xtr, build_lexicon_features(X_tr.apply(strip_label_words))]).tocsr()
        Xte = hstack([Xte, build_lexicon_features(X_te.apply(strip_label_words))]).tocsr()
    results += fit_and_score(Xtr, Xte, y_tr, y_te, f"TFIDF(1–3){' + lex' if args.with_lexicon else ''}")

    # Save single-split results
    res_df = pd.DataFrame([{
        "Features": r["Features"], "Model": r["Model"],
        "Accuracy": r["Accuracy"], "F1_macro": r["F1_macro"]
    } for r in results]).sort_values("F1_macro", ascending=False)
    res_df.to_csv(out / "sprint2_results.csv", index=False)

    # Best confusion matrix + report snippet
    best = max(results, key=lambda r: r["F1_macro"])
    labels_sorted = sorted(data[args.label_col].unique())
    print("\n=== Baseline Results (single split; sorted by Macro-F1) ===")
    print(res_df.to_string(index=False))
    print(f"\nBest: {best['Features']} + {best['Model']} | "
          f"Accuracy={best['Accuracy']:.3f} | F1_macro={best['F1_macro']:.3f}\n")
    print("=== Classification Report (Best) ===")
    print(classification_report(y_te, best["_pred"], labels=labels_sorted, zero_division=0))
    plot_confusion(y_te, best["_pred"], labels_sorted, out / "confusion_matrix.png")

    # Optional lexicon preview
    if args.with_lexicon and X_te.shape[0] > 0:
        lex = build_lexicon_features(X_te[:5].apply(strip_label_words))
        cols = ["pos_count","neg_count","pos_rate","neg_rate"] + \
               [f"{k}_count" for k in EMOTION_KEYS] + [f"{k}_rate" for k in EMOTION_KEYS]
        pd.DataFrame(np.asarray(lex.todense()), columns=cols).to_csv(out / "lexicon_preview.csv", index=False)

    # ---- 5-fold GroupKFold CV ----
    vec_builders = [
        ("TFIDF(1–2)" + (" + lex" if args.with_lexicon else ""),
         lambda: TfidfVectorizer(preprocessor=strip_label_words, lowercase=False,
                                 stop_words=custom_stop, ngram_range=(1,2), min_df=args.min_df)),
        ("TFIDF(1–3)" + (" + lex" if args.with_lexicon else ""),
         lambda: TfidfVectorizer(preprocessor=strip_label_words, lowercase=False,
                                 stop_words=custom_stop, ngram_range=(1,3), min_df=args.min_df)),
    ]
    cv_df = run_group_cv(
        texts=data[args.text_col].reset_index(drop=True),
        labels=data[args.label_col].reset_index(drop=True),
        groups=pd.Series(groups).reset_index(drop=True),
        vec_builders=vec_builders,
        with_lexicon=args.with_lexicon,
        n_splits=args.n_splits,
        preproc=strip_label_words,
        stripped_tokens=stripped_set
    )
    cv_df.to_csv(out / "sprint2_cv_results.csv", index=False)
    cv_agg = (cv_df
              .groupby(["Features","Model"], as_index=False)
              .agg(Accuracy_mean=("Accuracy","mean"),
                   Accuracy_std =("Accuracy","std"),
                   F1_mean      =("F1_macro","mean"),
                   F1_std       =("F1_macro","std"))
              .sort_values(["F1_mean"], ascending=False))
    cv_agg.to_csv(out / "sprint2_cv_summary.csv", index=False)

    # Markdown report
    md = []
    md.append("# Sprint 2: Basic NLP Features & Baselines (Leakage-Safe)\n")
    md.append(f"- **CSV:** {Path(args.csv).name}\n")
    md.append(f"- **Text column:** `{args.text_col}`   |   **Label column:** `{args.label_col}`\n")
    md.append(f"- **Groups:** {group_note}\n")
    md.append(f"- **Dedup:** exact duplicates removed after scrubbing\n")
    md.append(f"- **Leakage guard:** stripped tokens {sorted(set(stripped_set))}\n")
    md.append("\n## Feature extraction\n")
    md.append(f"- BoW (1-gram), min_df={args.min_df}\n")
    md.append("- TF-IDF with bigrams & trigrams (captures phrases like *not happy*, *very tired*).\n")
    if args.with_lexicon:
        md.append("- Lexicon counts: pos/neg + six emotions (anger, joy, sadness, fear, surprise, disgust).\n")
    md.append("\n## Baseline models\n- Logistic Regression (balanced)\n- Linear SVM (balanced)\n")
    md.append("\n## Single-split results (sorted by Macro-F1)\n\n")
    md.append(df_to_md(res_df)); md.append("\n\n")
    md.append(f"**Best (single split):** {best['Features']} + {best['Model']}  \n")
    md.append(f"Accuracy: **{best['Accuracy']:.3f}**   |   Macro-F1: **{best['F1_macro']:.3f}**\n")
    md.append("\n**Confusion matrix** saved to `confusion_matrix.png`.\n")
    md.append("\n## 5-fold CV (GroupKFold; mean ± std)\n\n")
    show = cv_agg.assign(
        Accuracy=lambda d: d["Accuracy_mean"].round(3).astype(str) + " ± " + d["Accuracy_std"].round(3).astype(str),
        F1_macro=lambda d: d["F1_mean"].round(3).astype(str) + " ± " + d["F1_std"].round(3).astype(str)
    )[["Features","Model","Accuracy","F1_macro"]]
    md.append(df_to_md(show)); md.append("\n")
    md.append("\n## Environment\n")
    md.append(f"- python: `{sys.executable}`\n")
    md.append(f"- numpy: {np.__version__} | pandas: {pd.__version__} | sklearn: {sklearn.__version__}\n")
    (out / "sprint2_report.md").write_text("\n".join(md), encoding="utf-8")

    # Settings for reproducibility
    settings = {
        "csv": str(Path(args.csv)),
        "text_col": args.text_col,
        "label_col": args.label_col,
        "group_col": args.group_col if args.group_col else None,
        "test_size": args.test_size,
        "min_df": args.min_df,
        "n_splits": args.n_splits,
        "with_lexicon": bool(args.with_lexicon),
        "extra_label_words": args.extra_label_words,
        "stripped_tokens_effective": stripped_set,
        "seed": SEED,
    }
    (out / "settings.json").write_text(json.dumps(settings, indent=2), encoding="utf-8")
    print(f"\nAll artifacts written to: {out.resolve()}\n")

if __name__ == "__main__":
    main()
