import api from "./api";

export async function loginAdmin({ email, password }) {
  const response = await api.post("/auth/login", {
    email,
    password,
  });

  console.log("LOGIN RESPONSE:", response.data);
  return response.data;
}

export async function sendPin(email) {
  const response = await api.post("/auth/send-pin", {
    email,
  });

  console.log("SEND PIN RESPONSE:", response.data);
  return response.data;
}

export async function verifyPin({ email, otp }) {
  const response = await api.post("/auth/verify-pin", {
    email,
    otp,
  });

  console.log("VERIFY PIN RESPONSE:", response.data);
  return response.data;
}