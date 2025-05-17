// src/App.jsx
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import LoginForm from './components/LoginForm';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import PatientDetailsPage from './pages/PatientDetailsPage';
import ReportFormPage from './pages/ReportFormPage';
import HeartAttackAnalyzerPage from './pages/HeartAttackAnalyzerPage';
import BookAppointment from './pages/BookAppointment';
import RegisterPatientForm from './components/RegisterPatientForm';
import BookedAppointments from './pages/BookedAppointments';
import PatientReport from './pages/PatientReport';
import ProtectedRoute from './components/ProtectedRoute';
import ReportDetails from './pages/ReportDetails';

function App() {
  return (
    <Router>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login/:role" element={<LoginForm />} />
        <Route path="/register/patient" element={<RegisterPatientForm />} />
        <Route path="/reports/:id" element={<ReportDetails />} />
        {/* Protected Patient routes */}
        <Route
          path="/patient/dashboard"
          element={
            <ProtectedRoute requiredRole="PATIENT">
              <PatientDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/book-appointment"
          element={
            <ProtectedRoute requiredRole="PATIENT">
              <BookAppointment />
            </ProtectedRoute>
          }
        />
        <Route
          path="/my-appointments"
          element={
            <ProtectedRoute requiredRole="PATIENT">
              <BookedAppointments />
            </ProtectedRoute>
          }
        />
        <Route
          path="/patient/report/:reportId"
          element={
            <ProtectedRoute requiredRole="PATIENT">
              <PatientReport />
            </ProtectedRoute>
          }
        />

        {/* Protected Doctor routes */}
        <Route
          path="/doctor/dashboard"
          element={
            <ProtectedRoute requiredRole="DOCTOR">
              <DoctorDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctor/patient/:patientId"
          element={
            <ProtectedRoute requiredRole="DOCTOR">
              <PatientDetailsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctor/patient/:patientId/analyze"
          element={
            <ProtectedRoute requiredRole="DOCTOR">
              <HeartAttackAnalyzerPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctor/appointment/:appointmentId/report"
          element={
            <ProtectedRoute requiredRole="DOCTOR">
              <ReportFormPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
