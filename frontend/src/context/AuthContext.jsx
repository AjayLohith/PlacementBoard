import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { getStoredUser, setStoredUser } from '../api/auth.js';
import { setStoredToken } from '../api/client.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    setUser(getStoredUser());
    setReady(true);
  }, []);

  const signIn = useCallback((u, token) => {
    setStoredToken(token);
    setStoredUser(u);
    setUser(u);
  }, []);

  const signOut = useCallback(() => {
    setStoredToken(null);
    setStoredUser(null);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      ready,
      signIn,
      signOut,
    }),
    [user, ready, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}
