import api from "./api";

export async function getPendingUsers() {
  const response = await api.get("/users/pending"); // confirm exact path with backend
  return response.data;
}

export async function approveUser(userId) {
  const response = await api.post(`/users/${userId}/approve`);
  return response.data;
}

export async function rejectUser(userId, reason) {
  const response = await api.post(`/users/${userId}/reject`, { rejectionReason: reason });
  return response.data;
}