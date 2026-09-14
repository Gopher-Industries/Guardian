import api from './api';

export async function getAppointments({
  page = 1,
  limit = 100,
  status = '',
  from = '',
  to = '',
} = {}) {
  const params = { page, limit };
  if (status) params.status = status;
  if (from) params.from = from;
  if (to) params.to = to;

  const response = await api.get('/medical-records', { params });
  return response.data;
}
