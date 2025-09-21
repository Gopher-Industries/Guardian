// src/data.js

export const patients = [
  { id: 1, name: "John Doe", age: 45, condition: "Cardiac Care", priority: "urgent", status: "unassigned", department: "Cardiology", notes: "" },
  { id: 2, name: "Maria Wilson", age: 62, condition: "Post-Surgery", priority: "medium", status: "unassigned", department: "Surgery", notes: "" },
  // Add more patients...
];

export const staff = [
  { id: 1, name: "Dr. Martinez", role: "doctor", department: "Cardiology", maxPatients: 4, status: "available" },
  { id: 2, name: "Nurse Smith", role: "nurse", department: "ICU", maxPatients: 5, status: "available" },
  // Add more staff...
];

export const assignments = [
  { id: 1, patientId: 1, staffId: 1, status: "active", notes: "Monitor regularly", assignedAt: new Date('2024-01-15') }
];
