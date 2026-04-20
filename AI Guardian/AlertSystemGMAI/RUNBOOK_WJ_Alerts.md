# Runbook – William Jackson

## Requirements

- Python 3.9+
- Recommended environment: Google Colab
- Packages needed: numpy, pandas, scikit-learn, tensorflow, matplotlib, joblib

In Colab install with:

```bash
!pip install numpy pandas scikit-learn tensorflow matplotlib joblib
```

## Dataset

File: New AI spreadsheet - Sheet1.csv  
Headers required:  
patientId, observationEnd, stepsTaken, calorieIntake, sleepHours, waterIntakeMl, bathroomVisits, exerciseMinutes, mealsSkipped

Upload the CSV to /content/ when running in Colab.

## Steps

1. Upload dataset to /content/.
2. Open AlertSystemScript.ipynb.
3. At the top, set the dataset path:
   ```python
   CSV_PATH = "/content/New AI spreadsheet - Sheet1.csv"
   ```
4. Run the notebook in its entirety
5. Outputs will be saved to /content/:
   - guardian_ae_predictions.csv
   - guardian_classifier_predictions.csv
   - top_alerts.csv (top 30 medium/high alerts)

## Artifacts

The ipynb file Training also creates:

- guardian_lstm_autoencoder.keras
- guardian_behavior_rf.joblib
- guardian_behavior_mlp.joblib
- scaler.npy
