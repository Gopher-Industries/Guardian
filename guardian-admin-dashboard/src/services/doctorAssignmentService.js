// const mockDoctors = [
//   {
//     _id: "doc001",
//     name: "Dr. John Smith",
//     email: "john.smith@guardianhealth.com",
//     specialization: "General Physician",
//   },
//   {
//     _id: "doc002",
//     name: "Dr. Emily Brown",
//     email: "emily.brown@guardianhealth.com",
//     specialization: "Cardiologist",
//   },
//   {
//     _id: "doc003",
//     name: "Dr. Michael Wilson",
//     email: "michael.wilson@guardianhealth.com",
//     specialization: "Neurologist",
//   },
// ];

// let mockDoctorPatients = {
//   doc001: [
//     {
//       _id: "pat001",
//       name: "Robert Green",
//       age: 72,
//       gender: "Male",
//       condition: "Diabetes",
//     },
//     {
//       _id: "pat002",
//       name: "Mary Johnson",
//       age: 68,
//       gender: "Female",
//       condition: "High Blood Pressure",
//     },
//   ],
//   doc002: [
//     {
//       _id: "pat003",
//       name: "William Taylor",
//       age: 75,
//       gender: "Male",
//       condition: "Heart Disease",
//     },
//   ],
//   doc003: [],
// };

// function delay(ms = 500) {
//   return new Promise((resolve) => setTimeout(resolve, ms));
// }

// export async function getDoctors() {
//   await delay();
//   return {
//     doctors: mockDoctors,
//   };
// }

// export async function getPatientsByDoctor(doctorId) {
//   await delay();
//   return {
//     patients: mockDoctorPatients[doctorId] || [],
//   };
// }

// export async function assignDoctorToPatient(patientId, doctorId) {
//   await delay();

//   const allPatients = Object.values(mockDoctorPatients).flat();
//   const existingPatient = allPatients.find((patient) => patient._id === patientId);

//   let patientToAssign =
//     existingPatient || {
//       _id: patientId,
//       name: `Patient ${patientId}`,
//       age: "N/A",
//       gender: "N/A",
//       condition: "Not available",
//     };

//   Object.keys(mockDoctorPatients).forEach((doctorKey) => {
//     mockDoctorPatients[doctorKey] = mockDoctorPatients[doctorKey].filter(
//       (patient) => patient._id !== patientId
//     );
//   });

//   if (!mockDoctorPatients[doctorId]) {
//     mockDoctorPatients[doctorId] = [];
//   }

//   mockDoctorPatients[doctorId].push(patientToAssign);

//   return {
//     success: true,
//     message: "Doctor assigned successfully",
//   };
// }

// export async function unassignDoctorFromPatient(patientId) {
//   await delay();

//   Object.keys(mockDoctorPatients).forEach((doctorKey) => {
//     mockDoctorPatients[doctorKey] = mockDoctorPatients[doctorKey].filter(
//       (patient) => patient._id !== patientId
//     );
//   });

//   return {
//     success: true,
//     message: "Doctor unassigned successfully",
//   };
// }
import api from "./api";

export async function getDoctors() {
  const response = await api.get("/api/v1/doctors");
  return response.data;
}

export async function getPatientsByDoctor(doctorId) {
  const response = await api.get(`/api/v1/doctors/${doctorId}/patients`);
  return response.data;
}

export async function assignDoctorToPatient(patientId, doctorId) {
  const response = await api.post(
    `/api/v1/patients/${patientId}/assign-doctor`,
    { doctorId }
  );
  return response.data;
}

export async function unassignDoctorFromPatient(patientId) {
  const response = await api.post(
    `/api/v1/patients/${patientId}/assign-doctor`,
    { doctorId: null }
  );
  return response.data;
}