import { api } from './client.js';

export async function fetchExperiencesByCompany(slug) {
  const { data } = await api.get(`/api/experiences/${encodeURIComponent(slug)}`);
  return data;
}

export async function createExperience(payload) {
  const { data } = await api.post('/api/experiences', payload);
  return data;
}

export async function fetchPendingExperiences() {
  const { data } = await api.get('/api/experiences/admin/pending');
  return data;
}

export async function fetchApprovedExperiences() {
  const { data } = await api.get('/api/experiences/admin/approved');
  return data;
}

export async function approveExperience(id) {
  const { data } = await api.put(`/api/experiences/admin/${encodeURIComponent(id)}/approve`);
  return data;
}

export async function deleteExperience(id) {
  await api.delete(`/api/experiences/admin/${encodeURIComponent(id)}`);
}

export async function rejectExperience(id, note) {
  const { data } = await api.put(`/api/experiences/admin/${encodeURIComponent(id)}/reject`, {
    note: note ?? '',
  });
  return data;
}
