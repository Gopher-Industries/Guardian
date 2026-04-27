import api from "./api";

export async function getNotifications() {
    const response = await api.get("/api/v1/notifications");
    return response.data;
}

export async function markNotificationAsRead(id) {
    const response = await api.patch(`/api/v1/notifications/${id}/read`);
    return response.data;
}

export async function deleteNotification(id) {
    const response = await api.delete(`/api/v1/notifications/${id}`);
    return response.data;
}
