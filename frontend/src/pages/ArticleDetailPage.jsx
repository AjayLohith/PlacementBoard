import { useQuery } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { Link, useParams } from 'react-router-dom';
import { fetchArticleBySlug } from '../api/articles.js';

export function ArticleDetailPage() {
  const { slug } = useParams();

  const q = useQuery({
    queryKey: ['article', slug],
    queryFn: () => fetchArticleBySlug(slug),
    enabled: Boolean(slug),
  });

  if (!slug) {
    return <p className="field__error">Invalid link.</p>;
  }

  if (q.isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
        <span className="spinner" />
      </div>
    );
  }

  if (q.isError) {
    return (
      <div className="empty">
        <p>Article not found.</p>
        <Link className="btn btn--secondary" to="/articles" style={{ marginTop: '1rem', display: 'inline-block' }}>
          All articles
        </Link>
      </div>
    );
  }

  const a = q.data;
  let when = '';
  if (a.publishedAt) {
    try {
      when = format(parseISO(a.publishedAt), 'MMMM d, yyyy');
    } catch {
      when = '';
    }
  }

  return (
    <>
      <p style={{ marginBottom: '1rem' }}>
        <Link to="/articles" className="header__link">
          All articles
        </Link>
      </p>
      <header className="page-head">
        <h1 className="page-head__title">{a.title}</h1>
        {when ? <p className="page-head__lede">{when}</p> : null}
      </header>
      {a.excerpt ? (
        <p style={{ fontSize: '1.05rem', color: 'var(--ink-soft)', marginBottom: '1.25rem' }}>{a.excerpt}</p>
      ) : null}
      <div className="card">
        <div className="card__body">
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.65 }}>{a.body}</div>
        </div>
      </div>
    </>
  );
}
