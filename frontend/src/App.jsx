import { Route, Routes } from 'react-router-dom';
import { SessionSync } from './components/SessionSync.jsx';
import { Shell } from './components/layout/Shell.jsx';
import { ProtectedRoute } from './components/ProtectedRoute.jsx';
import { AdminPage } from './pages/AdminPage.jsx';
import { ArticleDetailPage } from './pages/ArticleDetailPage.jsx';
import { ArticlesPage } from './pages/ArticlesPage.jsx';
import { CompaniesPage } from './pages/CompaniesPage.jsx';
import { CompanyDetailPage } from './pages/CompanyDetailPage.jsx';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage.jsx';
import { HomePage } from './pages/HomePage.jsx';
import { JobsPage } from './pages/JobsPage.jsx';
import { LoginPage } from './pages/LoginPage.jsx';
import { NotFoundPage } from './pages/NotFoundPage.jsx';
import { RegisterPage } from './pages/RegisterPage.jsx';
import { SubmitExperiencePage } from './pages/SubmitExperiencePage.jsx';

export default function App() {
  return (
    <>
      <SessionSync />
      <Routes>
      <Route element={<Shell />}>
        <Route index element={<HomePage />} />
        <Route path="companies" element={<CompaniesPage />} />
        <Route path="jobs" element={<JobsPage />} />
        <Route path="articles" element={<ArticlesPage />} />
        <Route path="articles/:slug" element={<ArticleDetailPage />} />
        <Route path="c/:slug" element={<CompanyDetailPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="reset-password" element={<ForgotPasswordPage />} />
        <Route
          path="share"
          element={
            <ProtectedRoute>
              <SubmitExperiencePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin"
          element={
            <ProtectedRoute admin>
              <AdminPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
    </>
  );
}
