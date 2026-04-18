"""
Project Guardian Monitor — Emotional Tagging and Alert System
Deakin University / Gopher Industries — Capstone Prototype

Pipeline:
  1. Load synthetic nurse notes dataset
  2. Generate synthetic 6-hour vitals windows aligned to each note
  3. Engineer vitals features (stats + clinical flags)
  4. Train/Val/Test split
  5. Text model: TF-IDF (unigrams+bigrams) + One-vs-Rest Logistic Regression (numpy only)
  6. Vitals risk model: rule-based clinical scoring → Low/Medium/High
  7. Fusion layer: equal-weight text + vitals → final alert
  8. Evaluation: per-tag F1, macro F1, confusion tables
  9. Dashboard card examples

"""

import os, re, math, json, random
import numpy as np
import pandas as pd
from collections import Counter
from datetime import datetime

SEED = 42
np.random.seed(SEED)
random.seed(SEED)

OUT_DIR = "/Users/tanvir/Desktop/unisem 4/sit782/emotional tagging"
os.makedirs(OUT_DIR, exist_ok=True)

TAGS = ["calm", "anxious", "confused", "agitated",
        "pain_concern", "respiratory_distress_concern", "improvement_stable"]

CONCERNING_TAGS = ["anxious", "confused", "agitated",
                   "pain_concern", "respiratory_distress_concern"]

# ─────────────────────────────────────────────────────────────────────────────
# SECTION 1: UTILITIES
# ─────────────────────────────────────────────────────────────────────────────

def pao2_to_spo2_approx(pao2):
    """Rough PaO2→SpO2 approximation for synthetic vitals generation."""
    if pd.isna(pao2) or pao2 <= 0:
        return None
    if pao2 < 27:   return 50.0
    if pao2 < 40:   return round(80 + (pao2 - 27) * (88 - 80) / 13, 1)
    if pao2 < 60:   return round(88 + (pao2 - 40) * (92 - 88) / 20, 1)
    if pao2 < 80:   return round(92 + (pao2 - 60) * (96 - 92) / 20, 1)
    if pao2 < 100:  return round(96 + (pao2 - 80) * (98 - 96) / 20, 1)
    return min(99.0, round(98 + (pao2 - 100) * 0.005, 1))


def clamp(value, lo, hi):
    return max(lo, min(hi, value))


def sigmoid(z):
    return 1.0 / (1.0 + np.exp(-np.clip(z, -500, 500)))


def compute_f1(y_true, y_pred, zero_div=0.0):
    tp = np.sum((y_true == 1) & (y_pred == 1))
    fp = np.sum((y_true == 0) & (y_pred == 1))
    fn = np.sum((y_true == 1) & (y_pred == 0))
    precision = tp / (tp + fp) if (tp + fp) > 0 else zero_div
    recall    = tp / (tp + fn) if (tp + fn) > 0 else zero_div
    if precision + recall == 0:
        return zero_div
    return 2 * precision * recall / (precision + recall)


def compute_precision_recall(y_true, y_pred):
    tp = np.sum((y_true == 1) & (y_pred == 1))
    fp = np.sum((y_true == 0) & (y_pred == 1))
    fn = np.sum((y_true == 1) & (y_pred == 0))
    precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
    recall    = tp / (tp + fn) if (tp + fn) > 0 else 0.0
    return precision, recall


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 2: LOAD DATA
# ─────────────────────────────────────────────────────────────────────────────

print("=" * 70)
print("PROJECT GUARDIAN MONITOR — PIPELINE v1")
print("=" * 70)
print()
print("[1/9] Loading synthetic nurse notes dataset ...")

notes_path = "/Users/tanvir/Desktop/unisem 4/sit782/emotional tagging/synthetic_nurse_notes_dataset.csv"
df = pd.read_csv(notes_path)
df["anchor_time"] = pd.to_datetime(df["anchor_time"])

print(f"    Loaded {len(df)} rows, {df['subject_id'].nunique()} unique subjects")
print(f"    Columns: {list(df.columns)}")
print()

# Tag distribution
print("    Tag distribution:")
for t in TAGS:
    n = df[t].sum()
    print(f"      {t:<35} {n:4d} ({100*n/len(df):.1f}%)")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 3: SYNTHETIC VITALS GENERATION
# ─────────────────────────────────────────────────────────────────────────────

print("[2/9] Generating synthetic 6-hour vitals windows ...")

# Each note gets 12 vital-sign readings (every 30 min over 6 hours).
# Readings are generated to be clinically consistent with the note's tags
# and any available SpO2 / temperature from the CSV.

VITALS_COLS = ["HR", "RR", "SBP", "DBP", "MAP", "SpO2", "Temp", "GCS"]
N_READINGS  = 12   # one per 30 min
MINUTES     = list(range(-330, 30, 30))  # -330..0 relative to note time


def tag_vitals_params(row):
    """
    Return (mean, std) targets per vital based on clinical state encoded
    in the tags row. If multiple tags → blend worst-case signals.
    """
    # Base normals
    hr_m, hr_s     = 78, 10
    rr_m, rr_s     = 15, 2
    sbp_m, sbp_s   = 120, 12
    dbp_m, dbp_s   = 76, 8
    spo2_m, spo2_s = 97, 1.5
    temp_m, temp_s = 37.0, 0.3
    gcs_m, gcs_s   = 15, 0

    # Resolve SpO2 anchor from CSV columns
    if not pd.isna(row.get("spo2_latest", np.nan)) and row["spo2_latest"] > 0:
        spo2_m = clamp(float(row["spo2_latest"]), 70, 100)
        spo2_s = 1.5
    elif not pd.isna(row.get("pao2_latest", np.nan)) and row["pao2_latest"] > 0:
        est = pao2_to_spo2_approx(row["pao2_latest"])
        if est:
            spo2_m = clamp(est, 70, 100)
            spo2_s = 2.0

    # Resolve temp anchor
    if not pd.isna(row.get("temp_latest", np.nan)) and row["temp_latest"] > 0:
        temp_m = clamp(float(row["temp_latest"]), 34, 41)
        temp_s = 0.2

    # Modulate by tags
    if row.get("anxious", 0) == 1:
        hr_m   = max(hr_m, 90);   hr_s   = 12
        rr_m   = max(rr_m, 17);   rr_s   = 3

    if row.get("agitated", 0) == 1:
        hr_m   = max(hr_m, 95);   hr_s   = 14
        rr_m   = max(rr_m, 18);   rr_s   = 3
        sbp_m  = max(sbp_m, 128); sbp_s  = 14

    if row.get("pain_concern", 0) == 1:
        hr_m   = max(hr_m, 88);   hr_s   = 12
        rr_m   = max(rr_m, 17);   rr_s   = 3
        sbp_m  = max(sbp_m, 125); sbp_s  = 13

    if row.get("respiratory_distress_concern", 0) == 1:
        rr_m   = max(rr_m, 22);   rr_s   = 4
        hr_m   = max(hr_m, 95);   hr_s   = 13
        spo2_m = min(spo2_m, 92); spo2_s = 2.5

    if row.get("confused", 0) == 1:
        gcs_m  = 12;  gcs_s = 2
        hr_m   = max(hr_m, 82)

    if row.get("calm", 0) == 1 and sum(row[t] for t in CONCERNING_TAGS) == 0:
        hr_m   = 72;  hr_s  = 8
        rr_m   = 14;  rr_s  = 2
        spo2_m = max(spo2_m, 96)

    if row.get("improvement_stable", 0) == 1:
        # trend improving: anchor means slightly better, small std
        hr_s  = min(hr_s, 9)
        rr_s  = min(rr_s, 2)

    # Opioid medications → lower RR concern
    if row.get("morphine_recent", 0) == 1 or row.get("hydromorphone_recent", 0) == 1 or \
       row.get("fentanyl_recent", 0) == 1:
        rr_m = max(10, rr_m - 2)

    return {
        "HR":   (hr_m,   hr_s),
        "RR":   (rr_m,   rr_s),
        "SBP":  (sbp_m,  sbp_s),
        "DBP":  (dbp_m,  dbp_s),
        "SpO2": (spo2_m, spo2_s),
        "Temp": (temp_m, temp_s),
        "GCS":  (gcs_m,  gcs_s),
    }


def generate_vitals_window(row, rng):
    """
    Generate N_READINGS synthetic vitals for a note row.
    Returns a list of dicts, one per time point.
    """
    params  = tag_vitals_params(row)
    anchor  = row["anchor_time"]
    note_id = row["note_id"]
    subject = row["subject_id"]
    records = []

    # Pick random-walk direction for trending signals
    # If improvement → trend toward normal; if deteriorating → trend away
    trend_dir = 0.0
    if row.get("improvement_stable", 0) == 1:
        trend_dir = -0.5   # gradual improvement (values closer to normal by end)
    elif row.get("respiratory_distress_concern", 0) == 1:
        trend_dir = 0.3    # slight worsening earlier in window

    for i, minute_offset in enumerate(MINUTES):
        t = anchor + pd.Timedelta(minutes=minute_offset)
        rec = {"note_id": note_id, "subject_id": subject,
               "measurement_time": t, "minutes_before_note": -minute_offset}

        # The last reading (i=11) anchors to the note time
        # Earlier readings drift slightly
        frac = i / (N_READINGS - 1)   # 0 (earliest) → 1 (latest)

        for vital in ["HR", "RR", "SBP", "DBP", "SpO2", "Temp", "GCS"]:
            mu, sigma = params[vital]
            # Add a small trend component
            mu_adj = mu + trend_dir * (1 - frac) * sigma * 0.4
            val = rng.normal(mu_adj, sigma)

            # Clamp to physiologically plausible bounds
            bounds = {
                "HR":   (30, 220),
                "RR":   (6,  60),
                "SBP":  (60, 220),
                "DBP":  (30, 130),
                "SpO2": (70, 100),
                "Temp": (34, 42),
                "GCS":  (3,  15),
            }
            lo, hi = bounds[vital]
            val = clamp(round(val, 1), lo, hi)
            if vital == "GCS":
                val = int(round(val))
            rec[vital] = val

        # Derive MAP = DBP + (SBP-DBP)/3
        rec["MAP"] = round(rec["DBP"] + (rec["SBP"] - rec["DBP"]) / 3.0, 1)
        records.append(rec)

    return records


rng = np.random.default_rng(SEED)
all_vitals = []
for _, row in df.iterrows():
    records = generate_vitals_window(row, rng)
    all_vitals.extend(records)

vitals_df = pd.DataFrame(all_vitals)
vitals_df.to_csv(f"{OUT_DIR}/synthetic_vitals_timeseries.csv", index=False)

print(f"    Generated {len(vitals_df)} vitals readings for {len(df)} notes")
print(f"    Vitals columns: {[c for c in vitals_df.columns if c not in ['note_id','subject_id','measurement_time']]}")
print(f"    Saved → synthetic_vitals_timeseries.csv")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 4: VITALS FEATURE ENGINEERING
# ─────────────────────────────────────────────────────────────────────────────

print("[3/9] Engineering 6-hour vitals window features ...")

VITAL_SIGNALS = ["HR", "RR", "SBP", "DBP", "MAP", "SpO2", "Temp", "GCS"]


def compute_vitals_features(group_df):
    """
    Given all 12 readings for one note, compute per-vital summary statistics
    and clinical flags. Returns a flat dict.
    """
    feats = {}

    for v in VITAL_SIGNALS:
        vals = group_df[v].dropna().values
        if len(vals) == 0:
            feats[f"{v}_latest"] = np.nan
            feats[f"{v}_mean"]   = np.nan
            feats[f"{v}_min"]    = np.nan
            feats[f"{v}_max"]    = np.nan
            feats[f"{v}_std"]    = np.nan
            feats[f"{v}_range"]  = np.nan
            feats[f"{v}_trend"]  = np.nan
        else:
            feats[f"{v}_latest"] = vals[-1]
            feats[f"{v}_mean"]   = round(np.mean(vals), 2)
            feats[f"{v}_min"]    = np.min(vals)
            feats[f"{v}_max"]    = np.max(vals)
            feats[f"{v}_std"]    = round(np.std(vals), 2)
            feats[f"{v}_range"]  = round(np.max(vals) - np.min(vals), 2)
            # trend = last value minus first value (positive = worsening if high is bad)
            feats[f"{v}_trend"]  = round(float(vals[-1]) - float(vals[0]), 2)

    # ── Clinical flags ─────────────────────────────────────────────────────
    hr_l  = feats.get("HR_latest",  80)
    rr_l  = feats.get("RR_latest",  15)
    sbp_l = feats.get("SBP_latest", 120)
    dbp_l = feats.get("DBP_latest", 76)
    spo2_l= feats.get("SpO2_latest",97)
    temp_l= feats.get("Temp_latest",37)
    hr_m  = feats.get("HR_mean",    80)

    feats["flag_tachycardia"]   = int(hr_l  > 100)
    feats["flag_bradycardia"]   = int(hr_l  < 60)
    feats["flag_hypotension"]   = int(sbp_l < 90)
    feats["flag_hypertension"]  = int(sbp_l > 140)
    feats["flag_fever"]         = int(temp_l > 38.0)
    feats["flag_hypothermia"]   = int(temp_l < 36.0)
    feats["flag_low_spo2"]      = int(spo2_l < 94)
    feats["flag_high_rr"]       = int(rr_l   > 20)

    # ── Combination flags ──────────────────────────────────────────────────
    feats["flag_resp_combo"]     = int(feats["flag_low_spo2"] and feats["flag_high_rr"])
    feats["flag_hr_fever"]       = int(feats["flag_tachycardia"] and feats["flag_fever"])
    feats["flag_shock_pattern"]  = int(feats["flag_hypotension"] and feats["flag_tachycardia"])

    return feats


# Group by note_id and compute features
feature_rows = []
for note_id, grp in vitals_df.groupby("note_id"):
    feats = compute_vitals_features(grp.sort_values("measurement_time"))
    feats["note_id"] = note_id
    feature_rows.append(feats)

feat_df = pd.DataFrame(feature_rows)

# Merge with original notes df
merged_df = df.merge(feat_df, on="note_id", how="left")

print(f"    Computed {len(feat_df.columns)-1} vitals features per note")
print(f"    Merged feature matrix shape: {merged_df.shape}")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 5: TRAIN / VAL / TEST SPLIT
# ─────────────────────────────────────────────────────────────────────────────

print("[4/9] Creating train/validation/test split (60/20/20) ...")

idx = np.arange(len(merged_df))
np.random.shuffle(idx)

n      = len(idx)
n_tr   = int(0.60 * n)
n_val  = int(0.20 * n)

tr_idx   = idx[:n_tr]
val_idx  = idx[n_tr: n_tr + n_val]
test_idx = idx[n_tr + n_val:]

df_tr   = merged_df.iloc[tr_idx].reset_index(drop=True)
df_val  = merged_df.iloc[val_idx].reset_index(drop=True)
df_test = merged_df.iloc[test_idx].reset_index(drop=True)

print(f"    Train: {len(df_tr)} | Val: {len(df_val)} | Test: {len(df_test)}")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 6: TEXT MODEL — TF-IDF + OvR LOGISTIC REGRESSION (numpy)
# ─────────────────────────────────────────────────────────────────────────────

print("[5/9] Building text model (TF-IDF + OvR Logistic Regression) ...")

# ── Text cleaning ──────────────────────────────────────────────────────────

def clean_text(text):
    text = str(text).lower()
    text = re.sub(r"[^\w\s]", "", text)
    return text


def tokenize(text, cleaned=True):
    if cleaned:
        text = clean_text(text)
    return text.split()


def get_ngrams(tokens, n_max=2):
    """Return unigrams + bigrams."""
    ngrams = list(tokens)
    if n_max >= 2:
        for i in range(len(tokens) - 1):
            ngrams.append(f"{tokens[i]}__{tokens[i+1]}")
    return ngrams


# ── Vocabulary builder ─────────────────────────────────────────────────────

def build_vocab(texts, min_df=2, max_features=5000):
    df_counts = Counter()
    for text in texts:
        tokens = get_ngrams(tokenize(text))
        for t in set(tokens):
            df_counts[t] += 1
    vocab = {term: idx for idx, (term, _) in enumerate(
        sorted([(t, c) for t, c in df_counts.items() if c >= min_df],
               key=lambda x: -x[1])[:max_features]
    )}
    return vocab


# ── TF-IDF vectorizer ──────────────────────────────────────────────────────

class TFIDFVectorizer:
    def __init__(self, min_df=2, max_features=5000):
        self.min_df = min_df
        self.max_features = max_features
        self.vocab = {}
        self.idf   = {}

    def fit(self, texts):
        self.vocab = build_vocab(texts, self.min_df, self.max_features)
        n = len(texts)
        df_counts = Counter()
        for text in texts:
            tokens = set(get_ngrams(tokenize(text)))
            for t in tokens:
                if t in self.vocab:
                    df_counts[t] += 1
        for term, idx in self.vocab.items():
            df = df_counts.get(term, 0)
            self.idf[term] = math.log((n + 1) / (df + 1)) + 1   # smooth IDF
        return self

    def transform(self, texts):
        X = np.zeros((len(texts), len(self.vocab)), dtype=np.float32)
        for i, text in enumerate(texts):
            tokens = get_ngrams(tokenize(text))
            n_tokens = len(tokens) if tokens else 1
            tf_counts = Counter(tokens)
            for term, count in tf_counts.items():
                if term in self.vocab:
                    tf = count / n_tokens
                    X[i, self.vocab[term]] = tf * self.idf.get(term, 1.0)
        # L2 normalise rows
        norms = np.linalg.norm(X, axis=1, keepdims=True)
        norms[norms == 0] = 1.0
        X = X / norms
        return X

    def fit_transform(self, texts):
        return self.fit(texts).transform(texts)


# ── Logistic Regression (numpy, binary) ───────────────────────────────────

class LogisticRegressionNP:
    """
    Binary logistic regression trained with mini-batch gradient descent.
    Supports balanced class weights for imbalanced data.
    """

    def __init__(self, lr=0.05, n_iter=600, C=1.0, batch_size=64, tol=1e-5):
        self.lr         = lr
        self.n_iter     = n_iter
        self.C          = C           # inverse regularisation strength
        self.batch_size = batch_size
        self.tol        = tol
        self.w          = None
        self.b          = 0.0

    def _class_weights(self, y):
        n_pos = y.sum()
        n_neg = len(y) - n_pos
        if n_pos == 0 or n_neg == 0:
            return np.ones(len(y))
        w_pos = len(y) / (2 * n_pos)
        w_neg = len(y) / (2 * n_neg)
        return np.where(y == 1, w_pos, w_neg)

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d, dtype=np.float64)
        self.b = 0.0
        weights = self._class_weights(y)
        prev_loss = np.inf

        for epoch in range(self.n_iter):
            perm = np.random.permutation(n)
            epoch_loss = 0.0

            for start in range(0, n, self.batch_size):
                idx   = perm[start: start + self.batch_size]
                Xb    = X[idx].astype(np.float64)
                yb    = y[idx]
                wb    = weights[idx]
                m     = len(idx)

                z    = Xb @ self.w + self.b
                pred = sigmoid(z)
                err  = (pred - yb) * wb     # weighted error

                dw = (Xb.T @ err) / m + self.w / (self.C * n)
                db = np.mean(err)

                self.w -= self.lr * dw
                self.b -= self.lr * db

                # batch loss (weighted cross-entropy)
                eps = 1e-9
                loss = -np.mean(wb * (yb * np.log(pred + eps) +
                                       (1 - yb) * np.log(1 - pred + eps)))
                epoch_loss += loss

            if abs(prev_loss - epoch_loss) < self.tol:
                break
            prev_loss = epoch_loss

        return self

    def predict_proba(self, X):
        return sigmoid(X.astype(np.float64) @ self.w + self.b)

    def predict(self, X, threshold=0.5):
        return (self.predict_proba(X) >= threshold).astype(int)


# ── One-vs-Rest wrapper ────────────────────────────────────────────────────

class OvRTextClassifier:
    def __init__(self, tags, **lr_kwargs):
        self.tags        = tags
        self.classifiers = {t: LogisticRegressionNP(**lr_kwargs) for t in tags}
        self.thresholds  = {t: 0.5 for t in tags}

    def fit(self, X, label_df):
        for tag in self.tags:
            y = label_df[tag].values
            if y.sum() > 0:
                self.classifiers[tag].fit(X, y)
        return self

    def predict_proba_dict(self, X):
        return {t: self.classifiers[t].predict_proba(X) for t in self.tags}

    def tune_thresholds(self, X, label_df):
        """Grid-search threshold per tag on validation set to maximise F1."""
        probs = self.predict_proba_dict(X)
        for tag in self.tags:
            y_true = label_df[tag].values
            if y_true.sum() == 0:
                continue
            best_t, best_f1 = 0.5, 0.0
            for t in np.arange(0.10, 0.91, 0.05):
                f1 = compute_f1(y_true, (probs[tag] >= t).astype(int))
                if f1 > best_f1:
                    best_f1, best_t = f1, float(t)
            self.thresholds[tag] = best_t
        return self

    def predict(self, X):
        probs = self.predict_proba_dict(X)
        return {t: (probs[t] >= self.thresholds[t]).astype(int) for t in self.tags}


# ── Fit text model ──────────────────────────────────────────────────────────

tfidf = TFIDFVectorizer(min_df=2, max_features=5000)
X_tr_text   = tfidf.fit_transform(df_tr["synthetic_note"].tolist())
X_val_text  = tfidf.transform(df_val["synthetic_note"].tolist())
X_test_text = tfidf.transform(df_test["synthetic_note"].tolist())

print(f"    Vocabulary size: {len(tfidf.vocab)} terms (unigrams + bigrams)")

text_model = OvRTextClassifier(
    TAGS,
    lr=0.05, n_iter=600, C=1.0, batch_size=64, tol=1e-6
)
text_model.fit(X_tr_text, df_tr[TAGS])
text_model.tune_thresholds(X_val_text, df_val[TAGS])

print("    Tuned thresholds:", {k: round(v, 2) for k, v in text_model.thresholds.items()})
print()


# ── Per-tag evaluation on validation set ───────────────────────────────────

val_probs = text_model.predict_proba_dict(X_val_text)
val_preds = text_model.predict(X_val_text)

print("    Validation text F1 per tag:")
for tag in TAGS:
    y_true = df_val[tag].values
    y_pred = val_preds[tag]
    f1 = compute_f1(y_true, y_pred)
    p, r = compute_precision_recall(y_true, y_pred)
    print(f"      {tag:<35} F1={f1:.3f}  P={p:.3f}  R={r:.3f}  thr={text_model.thresholds[tag]:.2f}")

print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 7: VITALS RISK MODEL (RULE-BASED CLINICAL SCORING)
# ─────────────────────────────────────────────────────────────────────────────

print("[6/9] Building rule-based vitals risk model ...")

# Feature columns used by the vitals model
VITALS_FEAT_COLS = (
    [f"{v}_{s}" for v in VITAL_SIGNALS
                for s in ["latest", "mean", "min", "max", "std", "range", "trend"]] +
    ["flag_tachycardia", "flag_bradycardia", "flag_hypotension", "flag_hypertension",
     "flag_fever", "flag_hypothermia", "flag_low_spo2", "flag_high_rr",
     "flag_resp_combo", "flag_hr_fever", "flag_shock_pattern"]
)

# Keep only columns that exist
VITALS_FEAT_COLS = [c for c in VITALS_FEAT_COLS if c in merged_df.columns]


def vitals_risk_score(row):
    """
    Clinical rule-based scoring → Low / Medium / High risk.

    Priority rules (condition-based, not simple sums):
      • Critical respiratory combo (low SpO2 + high RR) → major penalty
      • Shock pattern (hypotension + tachycardia) → major penalty
      • Sepsis concern (tachycardia + fever) → moderate penalty
      • Individual flags → minor penalties
    Returns (risk_label, score, flags_triggered)
    """
    score = 0
    flags = []

    resp_combo  = row.get("flag_resp_combo",  0)
    shock       = row.get("flag_shock_pattern", 0)
    tach        = row.get("flag_tachycardia",   0)
    fever       = row.get("flag_fever",         0)
    hr_fever    = row.get("flag_hr_fever",      0)
    low_spo2    = row.get("flag_low_spo2",      0)
    high_rr     = row.get("flag_high_rr",       0)
    hypotension = row.get("flag_hypotension",   0)
    bradycardia = row.get("flag_bradycardia",   0)
    hypothermia = row.get("flag_hypothermia",   0)
    hypertension= row.get("flag_hypertension",  0)

    spo2  = row.get("SpO2_latest", 97)
    rr    = row.get("RR_latest",   15)
    hr    = row.get("HR_latest",   75)
    sbp   = row.get("SBP_latest", 120)
    gcs   = row.get("GCS_latest",  15)

    # ── Severity grading for key vitals ────────────────────────────────
    # SpO2 severity
    if not pd.isna(spo2):
        if   spo2 < 88:  score += 3; flags.append("critical_low_SpO2")
        elif spo2 < 92:  score += 2; flags.append("low_SpO2")
        elif spo2 < 94:  score += 1; flags.append("borderline_SpO2")

    # RR severity
    if not pd.isna(rr):
        if   rr > 28:  score += 2; flags.append("severe_high_RR")
        elif rr > 20:  score += 1; flags.append("elevated_RR")

    # HR severity
    if not pd.isna(hr):
        if   hr > 130: score += 2; flags.append("severe_tachycardia")
        elif hr > 100: score += 1; flags.append("tachycardia")
        elif hr < 50:  score += 2; flags.append("bradycardia")
        elif hr < 60:  score += 1; flags.append("low_HR")

    # SBP severity
    if not pd.isna(sbp):
        if   sbp < 80:  score += 3; flags.append("severe_hypotension")
        elif sbp < 90:  score += 2; flags.append("hypotension")

    # Temperature
    if not pd.isna(row.get("Temp_latest", np.nan)):
        temp = row["Temp_latest"]
        if   temp > 39.0: score += 2; flags.append("high_fever")
        elif temp > 38.0: score += 1; flags.append("fever")
        elif temp < 35.5: score += 2; flags.append("hypothermia")

    # GCS
    if not pd.isna(gcs):
        if   gcs <= 8:  score += 3; flags.append("severe_reduced_GCS")
        elif gcs <= 12: score += 2; flags.append("reduced_GCS")
        elif gcs <= 14: score += 1; flags.append("mild_altered_GCS")

    # ── Combination bonuses ─────────────────────────────────────────────
    if resp_combo:
        score += 2; flags.append("respiratory_combo")
    if shock:
        score += 3; flags.append("shock_pattern")
    if hr_fever:
        score += 1; flags.append("hr_fever_combo")

    # ── Determine label ─────────────────────────────────────────────────
    if score >= 6 or "shock_pattern" in flags or "severe_hypotension" in flags:
        label = "High"
    elif score >= 3:
        label = "Medium"
    else:
        label = "Low"

    return label, score, flags


# Apply to all rows
vitals_risk_results = merged_df.apply(
    lambda row: pd.Series(vitals_risk_score(row),
                          index=["vitals_risk", "vitals_score", "vitals_flags"]),
    axis=1
)
merged_df = pd.concat([merged_df, vitals_risk_results], axis=1)

# Distribution check
dist = Counter(merged_df["vitals_risk"])
print("    Vitals risk distribution:", dict(dist))
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 8: FUSION LAYER
# ─────────────────────────────────────────────────────────────────────────────

print("[7/9] Building fusion layer and alert generator ...")

VITALS_RISK_MAP = {"Low": 0, "Medium": 1, "High": 2}
ALERT_LABELS    = ["Low", "Medium", "High"]


def compute_text_concern_score(probs_dict):
    """
    Summarise the tag probability vector into a 0..1 concern score.
    Severity weights are calibrated so that:
      • Single serious tag (resp/agitated, prob≈0.9) → score ~0.40-0.45
      • Single mild tag (pain, prob≈0.9) → score ~0.22
      • Multiple concerning tags → score ~0.6-1.0
      • Calm/stable alone → score ~0 (capped at 0)
    """
    weights = {
        "respiratory_distress_concern": 0.50,   # most clinically severe
        "agitated":                     0.45,
        "anxious":                      0.30,
        "confused":                     0.30,
        "pain_concern":                 0.25,
        "calm":                        -0.20,
        "improvement_stable":          -0.15,
    }
    score = 0.0
    for tag, w in weights.items():
        if tag in probs_dict:
            score += w * float(probs_dict[tag])
    return float(np.clip(score, 0.0, 1.0))


def fuse_and_alert(text_concern, vitals_risk_label):
    """
    Equal-weight fusion of text concern score (0..1) and vitals risk level.
    Returns final alert string: 'Low', 'Medium', or 'High'.

    Calibrated thresholds (v1):
      combined = 0.5 * text_concern + 0.5 * vitals_norm
      • combined >= 0.35  → High
      • combined >= 0.12  → Medium
      • else              → Low

    With Low vitals (norm=0): text_concern drives alert; max possible
    combined is ~0.25 → at most Medium (appropriate: stable vitals cap severity).
    With High vitals (norm=1.0): combined >= 0.5 always → always High.
    """
    vitals_norm = VITALS_RISK_MAP[vitals_risk_label] / 2.0
    combined    = 0.5 * text_concern + 0.5 * vitals_norm

    # Hard rule overrides
    if vitals_risk_label == "High":          # critical vitals → always at least High
        return "High"
    if vitals_risk_label == "Medium" and text_concern < 0.05:
        return "Medium"                      # mild text + medium vitals → Medium

    # Calibrated thresholds:
    # Low vitals (norm=0): combined = 0.5*text_concern
    #   → text_concern >= 0.64 needed for High (resp+anxious combo, prob~0.9 → 0.72)
    #   → text_concern >= 0.20 needed for Medium (pain or anxious alone, prob~0.9 → 0.225-0.27)
    # Medium vitals (norm=0.5): combined = 0.5*text + 0.25 → much easier to reach High
    # High vitals (norm=1.0): already returned "High" above

    if combined >= 0.32:
        return "High"
    elif combined >= 0.10:
        return "Medium"
    else:
        return "Low"


def generate_explanation(alert_level, predicted_tags, vitals_risk, vitals_flags, text_concern):
    """Return a single-sentence dashboard explanation."""
    concerning = [t.replace("_", " ") for t in predicted_tags
                  if t in CONCERNING_TAGS]
    positive   = [t.replace("_", " ") for t in predicted_tags
                  if t in ("calm", "improvement_stable")]

    vitals_desc = [f.replace("_", " ") for f in vitals_flags[:3]]  # top 3 flags

    if alert_level == "Low":
        if positive:
            return f"Low Alert — {', '.join(positive)}; vitals stable."
        return "Low Alert — no significant indicators detected."

    parts = []
    if concerning:
        parts.append(f"note: {', '.join(concerning)}")
    if vitals_desc:
        parts.append(f"vitals: {', '.join(vitals_desc)}")
    if not parts:
        parts.append("borderline indicators")

    return f"{alert_level} Alert — {'; '.join(parts)}."


# ── Run inference on all rows ───────────────────────────────────────────────

def run_inference(subset_df, X_text):
    probs_all = text_model.predict_proba_dict(X_text)
    preds_all = text_model.predict(X_text)

    rows = []
    for i in range(len(subset_df)):
        row    = subset_df.iloc[i]
        note_id = row["note_id"]

        # Per-sample probability dict
        probs_i = {t: float(probs_all[t][i]) for t in TAGS}

        # Predicted tags
        pred_tags = [t for t in TAGS if preds_all[t][i] == 1]

        # Text concern score
        text_concern = compute_text_concern_score(probs_i)

        # Vitals risk (pre-computed in merged_df)
        vitals_risk  = row.get("vitals_risk",  "Low")
        vitals_flags = row.get("vitals_flags", [])
        if isinstance(vitals_flags, str):
            vitals_flags = eval(vitals_flags)

        # Fused alert
        alert = fuse_and_alert(text_concern, vitals_risk)

        # Explanation
        explanation = generate_explanation(
            alert, pred_tags, vitals_risk, vitals_flags, text_concern)

        out = {
            "note_id":       note_id,
            "subject_id":    row["subject_id"],
            "anchor_time":   row["anchor_time"],
            "synthetic_note": row["synthetic_note"],
            "true_tags":     [t for t in TAGS if row[t] == 1],
            "pred_tags":     pred_tags,
            "text_concern":  round(text_concern, 3),
            "vitals_risk":   vitals_risk,
            "final_alert":   alert,
            "explanation":   explanation,
        }
        for t in TAGS:
            out[f"prob_{t}"]      = round(probs_i[t], 4)
            out[f"true_{t}"]      = int(row[t])
            out[f"pred_{t}"]      = int(preds_all[t][i])
        rows.append(out)

    return pd.DataFrame(rows)


# Rebuild X_text for each split using full merged_df indices
tr_idx_sorted   = df_tr.index.tolist()
val_idx_sorted  = df_val.index.tolist()
test_idx_sorted = df_test.index.tolist()

results_tr   = run_inference(df_tr,   X_tr_text)
results_val  = run_inference(df_val,  X_val_text)
results_test = run_inference(df_test, X_test_text)

results_all = pd.concat([results_tr, results_val, results_test], ignore_index=True)
results_all.to_csv(f"{OUT_DIR}/predictions_all.csv", index=False)
results_test.to_csv(f"{OUT_DIR}/predictions_test.csv", index=False)

print(f"    Predictions generated for all {len(results_all)} rows")
print(f"    Alert distribution (test): {dict(Counter(results_test['final_alert']))}")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 9: EVALUATION
# ─────────────────────────────────────────────────────────────────────────────

print("[8/9] Evaluating full pipeline on test set ...")
print()

report_lines = []
report_lines.append("=" * 70)
report_lines.append("PROJECT GUARDIAN MONITOR — EVALUATION REPORT")
report_lines.append(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
report_lines.append("=" * 70)
report_lines.append("")
report_lines.append(f"Test set size: {len(results_test)} samples")
report_lines.append("")

# ── Per-tag F1 on test set ─────────────────────────────────────────────────
report_lines.append("TEXT MODEL — Per-tag F1 (test set)")
report_lines.append("-" * 50)
report_lines.append(f"{'Tag':<35} {'F1':>6}  {'P':>6}  {'R':>6}  {'Support':>7}")
report_lines.append("-" * 50)

f1_scores = []
for tag in TAGS:
    y_true = results_test[f"true_{tag}"].values
    y_pred = results_test[f"pred_{tag}"].values
    f1 = compute_f1(y_true, y_pred)
    p, r = compute_precision_recall(y_true, y_pred)
    sup  = y_true.sum()
    f1_scores.append(f1)
    line = f"{tag:<35} {f1:>6.3f}  {p:>6.3f}  {r:>6.3f}  {sup:>7}"
    report_lines.append(line)
    print(f"    {line}")

macro_f1 = np.mean(f1_scores)
report_lines.append("-" * 50)
report_lines.append(f"{'Macro F1':<35} {macro_f1:>6.3f}")
print(f"    {'Macro F1':<35} {macro_f1:>6.3f}")
print()
report_lines.append("")

# ── Alert level evaluation (treating High as most critical) ────────────────
report_lines.append("FINAL ALERT DISTRIBUTION (test set)")
report_lines.append("-" * 30)

# Ground-truth alert: based on true tags
def ground_truth_alert(row_result):
    """
    Heuristic ground-truth alert from true tags + vitals risk.
    Maps directly to the same alert logic as fuse_and_alert for consistent evaluation.

    High:   respiratory_distress_concern  OR  agitated  OR  vitals High
    Medium: anxious  OR  confused  OR  pain_concern  OR  vitals Medium
    Low:    otherwise (calm/improvement/stable or no tags, Low vitals)
    """
    true_tags = row_result["true_tags"]
    if isinstance(true_tags, str):
        true_tags = eval(true_tags)
    vr = row_result["vitals_risk"]

    has_resp  = "respiratory_distress_concern" in true_tags
    has_agit  = "agitated" in true_tags
    has_anx   = "anxious"  in true_tags
    has_conf  = "confused" in true_tags
    has_pain  = "pain_concern" in true_tags

    if vr == "High" or has_resp or has_agit:
        return "High"
    if vr == "Medium" or has_anx or has_conf or has_pain:
        return "Medium"
    return "Low"

results_test["true_alert"] = results_test.apply(ground_truth_alert, axis=1)

alert_dist_true = Counter(results_test["true_alert"])
alert_dist_pred = Counter(results_test["final_alert"])
for lv in ["Low", "Medium", "High"]:
    report_lines.append(f"  {lv:<8}: true={alert_dist_true.get(lv,0)}  pred={alert_dist_pred.get(lv,0)}")
    print(f"    {lv:<8}: true={alert_dist_true.get(lv,0)}  pred={alert_dist_pred.get(lv,0)}")

print()
report_lines.append("")

# ── Alert F1 ───────────────────────────────────────────────────────────────
report_lines.append("ALERT-LEVEL F1 (per level, test set)")
report_lines.append("-" * 40)

for lv in ["Low", "Medium", "High"]:
    y_t = (results_test["true_alert"] == lv).astype(int).values
    y_p = (results_test["final_alert"] == lv).astype(int).values
    f1 = compute_f1(y_t, y_p)
    p, r = compute_precision_recall(y_t, y_p)
    line = f"  {lv:<8}: F1={f1:.3f}  P={p:.3f}  R={r:.3f}"
    report_lines.append(line)
    print(f"    {line}")

print()
report_lines.append("")

# ── Vitals risk distribution by true alert ─────────────────────────────────
report_lines.append("VITALS RISK DISTRIBUTION (test set)")
report_lines.append("-" * 30)
vr_dist = Counter(zip(results_test["vitals_risk"], results_test["true_alert"]))
for (vr, al), cnt in sorted(vr_dist.items()):
    report_lines.append(f"  vitals={vr:<7} true_alert={al}: {cnt}")

report_lines.append("")

# ── Save report ────────────────────────────────────────────────────────────
report_text = "\n".join(report_lines)
with open(f"{OUT_DIR}/evaluation_report.txt", "w") as f:
    f.write(report_text)
print(f"    Evaluation report saved → evaluation_report.txt")
print()


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 10: EXAMPLE DASHBOARD CARDS
# ─────────────────────────────────────────────────────────────────────────────

print("[9/9] Generating example dashboard cards ...")

def format_dashboard_card(row):
    pred_tags = row["pred_tags"]
    if isinstance(pred_tags, str):
        pred_tags = eval(pred_tags)
    true_tags = row["true_tags"]
    if isinstance(true_tags, str):
        true_tags = eval(true_tags)

    tag_str  = ", ".join(pred_tags) if pred_tags else "(no tags)"
    alert    = row["final_alert"]
    alert_emoji = {"Low": "🟢", "Medium": "🟡", "High": "🔴"}.get(alert, "⚪")

    lines = [
        "┌" + "─" * 66 + "┐",
        f"│  Patient {str(row['subject_id']):<12} │  Note: {str(row['note_id']):<12} │  {alert_emoji} {alert} Alert{' ' * (12 - len(alert))}│",
        "├" + "─" * 66 + "┤",
        f"│  Note:    {str(row['synthetic_note'])[:58]:<58} │",
        f"│  Tags:    {tag_str[:58]:<58} │",
        f"│  Vitals:  {str(row['vitals_risk']):<10} risk  │  Text concern: {row['text_concern']:.2f}{'':>17}│",
        "├" + "─" * 66 + "┤",
        f"│  {str(row['explanation'])[:64]:<64} │",
        "└" + "─" * 66 + "┘",
    ]
    return "\n".join(lines)


# Select one Low, Medium, High example from test set
card_examples = []
for lv in ["Low", "Medium", "High"]:
    subset = results_test[results_test["final_alert"] == lv]
    if len(subset) > 0:
        card_examples.append(subset.sample(1, random_state=SEED).iloc[0])

card_text_lines = ["PROJECT GUARDIAN MONITOR — EXAMPLE DASHBOARD CARDS",
                   "=" * 68, ""]
for ex in card_examples:
    card_text_lines.append(format_dashboard_card(ex))
    card_text_lines.append("")

card_text = "\n".join(card_text_lines)
print(card_text)

with open(f"{OUT_DIR}/example_dashboard_cards.txt", "w") as f:
    f.write(card_text)

print()

# ─────────────────────────────────────────────────────────────────────────────
# SECTION 11: SCHEMA SUMMARY + OUTPUT MANIFEST
# ─────────────────────────────────────────────────────────────────────────────

schema = {
    "synthetic_note_schema": {
        "note_id": "str — unique note identifier (SYN_XXXX)",
        "subject_id": "int — MIMIC-IV demo patient ID",
        "anchor_time": "datetime — note timestamp (synthetic, aligned to MIMIC-IV demo)",
        "variant": "str — note generation variant (baseline/resp_focus/pain_focus/etc.)",
        "synthetic_note": "str — free-text synthetic nurse note",
        "tags": "str — comma-separated ground-truth tag labels",
        "calm/anxious/.../improvement_stable": "int (0/1) — binary label per tag",
        "spo2_latest/pao2_latest/temp_latest/...": "float — anchoring vital measurements",
        "morphine_recent/...": "int (0/1) — recent medication flags",
    },
    "vitals_features_schema": {
        f"{v}_{s}": f"float — {s} of {v} over previous 6 hours"
        for v in VITAL_SIGNALS for s in ["latest", "mean", "min", "max", "std", "range", "trend"]
    },
    "prediction_output_schema": {
        "note_id": "str",
        "subject_id": "int",
        "anchor_time": "datetime",
        "pred_tags": "list[str] — predicted multi-label tags",
        "text_concern": "float — 0..1 text concern score",
        "vitals_risk": "str — Low/Medium/High vitals risk",
        "final_alert": "str — Low/Medium/High fused alert level",
        "explanation": "str — single-sentence dashboard explanation",
    },
    "tags_definition": {t: "binary label (0=absent, 1=present)" for t in TAGS},
    "alert_levels": {
        "Low":    "No significant concern; calm/stable presentation",
        "Medium": "One or more concerning indicators; monitor closely",
        "High":   "Multiple or severe indicators; escalation recommended",
    },
    "vitals_risk_rules": {
        "High":   "score ≥ 6 OR shock pattern OR severe hypotension",
        "Medium": "score 3-5",
        "Low":    "score < 3",
    },
}

with open(f"{OUT_DIR}/pipeline_schema.json", "w") as f:
    json.dump(schema, f, indent=2)

print("=" * 70)
print("PIPELINE COMPLETE — Output files saved:")
print(f"  {OUT_DIR}/")
for fn in [
    "synthetic_vitals_timeseries.csv",
    "predictions_all.csv",
    "predictions_test.csv",
    "evaluation_report.txt",
    "example_dashboard_cards.txt",
    "pipeline_schema.json",
]:
    fpath = f"{OUT_DIR}/{fn}"
    size  = os.path.getsize(fpath) if os.path.exists(fpath) else 0
    print(f"    ✓ {fn}  ({size:,} bytes)")

print()
print(f"  Text model vocabulary:   {len(tfidf.vocab)} terms")
print(f"  Tuned thresholds:        {text_model.thresholds}")
print(f"  Macro F1 (test, text):   {macro_f1:.3f}")
print()
print("Prototype ready for capstone demo.")
print("=" * 70)
