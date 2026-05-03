import api from "./api";

let mockNotifications = [
  {
    id: "1",
    title: "System Update",
    message: "Guardian Admin Dashboard v2.4 is now live with improved security protocols.",
    type: "info",
    isRead: false,
    createdAt: new Date().toISOString(),
  },
  {
    id: "2",
    title: "Security Breach Attempt",
    message: "Multiple failed login attempts detected from IP 192.168.1.105. Verification required.",
    type: "error",
    isRead: false,
    createdAt: new Date(Date.now() - 3600000).toISOString(),
  },
  {
    id: "3",
    title: "Database Backup",
    message: "Weekly automated database backup completed successfully.",
    type: "success",
    isRead: true,
    createdAt: new Date(Date.now() - 86400000).toISOString(),
  },
  {
    id: "4",
    title: "Server Warning",
    message: "Primary server CPU usage at 85%. Monitoring system is active.",
    type: "warning",
    isRead: false,
    createdAt: new Date(Date.now() - 172800000).toISOString(),
  }
];

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

export async function getNotifications() {
    // const response = await api.get("/api/v1/notifications");
    // return response.data;
    
    await delay(800); 
    return { notifications: [...mockNotifications] };
}

export async function markNotificationAsRead(id) {
    // const response = await api.patch(`/api/v1/notifications/${id}/read`);
    // return response.data;
    
    await delay(300);
    mockNotifications = mockNotifications.map(n => 
        n.id === id ? { ...n, isRead: true, read: true } : n
    );
    return { success: true };
}

export async function deleteNotification(id) {
    // const response = await api.delete(`/api/v1/notifications/${id}`);
    // return response.data;
    
    await delay(300);
    mockNotifications = mockNotifications.filter(n => n.id !== id);
    return { success: true };
}
