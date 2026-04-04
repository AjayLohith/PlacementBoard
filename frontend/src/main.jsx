import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'sonner';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import './styles/global.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60000,
      retry: 1,
    },
  },
});

const rootEl = document.getElementById('root');
createRoot(rootEl).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </AuthProvider>
      <Toaster
        position="top-right"
        closeButton
        toastOptions={{
          className: 'pp-toast',
          style: {
            fontFamily: 'Source Sans 3, system-ui, sans-serif',
            border: '1px solid var(--border-strong)',
          },
        }}
      />
    </QueryClientProvider>
  </StrictMode>,
);
