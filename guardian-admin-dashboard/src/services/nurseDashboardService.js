import api from "./api";

export async function getNurseDashboardSummary() {
  const response = await api.get("/nurse/dashboard-summary");
  return response.data;
}