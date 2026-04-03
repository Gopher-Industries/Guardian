import api from "./api";

export async function getMyOrganizations() {
  const response = await api.get("/api/v1/orgs/mine");
  return response.data?.orgs ?? [];
}
