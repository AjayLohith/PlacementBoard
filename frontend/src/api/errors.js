import axios from 'axios';

export function getApiErrorMessage(err, fallback = 'Something went wrong') {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data;
    if (data?.message && typeof data.message === 'string') {
      return data.message;
    }
    if (err.response?.status === 401) {
      return 'Sign in again to continue.';
    }
    if (err.response?.status === 403) {
      return data?.message && typeof data.message === 'string'
        ? data.message
        : 'You do not have access to this action.';
    }
  }
  if (err instanceof Error) {
    return err.message;
  }
  return fallback;
}
