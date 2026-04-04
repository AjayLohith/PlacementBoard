import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { useState } from 'react';
import { toast } from 'sonner';
import {
  createArticle,
  deleteArticle,
  fetchArticlesAdmin,
  updateArticle,
} from '../api/articles.js';
import { aiParseArticle, aiParseJob } from '../api/adminAi.js';
import { getApiErrorMessage } from '../api/errors.js';
import {
  approveExperience,
  deleteExperience,
  fetchApprovedExperiences,
  fetchPendingExperiences,
  rejectExperience,
} from '../api/experiences.js';
import { createJob, deleteJob, fetchJobsAdmin, updateJob } from '../api/jobs.js';

function ExperienceAdminRow({ exp, busy, onApprove, onReject, onDelete }) {
  let dateLabel = exp.interviewDate;
  try {
    dateLabel = format(parseISO(exp.interviewDate), 'MMMM d, yyyy');
  } catch {
    try {
      dateLabel = format(parseISO(exp.interviewDate), 'MMM d, yyyy');
    } catch {
      /* keep raw */
    }
  }

  return (
    <article className="card experience-card" style={{ marginBottom: '1rem' }}>
      <div className="card__body">
        <div
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            gap: '0.5rem',
            alignItems: 'center',
            marginBottom: '0.35rem',
          }}
        >
          <h2 className="page-head__title" style={{ fontSize: '1.25rem', margin: 0 }}>
            {exp.postTitle}
          </h2>
          {exp.isApproved ? (
            <span className="badge badge--accent">Live</span>
          ) : (
            <span className="badge badge--warn">Pending</span>
          )}
          {exp.rejected ? <span className="badge">Rejected</span> : null}
        </div>
        <div className="experience-card__meta" style={{ marginBottom: '0.75rem' }}>
          <span>
            <strong>{exp.company?.name ?? 'Company'}</strong>
            {exp.user?.name ? (
              <>
                {' '}
                - shared by <strong>{exp.user.name}</strong>
              </>
            ) : null}
          </span>
          <span>Interview: {dateLabel}</span>
        </div>
        <p style={{ fontSize: '0.85rem', color: 'var(--muted)', marginBottom: '1rem' }}>
          Document ID <code>{exp._id}</code>
          {exp.interviewRounds?.length != null ? ` - ${exp.interviewRounds.length} round(s)` : ''}
        </p>

        {exp.interviewRounds && exp.interviewRounds.length > 0 && (
          <section style={{ marginTop: '0.5rem' }}>
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
            <p style={{ marginTop: '0.35rem', whiteSpace: 'pre-wrap' }}>{exp.suggestions}</p>
          </section>
        ) : null}
        {exp.additionalInfo ? (
          <section style={{ marginTop: '1rem' }}>
            <h3 className="page-head__eyebrow">Extra detail</h3>
            <p style={{ marginTop: '0.35rem', whiteSpace: 'pre-wrap' }}>{exp.additionalInfo}</p>
          </section>
        ) : null}
        {exp.closingNote ? (
          <section style={{ marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border)' }}>
            <p style={{ fontStyle: 'italic', color: 'var(--ink-soft)', whiteSpace: 'pre-wrap' }}>{exp.closingNote}</p>
          </section>
        ) : null}
        {exp.rejectionNote ? (
          <section style={{ marginTop: '1rem' }}>
            <h3 className="page-head__eyebrow">Rejection note</h3>
            <p style={{ marginTop: '0.35rem' }}>{exp.rejectionNote}</p>
          </section>
        ) : null}

        <div className="divider" style={{ margin: '1.25rem 0' }} />

        <div className="form-actions" style={{ marginTop: 0 }}>
          {!exp.isApproved ? (
            <button type="button" className="btn btn--primary btn--sm" disabled={busy} onClick={onApprove}>
              Approve
            </button>
          ) : null}
          {!exp.isApproved ? (
            <button type="button" className="btn btn--secondary btn--sm" disabled={busy} onClick={onReject}>
              Reject
            </button>
          ) : null}
          <button type="button" className="btn btn--danger btn--sm" disabled={busy} onClick={onDelete}>
            Delete
          </button>
        </div>
      </div>
    </article>
  );
}

export function AdminPage() {
  const queryClient = useQueryClient();
  const [section, setSection] = useState('experiences');
  const [expTab, setExpTab] = useState('pending');
  const [busyId, setBusyId] = useState(null);

  const [jobForm, setJobForm] = useState({
    title: '',
    companyName: '',
    description: '',
    applyLink: '',
    location: '',
    jobType: '',
    skillsRequired: '',
    passoutYear: '',
    postedOn: '',
    active: true,
  });
  const [editingJobId, setEditingJobId] = useState(null);
  const [jobAiPaste, setJobAiPaste] = useState('');

  const [articleForm, setArticleForm] = useState({
    title: '',
    slug: '',
    excerpt: '',
    body: '',
    published: false,
  });
  const [editingArticleId, setEditingArticleId] = useState(null);
  const [articleAiPaste, setArticleAiPaste] = useState('');

  const parseJobAiM = useMutation({
    mutationFn: () => aiParseJob(jobAiPaste),
    onSuccess: (data) => {
      setJobForm((f) => ({
        ...f,
        title: data.title ?? f.title,
        companyName: data.companyName ?? f.companyName,
        description: data.description ?? f.description,
        applyLink: data.applyLink ?? f.applyLink,
        location: data.location ?? f.location,
        jobType: data.jobType ?? f.jobType,
        skillsRequired: data.skillsRequired ?? f.skillsRequired,
        passoutYear: data.passoutYear ?? f.passoutYear,
        postedOn: data.postedOn ?? f.postedOn,
      }));
      toast.success('Job fields filled from AI — review and publish.');
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const parseArticleAiM = useMutation({
    mutationFn: () => aiParseArticle(articleAiPaste),
    onSuccess: (data) => {
      setArticleForm({
        title: data.title ?? '',
        slug: data.slug ?? '',
        excerpt: data.excerpt ?? '',
        body: data.body ?? '',
        published: Boolean(data.published),
      });
      toast.success('Article fields filled from AI — review then save.');
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const pendingQ = useQuery({
    queryKey: ['admin', 'pending'],
    queryFn: fetchPendingExperiences,
    enabled: section === 'experiences' && expTab === 'pending',
  });

  const approvedQ = useQuery({
    queryKey: ['admin', 'approved'],
    queryFn: fetchApprovedExperiences,
    enabled: section === 'experiences' && expTab === 'approved',
  });

  const jobsAdminQ = useQuery({
    queryKey: ['admin', 'jobs'],
    queryFn: fetchJobsAdmin,
    enabled: section === 'jobs',
  });

  const articlesAdminQ = useQuery({
    queryKey: ['admin', 'articles'],
    queryFn: fetchArticlesAdmin,
    enabled: section === 'articles',
  });

  const approveM = useMutation({
    mutationFn: approveExperience,
    onSuccess: () => {
      toast.success('Approved');
      queryClient.invalidateQueries({ queryKey: ['admin'] });
      queryClient.invalidateQueries({ queryKey: ['companies'] });
      queryClient.invalidateQueries({ queryKey: ['experiences'] });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
    onSettled: () => setBusyId(null),
  });

  const rejectM = useMutation({
    mutationFn: ({ id, note }) => rejectExperience(id, note),
    onSuccess: () => {
      toast.success('Rejected');
      queryClient.invalidateQueries({ queryKey: ['admin'] });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
    onSettled: () => setBusyId(null),
  });

  const deleteM = useMutation({
    mutationFn: deleteExperience,
    onSuccess: () => {
      toast.success('Removed');
      queryClient.invalidateQueries({ queryKey: ['admin'] });
      queryClient.invalidateQueries({ queryKey: ['companies'] });
      queryClient.invalidateQueries({ queryKey: ['experiences'] });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
    onSettled: () => setBusyId(null),
  });

  const saveJobM = useMutation({
    mutationFn: async () => {
      const payload = {
        title: jobForm.title.trim(),
        companyName: jobForm.companyName.trim() || undefined,
        description: jobForm.description.trim(),
        applyLink: jobForm.applyLink.trim() || undefined,
        location: jobForm.location.trim() || undefined,
        jobType: jobForm.jobType.trim() || undefined,
        skillsRequired: jobForm.skillsRequired.trim() || undefined,
        passoutYear: jobForm.passoutYear.trim() || undefined,
        postedOn: jobForm.postedOn.trim() || undefined,
        active: jobForm.active,
      };
      if (editingJobId) {
        return updateJob(editingJobId, payload);
      }
      return createJob(payload);
    },
    onSuccess: () => {
      toast.success(editingJobId ? 'Job updated' : 'Job posted');
      queryClient.invalidateQueries({ queryKey: ['admin', 'jobs'] });
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
      setEditingJobId(null);
      setJobForm({
        title: '',
        companyName: '',
        description: '',
        applyLink: '',
        location: '',
        jobType: '',
        skillsRequired: '',
        passoutYear: '',
        postedOn: '',
        active: true,
      });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const delJobM = useMutation({
    mutationFn: deleteJob,
    onSuccess: () => {
      toast.success('Job removed');
      queryClient.invalidateQueries({ queryKey: ['admin', 'jobs'] });
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const saveArticleM = useMutation({
    mutationFn: async () => {
      const payload = {
        title: articleForm.title.trim(),
        slug: articleForm.slug.trim() || undefined,
        excerpt: articleForm.excerpt.trim() || undefined,
        body: articleForm.body.trim(),
        published: articleForm.published,
      };
      if (editingArticleId) {
        return updateArticle(editingArticleId, payload);
      }
      return createArticle(payload);
    },
    onSuccess: () => {
      toast.success(editingArticleId ? 'Article updated' : 'Article saved');
      queryClient.invalidateQueries({ queryKey: ['admin', 'articles'] });
      queryClient.invalidateQueries({ queryKey: ['articles'] });
      setEditingArticleId(null);
      setArticleForm({ title: '', slug: '', excerpt: '', body: '', published: false });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const delArticleM = useMutation({
    mutationFn: deleteArticle,
    onSuccess: () => {
      toast.success('Article removed');
      queryClient.invalidateQueries({ queryKey: ['admin', 'articles'] });
      queryClient.invalidateQueries({ queryKey: ['articles'] });
    },
    onError: (e) => toast.error(getApiErrorMessage(e)),
  });

  const expList = expTab === 'pending' ? pendingQ.data : approvedQ.data;
  const expLoading = expTab === 'pending' ? pendingQ.isLoading : approvedQ.isLoading;

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Admin</p>
        <h1 className="page-head__title">Admin corner</h1>
        <p className="page-head__lede">Moderate experiences, publish jobs, and manage articles.</p>
      </header>

      <div className="tabs" style={{ marginBottom: '1.5rem' }}>
        <button
          type="button"
          className={`tabs__btn ${section === 'experiences' ? 'tabs__btn--active' : ''}`}
          onClick={() => setSection('experiences')}
        >
          Experiences
        </button>
        <button
          type="button"
          className={`tabs__btn ${section === 'jobs' ? 'tabs__btn--active' : ''}`}
          onClick={() => setSection('jobs')}
        >
          Job postings
        </button>
        <button
          type="button"
          className={`tabs__btn ${section === 'articles' ? 'tabs__btn--active' : ''}`}
          onClick={() => setSection('articles')}
        >
          Articles
        </button>
      </div>

      {section === 'experiences' && (
        <>
          <div className="tabs">
            <button
              type="button"
              className={`tabs__btn ${expTab === 'pending' ? 'tabs__btn--active' : ''}`}
              onClick={() => setExpTab('pending')}
            >
              Pending
            </button>
            <button
              type="button"
              className={`tabs__btn ${expTab === 'approved' ? 'tabs__btn--active' : ''}`}
              onClick={() => setExpTab('approved')}
            >
              Approved
            </button>
          </div>

          {expLoading && (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
              <span className="spinner" />
            </div>
          )}
          {!expLoading && expList && expList.length === 0 && (
            <div className="empty">{expTab === 'pending' ? 'Queue is empty.' : 'No approved records.'}</div>
          )}
          {expList?.map((exp) => (
            <ExperienceAdminRow
              key={exp._id}
              exp={exp}
              busy={busyId === exp._id}
              onApprove={() => {
                setBusyId(exp._id);
                approveM.mutate(exp._id);
              }}
              onReject={() => {
                const note = window.prompt('Optional rejection note (Cancel to abort):');
                if (note === null) return;
                setBusyId(exp._id);
                rejectM.mutate({ id: exp._id, note });
              }}
              onDelete={() => {
                if (window.confirm('Delete this experience permanently?')) {
                  setBusyId(exp._id);
                  deleteM.mutate(exp._id);
                }
              }}
            />
          ))}
        </>
      )}

      {section === 'jobs' && (
        <>
          <div className="card" style={{ marginBottom: '1.5rem', background: 'var(--surface)' }}>
            <div className="card__body">
              <h3 className="page-head__title" style={{ fontSize: '1.05rem', marginBottom: '0.5rem' }}>
                AI: paste job text (one box)
              </h3>
              <p className="field__hint" style={{ marginBottom: '0.75rem' }}>
                Paste a full job forward, JD, or link dump. One click fills title, company, description, apply link,
                location, type, skills, passout year, and posted/deadline line. Uses the server-side{' '}
                <code>AI_API_KEY</code> only — it is never sent to the browser.
              </p>
              <textarea
                className="textarea"
                rows={5}
                placeholder="Paste everything here…"
                value={jobAiPaste}
                onChange={(e) => setJobAiPaste(e.target.value)}
              />
              <div className="form-actions" style={{ marginTop: '0.75rem' }}>
                <button
                  type="button"
                  className="btn btn--primary btn--sm"
                  disabled={!jobAiPaste.trim() || parseJobAiM.isPending}
                  onClick={() => parseJobAiM.mutate()}
                >
                  {parseJobAiM.isPending ? <span className="spinner" /> : 'Fill job fields with AI'}
                </button>
              </div>
            </div>
          </div>

          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div className="card__body">
              <h3 className="page-head__title" style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>
                {editingJobId ? 'Edit job' : 'New job posting'}
              </h3>
              <div className="field">
                <label className="field__label" htmlFor="j-title">
                  Title
                </label>
                <input
                  id="j-title"
                  className="input"
                  value={jobForm.title}
                  onChange={(e) => setJobForm((f) => ({ ...f, title: e.target.value }))}
                />
              </div>
              <div className="inline-fields inline-fields--2">
                <div className="field">
                  <label className="field__label" htmlFor="j-co">
                    Company
                  </label>
                  <input
                    id="j-co"
                    className="input"
                    value={jobForm.companyName}
                    onChange={(e) => setJobForm((f) => ({ ...f, companyName: e.target.value }))}
                  />
                </div>
                <div className="field">
                  <label className="field__label" htmlFor="j-loc">
                    Location
                  </label>
                  <input
                    id="j-loc"
                    className="input"
                    value={jobForm.location}
                    onChange={(e) => setJobForm((f) => ({ ...f, location: e.target.value }))}
                  />
                </div>
              </div>
              <div className="inline-fields inline-fields--2">
                <div className="field">
                  <label className="field__label" htmlFor="j-type">
                    Type
                  </label>
                  <input
                    id="j-type"
                    className="input"
                    placeholder="intern, full-time"
                    value={jobForm.jobType}
                    onChange={(e) => setJobForm((f) => ({ ...f, jobType: e.target.value }))}
                  />
                </div>
                <div className="field">
                  <label className="field__label" htmlFor="j-link">
                    Apply link
                  </label>
                  <input
                    id="j-link"
                    className="input"
                    value={jobForm.applyLink}
                    onChange={(e) => setJobForm((f) => ({ ...f, applyLink: e.target.value }))}
                  />
                </div>
              </div>
              <div className="inline-fields inline-fields--2">
                <div className="field">
                  <label className="field__label" htmlFor="j-passout">
                    Passout year / batches
                  </label>
                  <input
                    id="j-passout"
                    className="input"
                    placeholder="e.g. 2025, 2025–2026"
                    value={jobForm.passoutYear}
                    onChange={(e) => setJobForm((f) => ({ ...f, passoutYear: e.target.value }))}
                  />
                </div>
                <div className="field">
                  <label className="field__label" htmlFor="j-posted">
                    Posted on / deadline
                  </label>
                  <input
                    id="j-posted"
                    className="input"
                    placeholder="e.g. Deadline 20 Feb 2026"
                    value={jobForm.postedOn}
                    onChange={(e) => setJobForm((f) => ({ ...f, postedOn: e.target.value }))}
                  />
                </div>
              </div>
              <div className="field">
                <label className="field__label" htmlFor="j-skills">
                  Skills required
                </label>
                <textarea
                  id="j-skills"
                  className="textarea"
                  rows={3}
                  placeholder="Languages, frameworks, tools…"
                  value={jobForm.skillsRequired}
                  onChange={(e) => setJobForm((f) => ({ ...f, skillsRequired: e.target.value }))}
                />
              </div>
              <div className="field">
                <label className="field__label" htmlFor="j-desc">
                  Description
                </label>
                <textarea
                  id="j-desc"
                  className="textarea"
                  rows={4}
                  value={jobForm.description}
                  onChange={(e) => setJobForm((f) => ({ ...f, description: e.target.value }))}
                />
              </div>
              <label className="field__hint" style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <input
                  type="checkbox"
                  checked={jobForm.active}
                  onChange={(e) => setJobForm((f) => ({ ...f, active: e.target.checked }))}
                />
                Active (visible on public job board)
              </label>
              <div className="form-actions" style={{ marginTop: '1rem' }}>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={!jobForm.title.trim() || !jobForm.description.trim() || saveJobM.isPending}
                  onClick={() => saveJobM.mutate()}
                >
                  {saveJobM.isPending ? <span className="spinner" /> : editingJobId ? 'Save changes' : 'Publish job'}
                </button>
                {editingJobId ? (
                  <button
                    type="button"
                    className="btn btn--ghost"
                    onClick={() => {
                      setEditingJobId(null);
                      setJobForm({
                        title: '',
                        companyName: '',
                        description: '',
                        applyLink: '',
                        location: '',
                        jobType: '',
                        skillsRequired: '',
                        passoutYear: '',
                        postedOn: '',
                        active: true,
                      });
                    }}
                  >
                    Cancel edit
                  </button>
                ) : null}
              </div>
            </div>
          </div>

          {jobsAdminQ.isLoading && (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
              <span className="spinner" />
            </div>
          )}
          {jobsAdminQ.data?.map((j) => (
            <article key={j._id} className="card" style={{ marginBottom: '1rem' }}>
              <div className="card__body">
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'center' }}>
                  <strong>{j.title}</strong>
                  {j.active ? <span className="badge badge--accent">Active</span> : <span className="badge">Hidden</span>}
                </div>
                <p className="field__hint" style={{ marginTop: '0.35rem' }}>
                  {j.companyName || 'No company'} - {j.location || 'no location'}
                </p>
                <div className="form-actions" style={{ marginTop: '0.75rem' }}>
                  <button
                    type="button"
                    className="btn btn--secondary btn--sm"
                    onClick={() => {
                      setEditingJobId(j._id);
                      setJobForm({
                        title: j.title || '',
                        companyName: j.companyName || '',
                        description: j.description || '',
                        applyLink: j.applyLink || '',
                        location: j.location || '',
                        jobType: j.jobType || '',
                        skillsRequired: j.skillsRequired || '',
                        passoutYear: j.passoutYear || '',
                        postedOn: j.postedOn || '',
                        active: j.active !== false,
                      });
                    }}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="btn btn--danger btn--sm"
                    onClick={() => {
                      if (window.confirm('Delete this job posting?')) delJobM.mutate(j._id);
                    }}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </article>
          ))}
        </>
      )}

      {section === 'articles' && (
        <>
          <div className="card" style={{ marginBottom: '1.5rem', background: 'var(--surface)' }}>
            <div className="card__body">
              <h3 className="page-head__title" style={{ fontSize: '1.05rem', marginBottom: '0.5rem' }}>
                AI: paste notes for an article
              </h3>
              <p className="field__hint" style={{ marginBottom: '0.75rem' }}>
                Paste rough notes or bullets; AI returns a structured, polished article that follows your intent
                (title, slug, excerpt, full body). Review and edit before saving.
              </p>
              <textarea
                className="textarea"
                rows={5}
                placeholder="Paste rough notes, outline, or links…"
                value={articleAiPaste}
                onChange={(e) => setArticleAiPaste(e.target.value)}
              />
              <div className="form-actions" style={{ marginTop: '0.75rem' }}>
                <button
                  type="button"
                  className="btn btn--primary btn--sm"
                  disabled={!articleAiPaste.trim() || parseArticleAiM.isPending}
                  onClick={() => parseArticleAiM.mutate()}
                >
                  {parseArticleAiM.isPending ? <span className="spinner" /> : 'Fill article fields with AI'}
                </button>
              </div>
            </div>
          </div>

          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div className="card__body">
              <h3 className="page-head__title" style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>
                {editingArticleId ? 'Edit article' : 'New article'}
              </h3>
              <div className="field">
                <label className="field__label" htmlFor="a-title">
                  Title
                </label>
                <input
                  id="a-title"
                  className="input"
                  value={articleForm.title}
                  onChange={(e) => setArticleForm((f) => ({ ...f, title: e.target.value }))}
                />
              </div>
              <div className="field">
                <label className="field__label" htmlFor="a-slug">
                  Slug (optional)
                </label>
                <input
                  id="a-slug"
                  className="input"
                  placeholder="auto from title if empty"
                  value={articleForm.slug}
                  onChange={(e) => setArticleForm((f) => ({ ...f, slug: e.target.value }))}
                />
              </div>
              <div className="field">
                <label className="field__label" htmlFor="a-ex">
                  Excerpt
                </label>
                <textarea
                  id="a-ex"
                  className="textarea"
                  rows={2}
                  value={articleForm.excerpt}
                  onChange={(e) => setArticleForm((f) => ({ ...f, excerpt: e.target.value }))}
                />
              </div>
              <div className="field">
                <label className="field__label" htmlFor="a-body">
                  Body
                </label>
                <textarea
                  id="a-body"
                  className="textarea"
                  rows={8}
                  value={articleForm.body}
                  onChange={(e) => setArticleForm((f) => ({ ...f, body: e.target.value }))}
                />
              </div>
              <label className="field__hint" style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <input
                  type="checkbox"
                  checked={articleForm.published}
                  onChange={(e) => setArticleForm((f) => ({ ...f, published: e.target.checked }))}
                />
                Published (visible on Articles page)
              </label>
              <div className="form-actions" style={{ marginTop: '1rem' }}>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={!articleForm.title.trim() || !articleForm.body.trim() || saveArticleM.isPending}
                  onClick={() => saveArticleM.mutate()}
                >
                  {saveArticleM.isPending ? (
                    <span className="spinner" />
                  ) : editingArticleId ? (
                    'Save article'
                  ) : (
                    'Create article'
                  )}
                </button>
                {editingArticleId ? (
                  <button
                    type="button"
                    className="btn btn--ghost"
                    onClick={() => {
                      setEditingArticleId(null);
                      setArticleForm({ title: '', slug: '', excerpt: '', body: '', published: false });
                    }}
                  >
                    Cancel edit
                  </button>
                ) : null}
              </div>
            </div>
          </div>

          {articlesAdminQ.isLoading && (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
              <span className="spinner" />
            </div>
          )}
          {articlesAdminQ.data?.map((a) => (
            <article key={a._id} className="card" style={{ marginBottom: '1rem' }}>
              <div className="card__body">
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'center' }}>
                  <strong>{a.title}</strong>
                  {a.published ? (
                    <span className="badge badge--accent">Published</span>
                  ) : (
                    <span className="badge badge--warn">Draft</span>
                  )}
                </div>
                <p className="field__hint" style={{ marginTop: '0.35rem' }}>
                  /articles/{a.slug}
                </p>
                <div className="form-actions" style={{ marginTop: '0.75rem' }}>
                  <button
                    type="button"
                    className="btn btn--secondary btn--sm"
                    onClick={() => {
                      setEditingArticleId(a._id);
                      setArticleForm({
                        title: a.title || '',
                        slug: a.slug || '',
                        excerpt: a.excerpt || '',
                        body: a.body || '',
                        published: Boolean(a.published),
                      });
                    }}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="btn btn--danger btn--sm"
                    onClick={() => {
                      if (window.confirm('Delete this article?')) delArticleM.mutate(a._id);
                    }}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </article>
          ))}
        </>
      )}
    </>
  );
}
