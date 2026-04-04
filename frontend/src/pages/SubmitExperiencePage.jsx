import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useFieldArray, useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
import { z } from 'zod';
import { createExperience } from '../api/experiences.js';
import { getApiErrorMessage } from '../api/errors.js';

const roundSchema = z.object({
  roundName: z.string().optional(),
  duration: z.string().optional(),
  method: z.string().optional(),
  focus: z.string().optional(),
  keyQuestions: z.string().optional(),
  obstacles: z.string().optional(),
});

const schema = z.object({
  postTitle: z.string().min(3, 'Title required'),
  companyName: z.string().min(2, 'Company name required'),
  interviewDate: z.string().min(1, 'Pick a date'),
  interviewRounds: z.array(roundSchema).optional(),
  suggestions: z.string().optional(),
  additionalInfo: z.string().optional(),
  closingNote: z.string().optional(),
});

export function SubmitExperiencePage() {
  const queryClient = useQueryClient();

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      interviewRounds: [{ roundName: '', duration: '', method: '', focus: '', keyQuestions: '', obstacles: '' }],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: 'interviewRounds' });

  const mutation = useMutation({
    mutationFn: async (values) => {
      const iso = new Date(values.interviewDate + 'T12:00:00').toISOString();
      const rounds = values.interviewRounds?.filter(
        (r) =>
          r.roundName ||
          r.duration ||
          r.method ||
          r.focus ||
          r.keyQuestions ||
          r.obstacles,
      );
      return createExperience({
        postTitle: values.postTitle.trim(),
        companyName: values.companyName.trim(),
        interviewDate: iso,
        interviewRounds: rounds && rounds.length ? rounds : undefined,
        suggestions: values.suggestions?.trim() || undefined,
        additionalInfo: values.additionalInfo?.trim() || undefined,
        closingNote: values.closingNote?.trim() || undefined,
      });
    },
    onSuccess: () => {
      toast.success('Submitted for review. Thanks for contributing.');
      queryClient.invalidateQueries({ queryKey: ['companies'] });
      reset();
    },
    onError: (err) => {
      toast.error(getApiErrorMessage(err));
    },
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Contribute</p>
        <h1 className="page-head__title">Share an experience</h1>
        <p className="page-head__lede">
          Submissions are reviewed before they appear publicly. Be specific; redact anything personal or
          confidential.
        </p>
      </header>

      <div className="form-panel form-panel--wide">
        <form className="card" onSubmit={handleSubmit((v) => mutation.mutate(v))} noValidate>
          <div className="card__body">
            <div className="field">
              <label className="field__label" htmlFor="postTitle">
                Title
              </label>
              <input id="postTitle" className="input" {...register('postTitle')} placeholder="e.g. SDE intern, off-campus" />
              {errors.postTitle && <p className="field__error">{errors.postTitle.message}</p>}
            </div>
            <div className="inline-fields inline-fields--2">
              <div className="field">
                <label className="field__label" htmlFor="companyName">
                  Company name
                </label>
                <input id="companyName" className="input" {...register('companyName')} />
                {errors.companyName && <p className="field__error">{errors.companyName.message}</p>}
              </div>
              <div className="field">
                <label className="field__label" htmlFor="interviewDate">
                  Interview date
                </label>
                <input id="interviewDate" className="input" type="date" {...register('interviewDate')} />
                {errors.interviewDate && <p className="field__error">{errors.interviewDate.message}</p>}
              </div>
            </div>

            <div className="divider" />

            <h3 style={{ margin: '0 0 1rem', fontFamily: 'var(--font-display)' }}>Interview rounds</h3>
            {fields.map((field, index) => (
              <div key={field.id} className="card" style={{ marginBottom: '1rem', boxShadow: 'none' }}>
                <div className="card__body">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span className="badge badge--accent">Round {index + 1}</span>
                    {fields.length > 1 ? (
                      <button type="button" className="btn btn--ghost btn--sm" onClick={() => remove(index)}>
                        Remove
                      </button>
                    ) : null}
                  </div>
                  <div className="inline-fields inline-fields--2" style={{ marginTop: '0.75rem' }}>
                    <div className="field">
                      <label className="field__label">Round name</label>
                      <input className="input" {...register(`interviewRounds.${index}.roundName`)} />
                    </div>
                    <div className="field">
                      <label className="field__label">Duration</label>
                      <input className="input" {...register(`interviewRounds.${index}.duration`)} />
                    </div>
                  </div>
                  <div className="inline-fields inline-fields--2">
                    <div className="field">
                      <label className="field__label">Method</label>
                      <input className="input" {...register(`interviewRounds.${index}.method`)} placeholder="Online / on-site" />
                    </div>
                    <div className="field">
                      <label className="field__label">Focus</label>
                      <input className="input" {...register(`interviewRounds.${index}.focus`)} />
                    </div>
                  </div>
                  <div className="field">
                    <label className="field__label">Key questions</label>
                    <textarea className="textarea" rows={3} {...register(`interviewRounds.${index}.keyQuestions`)} />
                  </div>
                  <div className="field">
                    <label className="field__label">Obstacles / pressure points</label>
                    <textarea className="textarea" rows={2} {...register(`interviewRounds.${index}.obstacles`)} />
                  </div>
                </div>
              </div>
            ))}
            <button
              type="button"
              className="btn btn--secondary btn--sm"
              onClick={() =>
                append({ roundName: '', duration: '', method: '', focus: '', keyQuestions: '', obstacles: '' })
              }
            >
              Add round
            </button>

            <div className="divider" />

            <div className="field">
              <label className="field__label">Suggestions for others</label>
              <textarea className="textarea" rows={3} {...register('suggestions')} />
            </div>
            <div className="field">
              <label className="field__label">Additional context</label>
              <textarea className="textarea" rows={3} {...register('additionalInfo')} />
            </div>
            <div className="field">
              <label className="field__label">Closing note</label>
              <textarea className="textarea" rows={2} {...register('closingNote')} />
            </div>

            <div className="form-actions">
              <button className="btn btn--primary" type="submit" disabled={mutation.isPending}>
                {mutation.isPending ? <span className="spinner" /> : 'Submit for review'}
              </button>
              <Link className="btn btn--secondary" to="/companies">
                Cancel
              </Link>
            </div>
          </div>
        </form>
      </div>
    </>
  );
}
