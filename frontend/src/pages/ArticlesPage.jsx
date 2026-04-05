import { useQuery } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchArticles } from '../api/articles.js';

const PAGE_SIZE = 8;

export function ArticlesPage() {
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['articles', page],
    queryFn: () => fetchArticles({ page, size: PAGE_SIZE }),
  });

  const articles = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Guides</p>
        <h1 className="page-head__title">Articles</h1>
        <p className="page-head__lede">Tips and updates from the team.</p>
      </header>

      {isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <span className="spinner" />
        </div>
      )}
      {isError && <p className="field__error">Could not load articles.</p>}
      {!isLoading && !isError && articles.length === 0 && <div className="empty">No published articles yet.</div>}
      {!isLoading && articles.length > 0 && (
        <>
          <div className="card-grid">
            {articles.map((a) => {
              let when = '';
              if (a.publishedAt) {
                try {
                  when = format(parseISO(a.publishedAt), 'MMMM d, yyyy');
                } catch {
                  when = '';
                }
              }
              return (
                <Link key={a._id} to={`/articles/${a.slug}`} className="company-card__link">
                  <article className="card card--interactive">
                    <div className="card__body">
                      <h2 className="page-head__title" style={{ fontSize: '1.2rem', marginBottom: '0.35rem' }}>
                        {a.title}
                      </h2>
                      {when ? <p className="field__hint">{when}</p> : null}
                      {a.excerpt ? <p style={{ marginTop: '0.5rem' }}>{a.excerpt}</p> : null}
                      <p className="field__hint" style={{ marginTop: '0.75rem' }}>
                        Read more
                      </p>
                    </div>
                  </article>
                </Link>
              );
            })}
          </div>

          {totalPages > 1 && (
            <div className="pager">
              <button
                type="button"
                className="btn btn--secondary btn--sm"
                disabled={page <= 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Previous
              </button>
              <span className="pager__meta">
                Page {page + 1} of {totalPages}
                {totalElements > 0 ? ` (${totalElements} total)` : ''}
              </span>
              <button
                type="button"
                className="btn btn--secondary btn--sm"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </>
  );
}
