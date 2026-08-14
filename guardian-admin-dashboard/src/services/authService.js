import api from "./api";

export async function loginAdmin({ email, password }) {
  const response = await api.post("/auth/login", {
    email,
    password,
  });

  return response.data;
}

export async function requestPasswordReset(email) {
  const response = await api.post("/auth/reset-password-request", {
    email,
  });

  return response.data;
}