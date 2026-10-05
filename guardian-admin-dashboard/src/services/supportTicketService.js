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

export async function createSupportTicket({
  subject,
  description,
  issue_type,
  priority,
}) {
  const response = await api.post('/admin/support-tickets', {
    subject,
    description,
    issue_type,
    priority,
  });
  return response.data;
}

export async function updateSupportTicket(
  ticketId,
  { subject, description, issue_type, priority, status, adminResponse },
) {
  const payload = {
    subject,
    description,
    issue_type,
    priority,
    status,
    adminResponse,
  };

  const response = await api.put(`/admin/support-tickets/${ticketId}`, payload);
  return response.data;
}
