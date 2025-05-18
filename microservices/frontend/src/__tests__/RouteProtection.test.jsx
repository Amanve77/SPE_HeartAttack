import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from '../components/ProtectedRoute';
import '@testing-library/jest-dom';

// Mock the useNavigate hook
const mockedUsedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUsedNavigate,
}));

describe('Route Protection Tests', () => {
  beforeEach(() => {
    localStorage.clear();
    mockedUsedNavigate.mockClear();
  });

  const protectedRoutes = [
    { path: '/patient/dashboard', role: 'PATIENT' },
    { path: '/doctor/dashboard', role: 'DOCTOR' },
    { path: '/book-appointment', role: 'PATIENT' },
    { path: '/my-appointments', role: 'PATIENT' },
    { path: '/doctor/patient/123', role: 'DOCTOR' },
    { path: '/doctor/patient/123/analyze', role: 'DOCTOR' }
  ];

  protectedRoutes.forEach(route => {
    test(`redirects to home when accessing ${route.path} without authentication`, () => {
      render(
        <MemoryRouter initialEntries={[route.path]}>
          <Routes>
            <Route
              path={route.path}
              element={
                <ProtectedRoute requiredRole={route.role}>
                  <div>Protected Content</div>
                </ProtectedRoute>
              }
            />
          </Routes>
        </MemoryRouter>
      );

      // Check that user is redirected (navigate('/') is called)
      expect(mockedUsedNavigate).toHaveBeenCalledWith('/');

      // Ensure protected content is not rendered
      expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    });
  });
});