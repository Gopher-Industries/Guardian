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

  const response = await api.get('/api/v1/admin/staff', { params });
  return response.data;
}

export async function createStaff(userId) {
  const response = await api.post('/api/v1/admin/staff', { userId });
  return response.data;
}

export async function deactivateStaff(id) {
  const response = await api.put(`/api/v1/admin/staff/${id}/deactivate`);
  return response.data;
}
