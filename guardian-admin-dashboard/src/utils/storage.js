import { STORAGE_KEYS } from "./constants";

export function setAuthToken(token) {
  localStorage.setItem(STORAGE_KEYS.token, token);
}

export function getAuthToken() {
  const token = localStorage.getItem(STORAGE_KEYS.token);
  return token;
}

export function removeAuthToken() {
  localStorage.removeItem(STORAGE_KEYS.token);
}

export function setAdminUser(user) {
  localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
}

export function getAdminUser() {
  const raw = localStorage.getItem(STORAGE_KEYS.user);
  return raw ? JSON.parse(raw) : null;
}

export function removeAdminUser() {
  localStorage.removeItem(STORAGE_KEYS.user);
}

export function clearAuthStorage() {
  removeAuthToken();
  removeAdminUser();
}