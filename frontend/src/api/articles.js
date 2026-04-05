import { api } from './client.js';

export async function fetchArticles({ page = 0, size = 8 } = {}) {
  const { data } = await api.get('/api/articles', {
    params: { page, size },
  });
  return data;
}

export async function fetchArticleBySlug(slug) {
  const { data } = await api.get(`/api/articles/slug/${encodeURIComponent(slug)}`);
  return data;
}

export async function fetchArticlesAdmin() {
  const { data } = await api.get('/api/articles/admin');
  return data;
}

export async function createArticle(payload) {
  const { data } = await api.post('/api/articles/admin', payload);
  return data;
}

export async function updateArticle(id, payload) {
  const { data } = await api.put(`/api/articles/admin/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteArticle(id) {
  await api.delete(`/api/articles/admin/${encodeURIComponent(id)}`);
}
