import { useQuery } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { Link } from 'react-router-dom';
import { fetchArticles } from '../api/articles.js';

export function ArticlesPage() {
  const { data: articles, isLoading, isError } = useQuery({
    queryKey: ['articles'],
    queryFn: fetchArticles,
  });

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
      {articles && articles.length === 0 && <div className="empty">No published articles yet.</div>}
      {articles && articles.length > 0 && (
        <div className="stack">
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
      )}
    </>
  );
}
