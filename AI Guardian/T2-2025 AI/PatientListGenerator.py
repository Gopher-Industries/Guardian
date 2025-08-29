import random
import csv

def generate_patients(num_patients=50, seed=42):
    random.seed(seed)  # Set seed for reproducibility
    genders = ['Male', 'Female']
    patients = []

    for i in range(1, num_patients + 1):
        patient_id = f'P{i:04d}'  # P0001, P0002, ...
        age = random.randint(60, 90)
        gender = random.choice(genders)
        patients.append({'patient_id': patient_id, 'age': age, 'gender': gender})

    return patients

def save_to_csv(patients, filename='patients.csv'):
    with open(filename, mode='w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=['patient_id', 'age', 'gender'])
        writer.writeheader()
        writer.writerows(patients)

patients_list = generate_patients()
save_to_csv(patients_list)
print(f'Saved {len(patients_list)} patients to patients.csv')
