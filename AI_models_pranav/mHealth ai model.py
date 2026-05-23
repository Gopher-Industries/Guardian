from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay
import matplotlib.pyplot as plt

# 🔹 Activity labels
activity_names = [
    "Standing", "Sitting", "Lying", "Walking",
    "Climbing Stairs", "Waist Bends Forward",
    "Frontal Elevation of Arms", "Knees Bending (Crouching)",
    "Cycling", "Jogging", "Running", "Jump"
]

# 🔹 Create confusion matrix
cm = confusion_matrix(y_test, y_pred)

# 🔹 Plot
fig, ax = plt.subplots(figsize=(10, 8))
disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=activity_names)
disp.plot(cmap="Blues", ax=ax)

plt.title("Confusion Matrix with Activity Labels")
plt.xticks(rotation=90)
plt.tight_layout()

# 🔹 Save
plt.savefig("confusion_matrix.png")

plt.show()