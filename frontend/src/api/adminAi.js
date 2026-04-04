import { api } from './client.js';

export async function aiParseJob(rawText) {
  const { data } = await api.post('/api/admin/ai/parse-job', { rawText });
  return data;
}

export async function aiParseArticle(rawText) {
  const { data } = await api.post('/api/admin/ai/parse-article', { rawText });
  return data;
}
