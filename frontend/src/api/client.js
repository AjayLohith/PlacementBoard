import axios from 'axios';

function resolveBaseUrl() {
  const fromEnv = import.meta.env.VITE_API_URL?.trim();
  if (fromEnv) {
    return fromEnv.replace(/\/$/, '');
  }
  if (import.meta.env.DEV) {
    return 'http://127.0.0.1:5001';
  }
  return '';
}

export const api = axios.create({
  baseURL: resolveBaseUrl(),
  headers: { 'Content-Type': 'application/json' },
});

function emitApiDown(err) {
  if (typeof window === 'undefined') {
    return;
  }
  const detail = {
    message: err?.message || 'Network error',
  };
  window.dispatchEvent(new CustomEvent('pp:api-down', { detail }));
}

function emitApiUp() {
  if (typeof window === 'undefined') {
    return;
  }
  window.dispatchEvent(new CustomEvent('pp:api-up'));
}

const TOKEN_KEY = 'pp_token';
const USER_KEY = 'pp_user';

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
  } else {
    localStorage.removeItem(TOKEN_KEY);
  }
}

api.interceptors.request.use((config) => {
  const t = getStoredToken();
  if (t) {
    config.headers.Authorization = `Bearer ${t}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => {
    emitApiUp();
    return res;
  },
  (err) => {
    if (axios.isAxiosError(err)) {
      const status = err.response?.status;
      if (!err.response || status === 502 || status === 503 || status === 504) {
        emitApiDown(err);
      }
    }
    if (!axios.isAxiosError(err) || err.response?.status !== 401) {
      return Promise.reject(err);
    }
    const url = err.config?.url ?? '';
    const skip =
      url.includes('/api/users/login') ||
      url.includes('/api/users/register') ||
      url.includes('/api/users/forgot-password') ||
      url.includes('/resetpassword');
    if (skip) {
      return Promise.reject(err);
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.dispatchEvent(new CustomEvent('pp:session-expired'));
    return Promise.reject(err);
  },
);
