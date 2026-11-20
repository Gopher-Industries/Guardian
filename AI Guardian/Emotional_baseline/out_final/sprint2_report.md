# Sprint 2: Basic NLP Features & Baselines (Leakage-Safe)

- **CSV:** New AI spreadsheet - Sheet1.csv

- **Text column:** `nursingNote`   |   **Label column:** `state`

- **Groups:** groups by normalized text

- **Dedup:** exact duplicates removed after scrubbing

- **Leakage guard:** stripped tokens ['comfort', 'comfortable', 'normal', 'normality', 'normally', 'sick', 'sickness', 'uncomfort', 'uncomfortable']


## Feature extraction

- BoW (1-gram), min_df=2

- TF-IDF with bigrams & trigrams (captures phrases like *not happy*, *very tired*).

- Lexicon counts: pos/neg + six emotions (anger, joy, sadness, fear, surprise, disgust).


## Baseline models
- Logistic Regression (balanced)
- Linear SVM (balanced)


## Single-split results (sorted by Macro-F1)


        Features     Model  Accuracy  F1_macro
    BoW(1) + lex    LogReg     1.000  1.000000
    BoW(1) + lex LinearSVM     1.000  1.000000
TFIDF(1–2) + lex LinearSVM     0.950  0.906020
TFIDF(1–3) + lex LinearSVM     0.950  0.906020
TFIDF(1–3) + lex    LogReg     0.925  0.884162
TFIDF(1–2) + lex    LogReg     0.900  0.822402



**Best (single split):** BoW(1) + lex + LogReg  

Accuracy: **1.000**   |   Macro-F1: **1.000**


**Confusion matrix** saved to `confusion_matrix.png`.


## 5-fold CV (GroupKFold; mean ± std)


        Features     Model      Accuracy      F1_macro
TFIDF(1–2) + lex LinearSVM  0.98 ± 0.011 0.956 ± 0.032
TFIDF(1–3) + lex LinearSVM  0.98 ± 0.011 0.956 ± 0.032
TFIDF(1–2) + lex    LogReg 0.975 ± 0.018  0.951 ± 0.03
TFIDF(1–3) + lex    LogReg 0.975 ± 0.018  0.951 ± 0.03



## Environment

- python: `/opt/anaconda3/envs/sprint2nlp/bin/python`

- numpy: 2.3.3 | pandas: 2.3.2 | sklearn: 1.7.2
