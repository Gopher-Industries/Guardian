import api from "./api";

// The task routes are mounted at /api/v1/tasks on the backend (see
// server.js: app.use('/api/v1/tasks', taskRoutes)) — NOT under /admin.
// The baseURL already includes /api/v1/, so the path here is just "/tasks".

export async function getAdminTasks(params = {}) {
  const response = await api.get("/tasks", { params });
  return response.data;
}

export async function createTask(taskData) {
  const response = await api.post("/tasks", taskData);
  return response.data;
}

export async function updateTask(taskId, taskData) {
  const response = await api.put(`/tasks/${taskId}`, taskData);
  return response.data;
}

export async function deleteTask(taskId) {
  const response = await api.delete(`/tasks/${taskId}`);
  return response.data;
}