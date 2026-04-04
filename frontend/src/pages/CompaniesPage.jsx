import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { fetchCompanies } from '../api/companies.js';

export function CompaniesPage() {
  const { data: companies, isLoading, isError } = useQuery({
    queryKey: ['companies'],
    queryFn: fetchCompanies,
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Directory</p>
        <h1 className="page-head__title">Companies</h1>
        <p className="page-head__lede">Open a company to read approved interview experiences.</p>
      </header>

      {isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <span className="spinner" />
        </div>
      )}
      {isError && <p className="field__error">Could not load companies.</p>}
      {companies && companies.length === 0 && (
        <div className="empty">Nothing listed yet.</div>
      )}
      {companies && companies.length > 0 && (
        <div className="grid-companies">
          {companies.map((c) => (
            <Link key={c._id} to={`/c/${c.slug}`} className="company-card__link">
              <article className="card card--interactive">
                <div className="card__body">
                  <div className="company-card__row">
                    <img className="company-card__logo" src={c.logo} alt="" loading="lazy" />
                    <div>
                      <h2 className="company-card__name">{c.name}</h2>
                      <p className="company-card__slug">View experiences &rarr;</p>
                    </div>
                  </div>
                </div>
              </article>
            </Link>
          ))}
        </div>
      )}
    </>
  );
}
