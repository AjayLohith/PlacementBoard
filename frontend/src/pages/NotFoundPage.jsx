import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="empty" style={{ marginTop: '2rem' }}>
      <h1 className="page-head__title" style={{ marginBottom: '0.5rem' }}>
        Page not found
      </h1>
      <p style={{ marginBottom: '1.25rem' }}>That path does not exist.</p>
      <Link className="btn btn--primary" to="/">
        Home
      </Link>
    </div>
  );
}
