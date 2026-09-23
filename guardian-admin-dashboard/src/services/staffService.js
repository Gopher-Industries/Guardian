import api from './api';

export async function getStaff({
  page = 1,
  limit = 10,
  search = '',
  orgId = '',
  role = '',
} = {}) {
  const params = { page, limit };
  if (search) params.search = search;
  if (orgId) params.orgId = orgId;
  if (role) params.role = role;

  const response = await api.get('/admin/staff', { params });
  return response.data;
}

export async function createStaff(userId) {
  const response = await api.post('/admin/staff', { userId });
  return response.data;
}

export async function deactivateStaff(id) {
  const response = await api.put(`/admin/staff/${id}/deactivate`);
  return response.data;
}

export async function getAllDoctors() {
  const response = await api.get('/doctors');
  return response.data;
}

export async function getAllNurses() {
  const response = await api.get('/nurse/all');
  return response.data;
}

export async function getPendingStaff() {
  const response = await api.get('/admin/staff/pending', {
    params: { orgId: "664f1c2e8b1a2c3d4e5f6a7b" }, // TEMP test value
  });
  return response.data;
}

export async function approveStaff(id) {
  const response = await api.put(`/admin/staff/${id}/approve`);
  return response.data;
}

export async function rejectStaff(id, reason) {
  const response = await api.put(`/admin/staff/${id}/status`, {
    action: "reject",
    reason,
  });
  return response.data;
}