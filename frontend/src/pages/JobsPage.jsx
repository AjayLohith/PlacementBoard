import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { fetchJobs } from '../api/jobs.js';

const PAGE_SIZE = 8;

export function JobsPage() {
  const [page, setPage] = useState(0);
  const [audience, setAudience] = useState('ALL');

  useEffect(() => {
    setPage(0);
  }, [audience]);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['jobs', page, audience],
    queryFn: () => fetchJobs({ page, size: PAGE_SIZE, audience }),
  });

  const jobs = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Opportunities</p>
        <h1 className="page-head__title">Job board</h1>
        <p className="page-head__lede">Postings from admins. Use apply links where provided.</p>
      </header>

      <div className="filter-tabs">
        <button
          type="button"
          className={`tabs__btn ${audience === 'ALL' ? 'tabs__btn--active' : ''}`}
          onClick={() => setAudience('ALL')}
        >
          All
        </button>
        <button
          type="button"
          className={`tabs__btn ${audience === 'FRESHERS' ? 'tabs__btn--active' : ''}`}
          onClick={() => setAudience('FRESHERS')}
        >
          Freshers
        </button>
        <button
          type="button"
          className={`tabs__btn ${audience === 'EXPERIENCED' ? 'tabs__btn--active' : ''}`}
          onClick={() => setAudience('EXPERIENCED')}
        >
          Experienced
        </button>
      </div>

      {isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <span className="spinner" />
        </div>
      )}
      {isError && <p className="field__error">Could not load job postings.</p>}
      {!isLoading && !isError && jobs.length === 0 && (
        <div className="empty">No active listings for this filter.</div>
      )}
      {!isLoading && jobs.length > 0 && (
        <>
          <div className="card-grid">
            {jobs.map((j) => (
              <article key={j._id} className="card">
                <div className="card__body">
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'baseline' }}>
                    <h2 className="page-head__title" style={{ fontSize: '1.2rem', margin: 0 }}>
                      {j.title}
                    </h2>
                    {j.jobType ? <span className="badge badge--accent">{j.jobType}</span> : null}
                    {j.audienceTag === 'FRESHERS' ? (
                      <span className="badge badge--accent">Freshers</span>
                    ) : j.audienceTag === 'EXPERIENCED' ? (
                      <span className="badge">Experienced</span>
                    ) : null}
                  </div>
                  {j.companyName ? (
                    <p className="field__hint" style={{ marginTop: '0.35rem' }}>
                      {j.companyName}
                      {j.location ? ` - ${j.location}` : ''}
                    </p>
                  ) : j.location ? (
                    <p className="field__hint" style={{ marginTop: '0.35rem' }}>
                      {j.location}
                    </p>
                  ) : null}
                  {(j.qualificationMajor || j.qualificationBranch || j.qualificationYear) && (
                    <p className="field__hint" style={{ marginTop: '0.5rem' }}>
                      <strong style={{ color: 'var(--ink-soft)' }}>Qualification: </strong>
                      {[j.qualificationMajor, j.qualificationBranch, j.qualificationYear]
                        .filter(Boolean)
                        .join(' · ')}
                    </p>
                  )}
                  {j.experienceText ? (
                    <p className="field__hint" style={{ marginTop: '0.35rem' }}>
                      <strong style={{ color: 'var(--ink-soft)' }}>Experience: </strong>
                      {j.experienceText}
                    </p>
                  ) : null}
                  {(j.passoutYear || j.postedOn) && (
                    <p className="field__hint" style={{ marginTop: '0.5rem' }}>
                      {j.passoutYear ? `Passout: ${j.passoutYear}` : ''}
                      {j.passoutYear && j.postedOn ? ' · ' : ''}
                      {j.postedOn || ''}
                    </p>
                  )}
                  <p style={{ marginTop: '0.75rem', whiteSpace: 'pre-wrap' }}>{j.description}</p>
                  {j.skillsRequired ? (
                    <section style={{ marginTop: '1rem' }}>
                      <h3 className="page-head__eyebrow" style={{ marginBottom: '0.35rem' }}>
                        Skills
                      </h3>
                      <p style={{ whiteSpace: 'pre-wrap', fontSize: '0.95rem' }}>{j.skillsRequired}</p>
                    </section>
                  ) : null}
                  {j.applyLink ? (
                    <p style={{ marginTop: '1rem' }}>
                      <a className="btn btn--primary btn--sm" href={j.applyLink} target="_blank" rel="noreferrer">
                        Apply
                      </a>
                    </p>
                  ) : null}
                </div>
              </article>
            ))}
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
