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

export function setPendingEmail(email) {
  localStorage.setItem(STORAGE_KEYS.email, email);
}

export function getPendingEmail() {
  return localStorage.getItem(STORAGE_KEYS.email);
}

export function removePendingEmail() {
  localStorage.removeItem(STORAGE_KEYS.email);
}

export function setPendingToken(token) {
  localStorage.setItem(STORAGE_KEYS.pendingToken, token);
}

export function getPendingToken() {
  return localStorage.getItem(STORAGE_KEYS.pendingToken);
}

export function removePendingToken() {
  localStorage.removeItem(STORAGE_KEYS.pendingToken);
}

export function setPendingUser(user) {
  localStorage.setItem(STORAGE_KEYS.pendingUser, JSON.stringify(user));
}

export function getPendingUser() {
  const raw = localStorage.getItem(STORAGE_KEYS.pendingUser);
  return raw ? JSON.parse(raw) : null;
}

export function removePendingUser() {
  localStorage.removeItem(STORAGE_KEYS.pendingUser);
}

export function promotePendingAuth() {
  const pendingToken = getPendingToken();
  const pendingUser = getPendingUser();

  if (pendingToken) setAuthToken(pendingToken);
  if (pendingUser) setAdminUser(pendingUser);

  removePendingToken();
  removePendingUser();
  removePendingEmail();
}

export function clearAuthStorage() {
  removeAuthToken();
  removeAdminUser();
  removePendingEmail();
  removePendingToken();
  removePendingUser();
}