import api from "./api";

export async function getTasks({
  page = 1,
  limit = 20,
  status = "",
  priority = "",
  patientId = "",
  assigneeId = ""
} = {}) {
  const params = { page, limit };

  if (status) params.status = status;
  if (priority) params.priority = priority;
  if (patientId) params.patientId = patientId;
  if (assigneeId) params.assigneeId = assigneeId;

  const response = await api.get("/tasks", { params });
  return response.data;
}

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