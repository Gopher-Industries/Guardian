import api from './api';

export async function getSupportTickets({
  page = 1,
  limit = 10,
  search = '',
  status = '',
  priority = '',
} = {}) {
  const params = { page, limit };
  if (search) params.search = search;
  if (status) params.status = status;
  if (priority) params.priority = priority;

  const response = await api.get('/admin/support-tickets', { params });
  return response.data;
}

export async function createSupportTicket(payload) {
  const response = await api.post('/admin/support-ticket', payload);
  return response.data;
}

export async function updateSupportTicket(ticketId, payload) {
  const response = await api.put(`/admin/support-ticket/${ticketId}`, payload);
  return response.data;
}
