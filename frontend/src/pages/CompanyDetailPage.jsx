import { useQuery } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { Link, useParams } from 'react-router-dom';
import { fetchCompanies } from '../api/companies.js';
import { fetchExperiencesByCompany } from '../api/experiences.js';

function ExperienceArticle({ exp }) {
  let dateLabel = exp.interviewDate;
  try {
    dateLabel = format(parseISO(exp.interviewDate), 'MMMM d, yyyy');
  } catch {
    /* keep */
  }

  return (
    <article className="card experience-card">
      <div className="card__body">
        <h2 className="page-head__title" style={{ fontSize: '1.35rem', marginBottom: '0.35rem' }}>
          {exp.postTitle}
        </h2>
        <div className="experience-card__meta">
          <span>
            Shared by <strong>{exp.user.name}</strong>
          </span>
          <span>Interview: {dateLabel}</span>
        </div>
        {exp.interviewRounds && exp.interviewRounds.length > 0 && (
          <section style={{ marginTop: '1rem' }}>
            <h3 className="page-head__eyebrow" style={{ marginBottom: '0.75rem' }}>
              Rounds
            </h3>
            {exp.interviewRounds.map((r, i) => (
              <div key={i} className="round-block">
                <p className="round-block__title">{r.roundName || `Round ${i + 1}`}</p>
                <div className="stack" style={{ gap: '0.35rem', fontSize: '0.92rem', color: 'var(--muted)' }}>
                  {r.duration ? <p>Duration: {r.duration}</p> : null}
                  {r.method ? <p>Format: {r.method}</p> : null}
                  {r.focus ? <p>Focus: {r.focus}</p> : null}
                  {r.keyQuestions ? (
                    <p>
                      <strong style={{ color: 'var(--ink-soft)' }}>Questions:</strong> {r.keyQuestions}
                    </p>
                  ) : null}
                  {r.obstacles ? (
                    <p>
                      <strong style={{ color: 'var(--ink-soft)' }}>Rough spots:</strong> {r.obstacles}
                    </p>
                  ) : null}
                </div>
              </div>
            ))}
          </section>
        )}
        {exp.suggestions ? (
          <section style={{ marginTop: '1rem' }}>
            <h3 className="page-head__eyebrow">Suggestions</h3>
            <p style={{ marginTop: '0.35rem' }}>{exp.suggestions}</p>
          </section>
        ) : null}
        {exp.additionalInfo ? (
          <section style={{ marginTop: '1rem' }}>
            <h3 className="page-head__eyebrow">Extra detail</h3>
            <p style={{ marginTop: '0.35rem' }}>{exp.additionalInfo}</p>
          </section>
        ) : null}
        {exp.closingNote ? (
          <section style={{ marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border)' }}>
            <p style={{ fontStyle: 'italic', color: 'var(--ink-soft)' }}>{exp.closingNote}</p>
          </section>
        ) : null}
      </div>
    </article>
  );
}

export function CompanyDetailPage() {
  const { slug } = useParams();

  const companiesQ = useQuery({
    queryKey: ['companies'],
    queryFn: fetchCompanies,
  });

  const q = useQuery({
    queryKey: ['experiences', slug],
    queryFn: () => fetchExperiencesByCompany(slug),
    enabled: Boolean(slug),
  });

  const fromDirectory = companiesQ.data?.find((c) => c.slug === slug);
  const companyName = q.data?.[0]?.company?.name ?? fromDirectory?.name;
  const companyLogo = fromDirectory?.logo;

  if (!slug) {
    return <p className="field__error">Invalid link.</p>;
  }

  return (
    <>
      <p style={{ marginBottom: '1rem' }}>
        <Link to="/companies" className="header__link">
          All companies
        </Link>
      </p>

      {q.isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <span className="spinner" />
        </div>
      )}

      {q.isError && (
        <div className="empty">
          <p>Company not found or no public experiences.</p>
          <div style={{ marginTop: '1rem' }}>
            <Link className="btn btn--secondary" to="/companies">
              Back to directory
            </Link>
          </div>
        </div>
      )}

      {q.data && (
        <>
          <header className="page-head">
            {companyLogo ? (
              <div className="company-card__row" style={{ marginBottom: '1rem' }}>
                <img className="company-card__logo" src={companyLogo} alt="" width={72} height={72} />
                <div>
                  <h1 className="page-head__title">{companyName || slug}</h1>
                  <p className="page-head__lede">{q.data.length} published experience(s).</p>
                </div>
              </div>
            ) : (
              <>
                <h1 className="page-head__title">{companyName || slug}</h1>
                <p className="page-head__lede">{q.data.length} published experience(s).</p>
              </>
            )}
          </header>
          {q.data.length === 0 ? (
            <div className="empty">No approved write-ups for this company yet.</div>
          ) : (
            <div className="stack">
              {q.data.map((exp) => (
                <ExperienceArticle key={exp._id} exp={exp} />
              ))}
            </div>
          )}
        </>
      )}
    </>
  );
}
