import api from "./api";

export async function getAdminPatients() {
  const response = await api.get("/admin/patients");
  return response.data;
}

export async function getCaretakers() {
  const response = await api.get("/caretaker");
  return response.data;
}

export async function getNurses() {
  const response = await api.get("/nurse/all");
  return response.data;
}