import api from './api';

export async function getPatients({ page = 1, limit = 10 } = {}) {
  const params = { page, limit };
  const response = await api.get('/api/v1/admin/patients', { params });

  console.log('GET patients response:', response.data);

  return response.data;
}

export async function createPatient(patientData) {
  const response = await api.post('/api/v1/admin/patients', patientData);

  console.log('POST create patient response:', response.data);

  return response.data;
}

export async function deactivatePatient(id) {
  const response = await api.delete(`/api/v1/admin/patients/${id}`);

  console.log('DELETE patient response:', response.data);

  return response.data;
}