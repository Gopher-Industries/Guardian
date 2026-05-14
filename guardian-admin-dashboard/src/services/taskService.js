import api from "./api";

// TODO: integrate with proper backend GET API when endpoint is available
// export async function getAdminTasks() {
//   const response = await api.get("/admin/tasks");
//   return response.data;
// }

export async function createTask(taskData) {
  const response = await api.post("/admin/tasks", taskData);
  return response.data;
}

export async function updateTask(taskId, taskData) {
  const response = await api.put(`/admin/tasks/${taskId}`, taskData);
  return response.data;
}

export async function deleteTask(taskId) {
  const response = await api.delete(`/admin/tasks/${taskId}`);
  return response.data;
}