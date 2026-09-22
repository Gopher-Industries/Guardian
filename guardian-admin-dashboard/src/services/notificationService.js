import api from "./api";


export async function getNotifications() {
  const response = await api.get("/notifications");
  return response.data;
}

export async function markNotificationAsRead(id) {
  const response = await api.patch(`/notifications/${id}/read`);
  return response.data;
}

export async function deleteNotification(id) {
  const response = await api.delete(`/notifications/${id}`);
  return response.data;
}
