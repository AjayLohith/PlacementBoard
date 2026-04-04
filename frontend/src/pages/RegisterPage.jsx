import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { z } from 'zod';
import { register as registerUser } from '../api/auth.js';
import { getApiErrorMessage } from '../api/errors.js';
import { useAuth } from '../context/AuthContext.jsx';

const schema = z.object({
  name: z.string().min(2, 'Name required'),
  email: z.string().min(1, 'Email required').email('Enter a valid email'),
  password: z.string().min(8, 'At least 8 characters'),
});

export function RegisterPage() {
  const navigate = useNavigate();
  const { signIn } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
  });

  const mutation = useMutation({
    mutationFn: (values) =>
      registerUser({
        name: values.name.trim(),
        email: values.email.trim().toLowerCase(),
        password: values.password,
      }),
    onSuccess: (data) => {
      signIn(
        { _id: data._id, name: data.name, email: data.email },
        data.token,
      );
      toast.success('Account created');
      navigate('/', { replace: true });
    },
    onError: (err) => {
      toast.error(getApiErrorMessage(err, 'Registration failed'));
    },
  });

  return (
    <>
      <header className="page-head">
        <p className="page-head__eyebrow">New account</p>
        <h1 className="page-head__title">Register</h1>
        <p className="page-head__lede">
          Password resets use a code sent to your email, so keep access to your inbox.
        </p>
      </header>

      <div className="form-panel form-panel--wide">
        <form className="card" onSubmit={handleSubmit((v) => mutation.mutate(v))} noValidate>
          <div className="card__body">
            <div className="inline-fields inline-fields--2">
              <div className="field">
                <label className="field__label" htmlFor="name">
                  Full name
                </label>
                <input id="name" className="input" {...register('name')} autoComplete="name" />
                {errors.name && <p className="field__error">{errors.name.message}</p>}
              </div>
              <div className="field">
                <label className="field__label" htmlFor="email">
                  Email
                </label>
                <input id="email" className="input" type="email" {...register('email')} />
                {errors.email && <p className="field__error">{errors.email.message}</p>}
              </div>
            </div>
            <div className="field">
              <label className="field__label" htmlFor="password">
                Password
              </label>
              <input id="password" className="input" type="password" {...register('password')} />
              {errors.password && <p className="field__error">{errors.password.message}</p>}
            </div>

            <div className="form-actions">
              <button className="btn btn--primary" type="submit" disabled={mutation.isPending}>
                {mutation.isPending ? <span className="spinner" /> : 'Create account'}
              </button>
            </div>
            <p className="field__hint" style={{ marginTop: '1rem' }}>
              Already registered? <Link to="/login">Sign in</Link>
            </p>
          </div>
        </form>
      </div>
    </>
  );
}
