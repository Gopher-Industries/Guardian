# Data Schemas & Sample Datasets – William Jackson

## Overview

This folder is for the Guardian alert system with emotional and healht risk detection - data & Requirement\_ task.
It contains reference schemas, sample datasets, and preprocessing notebooks for vitals, wearable inputs, and optional emotion data.

## Purpose

- Define data schemas for vitals, wearable inputs, and optional emotion data (facial/voice).
- Collect annotated vitals time-series and simulated wearable datasets.
- Design preprocessing pipelines for time-series cleaning and baseline normalization.
- Establish privacy and security guidelines for wearable and sensitive emotion data.
- Develop data acquisition plan including integration of future wearable data sources.
- Implement quality assurance for time-series integrity and labeling accuracy.
- Review requirements with clinical staff and guardians for alert relevance.

## Contents

- **Sample datasets**

  - `sample_emotion.csv` – example of optional emotion input (facial/voice tags).
  - `sample_vital.csv` – simulated vital signs time-series.
  - `sample_wearable.csv` – wearable activity and lifestyle signals.

- **Schemas**

  - `schema_alert.csv` – structure of system-generated alerts.
  - `schema_vital.csv` – expected fields for vitals ingestion.
  - `schema_wearable.csv` – expected fields for wearable inputs.

- **Notebooks**
  - `preprocessing.ipynb` – data cleaning, normalization, schema validation.

---

## How to Use

1. Review `schemas/` to understand the required fields.
2. Load files from `raw_data/` as test inputs.
3. Use `preprocessing.ipynb` to validate and normalize sample data.
4. Save cleaned outputs into `processed_data/`.

---

_Author: William Jackson_  
_AI Sprint T2 2025 – Data & Requirements_
