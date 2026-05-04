import { api } from './client.js';

export async function getAdminNotes() {
  const { data } = await api.get('/api/admin/notes');
  return data;
}

export async function updateAdminNotes(content) {
  const { data } = await api.put('/api/admin/notes', { content });
  return data;
}
