import api from './api';

export function extractTicketsResponse(data) {
  const tickets = Array.isArray(data?.tickets) ? data.tickets : [];
  const total = typeof data?.total === 'number' ? data.total : tickets.length;
  return { tickets, total };
}

export async function getSupportTickets({
  page = 1,
  limit = 10,
  search = '',
  status = '',
} = {}) {
  const params = { page, limit };
  if (search) params.search = search;
  if (status) params.status = status;

  const response = await api.get('/admin/support-tickets', { params });
  return extractTicketsResponse(response.data);
}

export async function createSupportTicket({ subject, description }) {
  const response = await api.post('/admin/support-tickets', {
    subject,
    description,
  });
  return response.data;
}

export async function updateSupportTicket(ticketId, { status, adminResponse }) {
  const response = await api.patch(`/admin/support-tickets/${ticketId}`, {
    status,
    adminResponse,
  });
  return response.data;
}

export async function addSupportTicketAction(
  ticketId,
  { actionTaken, outcome, recommendation, notes },
) {
  const response = await api.post(`/admin/support-tickets/${ticketId}/actions`, {
    actionTaken,
    outcome,
    recommendation,
    notes,
  });
  return response.data;
}
