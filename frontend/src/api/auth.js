import { api } from './client.js';

const USER_KEY = 'pp_user';

export function getStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function setStoredUser(user) {
  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  } else {
    localStorage.removeItem(USER_KEY);
  }
}

export async function login(email, password) {
  const { data } = await api.post('/api/users/login', { email, password });
  return data;
}

export async function register(payload) {
  const { data } = await api.post('/api/users/register', payload);
  return data;
}

export async function requestPasswordResetOtp(email) {
  await api.post('/api/users/forgot-password', { email });
}

export async function resetPasswordWithOtp(email, otp, password) {
  await api.put('/api/users/resetpassword', { email, otp, password });
}
