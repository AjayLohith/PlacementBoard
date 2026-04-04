import { useQuery } from '@tanstack/react-query';
import { fetchJobs } from '../api/jobs.js';

export function JobsPage() {
  const { data: jobs, isLoading, isError } = useQuery({
    queryKey: ['jobs'],
    queryFn: fetchJobs,
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Opportunities</p>
        <h1 className="page-head__title">Job board</h1>
        <p className="page-head__lede">Postings from admins. Use apply links where provided.</p>
      </header>

      {isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
          <span className="spinner" />
        </div>
      )}
      {isError && <p className="field__error">Could not load job postings.</p>}
      {jobs && jobs.length === 0 && <div className="empty">No active listings yet.</div>}
      {jobs && jobs.length > 0 && (
        <div className="stack">
          {jobs.map((j) => (
            <article key={j._id} className="card">
              <div className="card__body">
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'baseline' }}>
                  <h2 className="page-head__title" style={{ fontSize: '1.2rem', margin: 0 }}>
                    {j.title}
                  </h2>
                  {j.jobType ? <span className="badge badge--accent">{j.jobType}</span> : null}
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
      )}
    </>
  );
}
