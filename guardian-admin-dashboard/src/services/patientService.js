import api from "./api";

export const getAllPatients = async () => {
  const response = await api.get("/admin/patients");
  return response.data;
};

export const getPatientOverview = async (patientId, orgId) => {
  const response = await api.get(`/admin/patients/${patientId}/overview`, {
    params: { orgId },
  });
  return response.data;
};

export async function getPatients({ page = 1, limit = 10 } = {}) {
  const params = { page, limit };
  const response = await api.get('/admin/patients', { params });

  console.log('GET patients response:', response.data);

  return response.data;
}

export async function createPatient(patientData) {
  const response = await api.post('/admin/patients', patientData);

  console.log('POST create patient response:', response.data);

  return response.data;
}

export async function deactivatePatient(id) {
  const response = await api.delete(`/admin/patients/${id}`);

  console.log('DELETE patient response:', response.data);

  return response.data;
}
