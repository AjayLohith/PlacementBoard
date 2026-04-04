import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
import { requestPasswordResetOtp, resetPasswordWithOtp } from '../api/auth.js';
import { getApiErrorMessage } from '../api/errors.js';

export function ForgotPasswordPage() {
  const [step, setStep] = useState('email');
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');

  const sendOtp = useMutation({
    mutationFn: async () => {
      await requestPasswordResetOtp(email.trim().toLowerCase());
      setStep('code');
    },
    onSuccess: () => {
      toast.success('If this email is registered, a 6-digit code was sent to your inbox.');
    },
    onError: (err) => {
      toast.error(getApiErrorMessage(err));
    },
  });

  const finish = useMutation({
    mutationFn: async () => {
      await resetPasswordWithOtp(email.trim().toLowerCase(), otp.trim(), newPassword);
      setStep('done');
    },
    onSuccess: () => {
      toast.success('Password updated');
    },
    onError: (err) => {
      toast.error(getApiErrorMessage(err));
    },
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Recovery</p>
        <h1 className="page-head__title">Reset password</h1>
        <p className="page-head__lede">
          We email a one-time 6-digit code to your address (valid 10 minutes). Use the Gmail account you
          configured for the server.
        </p>
      </header>

      <div className="form-panel form-panel--wide">
        {step === 'email' && (
          <div className="card">
            <div className="card__body">
              <div className="field">
                <label className="field__label" htmlFor="re-email">
                  Account email
                </label>
                <input
                  id="re-email"
                  className="input"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="email"
                />
              </div>
              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={!email.trim() || sendOtp.isPending}
                  onClick={() => sendOtp.mutate()}
                >
                  {sendOtp.isPending ? <span className="spinner" /> : 'Send code'}
                </button>
              </div>
              <p className="field__hint" style={{ marginTop: '1rem' }}>
                <Link to="/login">Back to sign in</Link>
              </p>
            </div>
          </div>
        )}

        {step === 'code' && (
          <div className="card">
            <div className="card__body">
              <p className="field__hint" style={{ marginBottom: '1rem' }}>
                Enter the code from your email and choose a new password.
              </p>
              <div className="field">
                <label className="field__label" htmlFor="otp">
                  6-digit code
                </label>
                <input
                  id="otp"
                  className="input"
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  maxLength={6}
                  placeholder="000000"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                />
              </div>
              <div className="field">
                <label className="field__label" htmlFor="np">
                  New password
                </label>
                <input
                  id="np"
                  className="input"
                  type="password"
                  autoComplete="new-password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
                <p className="field__hint">At least 8 characters.</p>
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn--secondary" onClick={() => setStep('email')}>
                  Back
                </button>
                <button
                  type="button"
                  className="btn btn--primary"
                  disabled={otp.length !== 6 || newPassword.length < 8 || finish.isPending}
                  onClick={() => finish.mutate()}
                >
                  {finish.isPending ? <span className="spinner" /> : 'Update password'}
                </button>
              </div>
            </div>
          </div>
        )}

        {step === 'done' && (
          <div className="empty" style={{ background: 'var(--surface)' }}>
            <p style={{ color: 'var(--ink-soft)', marginBottom: '1rem' }}>
              You can sign in with your new password.
            </p>
            <Link className="btn btn--primary" to="/login">
              Go to sign in
            </Link>
          </div>
        )}
      </div>
    </>
  );
}
