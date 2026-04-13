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