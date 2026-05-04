import { api } from './client.js';

export async function fetchAdminNotes() {
  const { data } = await api.get('/api/admin/notes');
  return data;
}

export async function createAdminNote(payload) {
  const { data } = await api.post('/api/admin/notes', payload);
  return data;
}

export async function deleteAdminNote(id) {
  await api.delete(`/api/admin/notes/${encodeURIComponent(id)}`);
}

export async function updateAdminNotes(content) {
  const { data } = await api.post('/api/admin/notes', { content });
  return data;
}
