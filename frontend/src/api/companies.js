import { api } from './client.js';

export async function fetchCompanies() {
  const { data } = await api.get('/api/companies');
  return data;
}
