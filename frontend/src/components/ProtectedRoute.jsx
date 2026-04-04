import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { isAdminUser } from '../lib/admin.js';

export function ProtectedRoute({ children, admin }) {
  const { user, ready } = useAuth();
  const location = useLocation();

  if (!ready) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '3rem' }}>
        <span className="spinner" aria-label="Loading" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  if (admin && !isAdminUser(user)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
