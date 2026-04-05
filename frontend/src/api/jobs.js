import { api } from './client.js';

export async function fetchJobs({ page = 0, size = 8, audience = 'ALL' } = {}) {
  const { data } = await api.get('/api/jobs', {
    params: { page, size, audience },
  });
  return data;
}

export async function fetchJobsAdmin() {
  const { data } = await api.get('/api/jobs/admin');
  return data;
}

export async function createJob(payload) {
  const { data } = await api.post('/api/jobs/admin', payload);
  return data;
}

export async function updateJob(id, payload) {
  const { data } = await api.put(`/api/jobs/admin/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteJob(id) {
  await api.delete(`/api/jobs/admin/${encodeURIComponent(id)}`);
}
