# Alert System Task – William Jackson

## Overview

Behavioural anomaly detection pipeline using an LSTM autoencoder and simple classifiers.  
The autoencoder is trained on supporter behaviour sequences and reconstruction error is used to map anomalies into Low / Medium / High risk.  
Random Forest and MLP classifiers are also trained to detect anomalies from the same features.

## Features

- Steps taken
- Calorie intake
- Sleep hours
- Water intake (ml)
- Bathroom visits
- Exercise minutes (if available)
- Meals skipped (if available)

## Contents

- `AlertSystemScript.ipynb` – main notebook (training, scoring, exporting results)
- `artifacts/` – models and outputs
  - `guardian_lstm_autoencoder.keras`
  - `guardian_behavior_rf.joblib`
  - `guardian_behavior_mlp.joblib`
  - `scaler.npy`
  - `guardian_ae_predictions.csv`
  - `guardian_classifier_predictions.csv`
  - `top_alerts.csv` – shortlist of medium/high alerts top 30

## How to run (Colab)

1. Upload the dataset with original headers (`patientId, observationEnd, stepsTaken, calorieIntake, sleepHours, waterIntakeMl, bathroomVisits, exerciseMinutes, mealsSkipped`) to `/content/`.
2. Set the dataset path at the top of the notebook:
   ```python
   CSV_PATH = "/content/New AI spreadsheet - Sheet1.csv"
   ```
3. Run the notebook end-to-end.
4. Outputs will be saved to /content/:

- guardian_ae_predictions.csv
- guardian_classifier_predictions.csv
- top_alerts.csv
