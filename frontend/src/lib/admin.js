export function isAdminUser(user) {
  if (!user?.email) return false;
  const raw = import.meta.env.VITE_ADMIN_EMAILS ?? '';
  const list = raw
    .split(',')
    .map((s) => s.trim().toLowerCase())
    .filter(Boolean);
  return list.includes(user.email.toLowerCase());
}
