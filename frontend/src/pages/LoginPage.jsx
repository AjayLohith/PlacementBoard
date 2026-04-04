import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { z } from 'zod';
import { login } from '../api/auth.js';
import { getApiErrorMessage } from '../api/errors.js';
import { useAuth } from '../context/AuthContext.jsx';

const schema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(1, 'Password is required'),
});

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { signIn } = useAuth();
  const from = location.state?.from ?? '/';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ resolver: zodResolver(schema) });

  const mutation = useMutation({
    mutationFn: (values) => login(values.email, values.password),
    onSuccess: (data) => {
      signIn(
        { _id: data._id, name: data.name, email: data.email },
        data.token,
      );
      toast.success('Signed in');
      navigate(from, { replace: true });
    },
    onError: (err) => {
      toast.error(getApiErrorMessage(err, 'Sign in failed'));
    },
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">Account</p>
        <h1 className="page-head__title">Sign in</h1>
        <p className="page-head__lede">Use the email and password you registered with.</p>
      </header>

      <div className="form-panel">
        <form
          className="card"
          onSubmit={handleSubmit((v) => mutation.mutate(v))}
          noValidate
        >
          <div className="card__body">
            <div className="field">
              <label className="field__label" htmlFor="email">
                Email
              </label>
              <input id="email" className="input" type="email" autoComplete="email" {...register('email')} />
              {errors.email && <p className="field__error">{errors.email.message}</p>}
            </div>
            <div className="field">
              <label className="field__label" htmlFor="password">
                Password
              </label>
              <input
                id="password"
                className="input"
                type="password"
                autoComplete="current-password"
                {...register('password')}
              />
              {errors.password && <p className="field__error">{errors.password.message}</p>}
            </div>
            <div className="form-actions">
              <button className="btn btn--primary" type="submit" disabled={mutation.isPending}>
                {mutation.isPending ? <span className="spinner" /> : 'Sign in'}
              </button>
            </div>
            <p className="field__hint" style={{ marginTop: '1rem' }}>
              <Link to="/reset-password">Forgot password?</Link>
            </p>
            <p className="field__hint" style={{ marginTop: '0.5rem' }}>
              No account? <Link to="/register">Register</Link>
            </p>
          </div>
        </form>
      </div>
    </>
  );
}
