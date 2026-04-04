import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { fetchCompanies } from '../api/companies.js';
import { useAuth } from '../context/AuthContext.jsx';

export function HomePage() {
  const { user } = useAuth();
  const { data: companies, isLoading, isError } = useQuery({
    queryKey: ['companies'],
    queryFn: fetchCompanies,
  });

  return (
    <>
      <section className="hero">
        <p className="page-head__eyebrow">Student-written</p>
        <h1 className="hero__title">Placement interviews, demystified.</h1>
        <p className="hero__text">
          Read how rounds actually unfold&mdash;what was asked, how long it took, and what helped.
          Add your own story to help the next batch.
        </p>
        <div className="hero__actions">
          <Link className="btn btn--primary btn--lg" to="/companies">
            Browse companies
          </Link>
          <Link className="btn btn--secondary btn--lg" to="/jobs">
            Job board
          </Link>
          <Link className="btn btn--secondary btn--lg" to="/articles">
            Articles
          </Link>
          {!user ? (
            <Link className="btn btn--ghost btn--lg" to="/register">
              Create an account
            </Link>
          ) : null}
        </div>
        <p className="field__hint" style={{ marginTop: '1.25rem' }}>
          <Link to="/jobs" className="header__link">
            Job board
          </Link>
          <span style={{ margin: '0 0.5rem', color: 'var(--border-strong)' }}>|</span>
          <Link to="/articles" className="header__link">
            Articles
          </Link>
        </p>
      </section>

      <section>
        <header className="page-head">
          <h2 className="page-head__title">Companies with published experiences</h2>
          <p className="page-head__lede">
            Only firms that already have at least one approved write-up appear here.
          </p>
        </header>

        {isLoading && (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
            <span className="spinner" />
          </div>
        )}
        {isError && <p className="field__error">Could not load companies. Is the API running?</p>}
        {!isLoading && !isError && companies && companies.length === 0 && (
          <div className="empty">No companies yet. Be the first to share an experience.</div>
        )}
        {companies && companies.length > 0 && (
          <div className="grid-companies">
            {companies.map((c) => (
              <Link key={c._id} to={`/c/${c.slug}`} className="company-card__link">
                <article className="card card--interactive">
                  <div className="card__body">
                    <div className="company-card__row">
                      <img className="company-card__logo" src={c.logo} alt="" loading="lazy" />
                      <div className="company-card__text">
                        <h3 className="company-card__name">{c.name}</h3>
                      </div>
                    </div>
                  </div>
                </article>
              </Link>
            ))}
          </div>
        )}
      </section>
    </>
  );
}
