import os
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, ConfusionMatrixDisplay

# ============================
# 1. LOAD DATASET (LOCAL PATH)
# ============================
base_path = r"E:\Deakin\T3\Sit764\dataset\SisFall_dataset"

all_data = []

for subject_folder in os.listdir(base_path):
    subject_path = os.path.join(base_path, subject_folder)

    if os.path.isdir(subject_path):
        print(f"Processing folder: {subject_folder}")

        for filename in os.listdir(subject_path):
            if filename.endswith(".txt"):
                filepath = os.path.join(subject_path, filename)

                label = 1 if filename.startswith("F") else 0

                try:
                    df = pd.read_csv(
                        filepath,
                        header=None,
                        sep=r'[,;\s]+',
                        engine='python',
                        on_bad_lines='skip'
                    )

                    df = df.iloc[:, :9]
                    df = df.apply(pd.to_numeric, errors='coerce')
                    df.dropna(how='all', inplace=True)

                    if df.shape[1] == 9 and len(df) > 0:
                        df["label"] = label
                        all_data.append(df)

                except Exception as e:
                    print(f"Error reading {filepath}: {e}")

full_dataset_df = pd.concat(all_data, ignore_index=True)

print("\nDataset Loaded Successfully!")
print("Original Shape:", full_dataset_df.shape)

# ============================
# 2. TAKE SAMPLE FOR SPEED
# ============================
full_dataset_df = full_dataset_df.sample(n=200000, random_state=42)

print("Sampled Shape:", full_dataset_df.shape)

# ============================
# 3. DATA CLEANING
# ============================
sensor_cols = full_dataset_df.columns[:9]

full_dataset_df[sensor_cols] = full_dataset_df[sensor_cols].apply(pd.to_numeric, errors='coerce')
full_dataset_df.dropna(subset=sensor_cols, how='all', inplace=True)

full_dataset_df = full_dataset_df.iloc[:, :9].copy().assign(label=full_dataset_df["label"])

full_dataset_df.columns = [
    "acc1_x", "acc1_y", "acc1_z",
    "gyro_x", "gyro_y", "gyro_z",
    "acc2_x", "acc2_y", "acc2_z",
    "label"
]

print("After cleaning:", full_dataset_df.shape)
print(full_dataset_df["label"].value_counts())

# ============================
# 4. FEATURE + TARGET
# ============================
X = full_dataset_df.drop("label", axis=1)
y = full_dataset_df["label"]

# ============================
# 5. TRAIN TEST SPLIT
# ============================
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.3, random_state=42, stratify=y
)

# ============================
# 6. SCALING
# ============================
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# ============================
# 7. MODELS
# ============================
rf = RandomForestClassifier(n_estimators=100, random_state=42, n_jobs=-1)
svm = SVC(kernel="rbf")
knn = KNeighborsClassifier(n_neighbors=5)

print("\nTraining Random Forest...")
rf.fit(X_train_scaled, y_train)

print("Training SVM...")
svm.fit(X_train_scaled, y_train)

print("Training KNN...")
knn.fit(X_train_scaled, y_train)

# ============================
# 8. PREDICTIONS
# ============================
pred_rf = rf.predict(X_test_scaled)
pred_svm = svm.predict(X_test_scaled)
pred_knn = knn.predict(X_test_scaled)

# ============================
# 9. ACCURACY
# ============================
acc_rf = accuracy_score(y_test, pred_rf)
acc_svm = accuracy_score(y_test, pred_svm)
acc_knn = accuracy_score(y_test, pred_knn)

print("\nAccuracy:")
print("Random Forest:", acc_rf)
print("SVM:", acc_svm)
print("KNN:", acc_knn)

# ============================
# 10. BEST MODEL
# ============================
results = [
    ("Random Forest", acc_rf, pred_rf),
    ("SVM", acc_svm, pred_svm),
    ("KNN", acc_knn, pred_knn)
]

best_model = max(results, key=lambda x: x[1])

print("\nBest Model:", best_model[0])

# ============================
# 11. CLASSIFICATION REPORT
# ============================
print("\nClassification Report:")
print(classification_report(y_test, best_model[2], target_names=["Normal", "Fall"]))

# ============================
# 12. CONFUSION MATRIX
# ============================
cm = confusion_matrix(y_test, best_model[2])

disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=["Normal", "Fall"])
disp.plot()
plt.title(f"Confusion Matrix - {best_model[0]}")
plt.show()

# ============================
# 13. ACCURACY GRAPH
# ============================
models = ["Random Forest", "SVM", "KNN"]
accuracies = [acc_rf, acc_svm, acc_knn]

plt.figure(figsize=(8, 5))
bars = plt.bar(models, accuracies)
plt.title("Model Accuracy Comparison - SisFall")
plt.xlabel("Model")
plt.ylabel("Accuracy")
plt.ylim(0, 1)

for bar in bars:
    h = bar.get_height()
    plt.text(bar.get_x() + bar.get_width()/2, h + 0.01, f"{h:.4f}",
             ha="center", va="bottom")

plt.show()
