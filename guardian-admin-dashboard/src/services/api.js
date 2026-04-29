import axios from "axios";
import { getAuthToken } from "../utils/storage";

const api = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    "https://guardian-backend-git-fix-cors-patelrudra2306-5873s-projects.vercel.app/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = getAuthToken();
  console.log('token:', getAuthToken());

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;