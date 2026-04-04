import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { useAuth } from '../context/AuthContext.jsx';

/**
 * On API 401 (expired/invalid JWT), client dispatches pp:session-expired — sign out and go to login.
 */
export function SessionSync() {
  const navigate = useNavigate();
  const { signOut } = useAuth();

  useEffect(() => {
    const onExpired = () => {
      signOut();
      toast.info('Your session ended. Please sign in again.');
      if (window.location.pathname !== '/login') {
        const returnTo = `${window.location.pathname}${window.location.search}`;
        navigate('/login', { replace: true, state: { from: returnTo } });
      }
    };
    window.addEventListener('pp:session-expired', onExpired);
    return () => window.removeEventListener('pp:session-expired', onExpired);
  }, [navigate, signOut]);

  return null;
}
