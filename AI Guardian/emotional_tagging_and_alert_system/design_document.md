# Project Guardian Monitor — Emotional Tagging & Alert System
## Capstone Prototype v1 — Design Document
**Deakin University / Gopher Industries**

---

## 1. System Overview

Guardian Monitor is an interpretable research prototype that takes a **synthetic nurse note + 6 hours of structured vitals** for a patient encounter and produces:

- **Multi-label emotional/clinical tags** (calm, anxious, confused, agitated, pain concern, respiratory distress concern, improvement/stable)
- A **Low / Medium / High alert level**
- A **single short explanation** for the alert

> ⚠️ This is a synthetic research prototype for capstone purposes only. It uses synthetic text grounded in MIMIC-IV demo structured data patterns and is not suitable for clinical use.

---

## 2. Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     TRIGGER: New nurse note                     │
└───────────────┬──────────────────────────┬──────────────────────┘
                │                          │
        ┌───────▼──────────┐    ┌──────────▼──────────┐
        │   TEXT PATHWAY   │    │   VITALS PATHWAY    │
        │                  │    │                     │
        │  1. Clean text   │    │  1. 6-hr window     │
        │  2. TF-IDF       │    │  2. Stats features  │
        │     (uni+bigram) │    │     (latest/mean/   │
        │  3. OvR Logistic │    │      std/trend)     │
        │     Regression   │    │  3. Clinical flags  │
        │  4. Threshold    │    │  4. Rule-based      │
        │     tuning/tag   │    │     scoring →       │
        │  5. Tag probs    │    │     Low/Med/High    │
        └───────┬──────────┘    └──────────┬──────────┘
                │                          │
                │  Text concern score      │  Vitals risk norm
                │  (0..1, weighted)        │  (0, 0.5, 1.0)
                └──────────┬───────────────┘
                           │
                    ┌──────▼──────────────────────┐
                    │  FUSION (equal-weight v1)   │
                    │  combined = 0.5*text        │
                    │           + 0.5*vitals_norm │
                    │                             │
                    │  combined ≥ 0.32 → High     │
                    │  combined ≥ 0.10 → Medium   │
                    │  else           → Low       │
                    └──────┬──────────────────────┘
                           │
                    ┌──────▼──────────────────────────────────┐
                    │           DASHBOARD OUTPUT              │
                    │  • Alert: 🔴 High / 🟡 Medium / 🟢 Low │
                    │  • Tag list                             │
                    │  • One-line explanation                 │
                    └─────────────────────────────────────────┘
```

---

## 3. Data Foundation

### 3.1 Synthetic Nurse Notes Dataset
- **553 synthetic notes** across **10 MIMIC-IV demo patient encounters**
- Generated from structured vitals/medication patterns (SpO2, PaO2, temperature, opioids)
- Multiple note variants per encounter: `baseline`, `resp_focus`, `pain_focus`, `confused_focus`, `agitated_focus`, `stable_focus`
- All notes are clearly synthetic and grounded in structured state patterns

### 3.2 Synthetic Vitals Time Series
Generated per note from clinical-state-aware parameters:
- **12 readings over 6 hours** (every 30 minutes)
- Vitals: HR, RR, SBP, DBP, MAP, SpO2, Temperature, GCS
- Values seeded from CSV anchor measurements (spo2_latest, pao2_latest, temp_latest)
- Tag-aware modulation (e.g., respiratory distress → ↑RR, ↓SpO2; anxious → ↑HR, ↑RR)
- Trend-aware generation (improvement_stable → gradual normalisation)

---

## 4. Tag Definitions (v1)

| Tag | Clinical Meaning |
|-----|-----------------|
| `calm` | Patient settled, comfortable, cooperative, no acute distress |
| `anxious` | Anxious, nervous, worried, reassurance-seeking, on edge |
| `confused` | Disoriented, forgetful, requires reorientation, altered mental status |
| `agitated` | Agitated, combative, pulling at lines, highly restless |
| `pain_concern` | Reports pain, grimacing, guarding, appears uncomfortable |
| `respiratory_distress_concern` | Short of breath, laboured breathing, low SpO2/high RR presentation |
| `improvement_stable` | Improved, more settled, less distressed, stable |

**Negation supported:** "denies anxiety", "no agitation", "not in distress" block corresponding tags.

---

## 5. Text Model Pipeline

### 5.1 Pre-processing
- Lowercase + punctuation removal (cleaned text)
- Tokenisation by whitespace
- Unigrams + bigrams (feature: `token1__token2`)
- TF-IDF with smooth IDF: `idf(t) = log((N+1)/(df(t)+1)) + 1`
- L2 row normalisation
- Vocabulary: min_df=2, max_features=5000 → **384 terms** on this corpus

### 5.2 Classifier
- **One-vs-Rest binary Logistic Regression** (implemented from scratch with numpy)
- Mini-batch gradient descent (batch_size=64, lr=0.05, 600 iterations)
- L2 regularisation (C=1.0)
- **Balanced class weights** to handle label imbalance
- Separate **threshold tuning per tag** on validation set (grid search over 0.10–0.90)

### 5.3 Tuned Thresholds (v1)

| Tag | Threshold |
|-----|-----------|
| calm | 0.65 |
| anxious | 0.55 |
| confused | 0.60 |
| agitated | 0.50 |
| pain_concern | 0.40 |
| respiratory_distress_concern | 0.45 |
| improvement_stable | 0.65 |

---

## 6. Vitals Risk Model

### 6.1 Feature Set (67 features per note)
For each of HR, RR, SBP, DBP, MAP, SpO2, Temp, GCS:
- `{vital}_latest`, `{vital}_mean`, `{vital}_min`, `{vital}_max`
- `{vital}_std`, `{vital}_range`, `{vital}_trend` (last − first)

Clinical flags: `tachycardia`, `bradycardia`, `hypotension`, `hypertension`, `fever`, `hypothermia`, `low_spo2`, `high_rr`

Combination flags: `resp_combo` (low SpO2 + high RR), `hr_fever` (tachycardia + fever), `shock_pattern` (hypotension + tachycardia)

### 6.2 Rule-Based Scoring
Condition-based scoring (not simple point totals):

| Condition | Score | Clinical Rationale |
|-----------|-------|--------------------|
| SpO2 < 88% | +3 | Critical hypoxia |
| SpO2 88–91% | +2 | Significant hypoxia |
| SpO2 92–93% | +1 | Borderline |
| RR > 28 | +2 | Severe tachypnoea |
| RR 21–28 | +1 | Elevated |
| HR > 130 | +2 | Severe tachycardia |
| HR 101–130 | +1 | Tachycardia |
| SBP < 80 | +3 | Severe hypotension |
| SBP 80–89 | +2 | Hypotension |
| Temp > 39°C | +2 | High fever |
| GCS ≤ 8 | +3 | Severe reduced consciousness |
| GCS 9–12 | +2 | Moderate reduction |
| Respiratory combo (+2) | +2 | Low SpO2 AND high RR together |
| Shock pattern (+3) | +3 | Hypotension AND tachycardia together |

**Risk levels:** High (score ≥ 6 or shock/severe hypotension), Medium (3–5), Low (< 3)

---

## 7. Fusion Layer

```
text_concern_score = clip(
    0.50 × p(resp_distress) + 0.45 × p(agitated) + 0.30 × p(anxious) +
    0.30 × p(confused)     + 0.25 × p(pain)      - 0.20 × p(calm)    -
    0.15 × p(improvement_stable), 0, 1
)

vitals_norm = vitals_risk_score / 2.0   # {Low:0, Medium:0.5, High:1.0}

combined = 0.5 × text_concern + 0.5 × vitals_norm

Alert:
  High   if vitals_risk == High (hard rule)
       OR combined ≥ 0.32
  Medium if combined ≥ 0.10
  Low    otherwise
```

### Design Rationale
- Equal-weight fusion (v1) treats text and vitals as equally informative
- Hard rule: critical vitals always escalate to High regardless of text signal
- Severity weights in text concern reflect clinical priority: respiratory distress > agitation > anxiety/confusion > pain

---

## 8. Evaluation Results (Test Set, n=112)

### 8.1 Per-Tag F1 (Text Model)

| Tag | F1 | Precision | Recall | Support |
|-----|-----|-----------|--------|---------|
| calm | 0.914 | 0.842 | 1.000 | 16 |
| anxious | 0.935 | 0.878 | 1.000 | 36 |
| confused | 0.957 | 1.000 | 0.917 | 12 |
| agitated | 1.000 | 1.000 | 1.000 | 26 |
| pain_concern | 0.985 | 0.985 | 0.985 | 65 |
| respiratory_distress_concern | 0.960 | 0.923 | 1.000 | 24 |
| improvement_stable | 1.000 | 1.000 | 1.000 | 24 |
| **Macro F1** | **0.964** | | | |

> Note: High F1 reflects that the synthetic notes were generated with consistent, predictable language patterns per tag — appropriate for a prototype to verify the pipeline works. Production performance on real notes would be lower.

### 8.2 Alert-Level F1

| Alert Level | F1 | Precision | Recall |
|------------|-----|-----------|--------|
| Low | 0.773 | 0.630 | 1.000 |
| Medium | 0.703 | 0.963 | 0.553 |
| High | 0.887 | 0.810 | 0.979 |

---

## 9. Output Schema

### Prediction Output (per note)
```json
{
  "note_id":        "SYN_0063",
  "subject_id":     10014729,
  "anchor_time":    "2125-02-27T15:14:00",
  "pred_tags":      ["anxious", "respiratory_distress_concern"],
  "text_concern":   0.89,
  "vitals_risk":    "Low",
  "final_alert":    "High",
  "explanation":    "High Alert — note: anxious, respiratory distress concern."
}
```

### Dashboard Card (compact display)
```
┌────────────────────────────────────────────────────────┐
│ Patient 10014729  │ Note SYN_0063  │ 🔴 HIGH ALERT    │
├────────────────────────────────────────────────────────┤
│ Note:   Looks nervous and remains on edge. FiO2 ~40%  │
│ Tags:   anxious, respiratory_distress_concern         │
│ Vitals: Low risk  │ Text concern: 0.89               │
├────────────────────────────────────────────────────────┤
│ High Alert — note: anxious, respiratory distress.     │
└────────────────────────────────────────────────────────┘
```

---

## 10. Output Files

| File | Description |
|------|-------------|
| `guardian_pipeline.py` | Complete runnable pipeline (Python, numpy/pandas only) |
| `synthetic_vitals_timeseries.csv` | 6,636 synthetic vital-sign readings (12 per note) |
| `predictions_all.csv` | Full predictions for all 553 notes |
| `predictions_test.csv` | Test-set predictions with true labels for evaluation |
| `evaluation_report.txt` | Per-tag F1 + alert-level F1 report |
| `example_dashboard_cards.txt` | Sample Low/Medium/High dashboard outputs |
| `pipeline_schema.json` | Complete data and prediction schema |

---

## 11. Version 2 Roadmap

1. **Real note data**: Replace synthetic notes with restricted MIMIC-IV clinical text (requires PhysioNet credentialing)
2. **Stronger text model**: Fine-tuned ClinicalBERT or BioBERT with the same OvR tag structure
3. **Random Forest vitals model**: Use scikit-learn RF on the 67 vitals features (requires pyarrow for MEDS data reading)
4. **Negation handling**: Add rule-based negation detection (negspacy or custom regex)
5. **Temporal fusion**: Replace equal-weight fusion with a trained meta-learner (LR or XGBoost on combined features)
6. **Confidence intervals**: Bootstrap confidence estimates for tag probabilities
7. **Multi-encounter context**: Track alert history across visits for the same patient
