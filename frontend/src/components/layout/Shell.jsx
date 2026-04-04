import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { isAdminUser } from '../../lib/admin.js';

export function Shell() {
  const { user, signOut } = useAuth();
  const admin = isAdminUser(user);

  return (
    <div className="shell">
      <header className="header">
        <div className="header__inner">
          <Link to="/" className="header__brand">
            <span className="header__title">PlacementBoard</span>
          </Link>
          <nav className="header__nav" aria-label="Main">
            <Link className="header__link" to="/companies">
              Companies
            </Link>
            <Link className="header__link" to="/jobs">
              Jobs
            </Link>
            <Link className="header__link" to="/articles">
              Articles
            </Link>
            {user ? (
              <>
                <Link className="header__link" to="/share">
                  Share experience
                </Link>
                {admin ? (
                  <Link className="header__link" to="/admin">
                    Admin
                  </Link>
                ) : null}
                <span className="header__user" title={user.email}>
                  {user.name}
                </span>
                <button type="button" className="btn btn--ghost btn--sm" onClick={() => signOut()}>
                  Sign out
                </button>
              </>
            ) : (
              <>
                <Link className="header__link" to="/login">
                  Sign in
                </Link>
                <Link className="btn btn--primary btn--sm" to="/register">
                  Register
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="shell__main">
        <Outlet />
      </main>
      <footer className="footer">
        Interview notes from peers. Sign in to share experiences; admins publish jobs and articles.
      </footer>
    </div>
  );
}
