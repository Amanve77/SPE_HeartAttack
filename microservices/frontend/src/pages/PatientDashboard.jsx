import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import Navbar from '../components/Navbar';

export default function PatientDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [selectedReport, setSelectedReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const patientId = localStorage.getItem("patientId");
  const doctorId = localStorage.getItem("doctorId");
  const token = localStorage.getItem("token");

  // Add role-based redirection
  useEffect(() => {
    if (!token) {
      navigate('/');
      return;
    }

    // If user is a doctor, redirect to doctor dashboard
    if (doctorId) {
      navigate('/doctor/dashboard');
      return;
    }

    // If user is not a patient, redirect to home
    if (!patientId) {
      navigate('/');
      return;
    }
  }, [token, patientId, doctorId, navigate]);

  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        console.log('Fetching appointments for patientId:', patientId);
        console.log('Current token:', localStorage.getItem('token'));
        const res = await API.get(`/appointments/patient/${patientId}`);
        console.log('Appointments response:', res.data);
        setAppointments(res.data);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching appointments:', err);
        console.error('Error details:', err.response?.data);
        setError('Failed to load appointments. Please try again later.');
        setLoading(false);
      }
    };

    // Only fetch if we have a patientId and not a doctorId
    if (patientId && !doctorId) {
      fetchAppointments();
    }
  }, [patientId, doctorId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl font-semibold">Loading your dashboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl font-semibold text-red-600">{error}</div>
      </div>
    );
  }

  return (
    <>
      <Navbar />
      <div className="max-w-7xl mx-auto py-8 complete-padding">
        <div className="flex flex-col sm:flex-row justify-between items-center mb-8 px-4 sm:px-0">
          <h1 className="text-3xl font-bold text-gray-800 mb-4 sm:mb-0">🩺 Patient Dashboard</h1>
          <button
            onClick={() => navigate('/book-appointment')}
            className="bg-blue-500 hover:bg-blue-600 text-black px-6 py-2 rounded-lg shadow transition-colors"
          >
            Book New Appointment
          </button>
        </div>

        {appointments.length === 0 ? (
          <div className="text-center text-gray-600 mt-10">
            <p className="text-lg">No appointments found.</p>
            <p className="mt-2">Click the button above to book your first appointment!</p>
          </div>
        ) : (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 px-4 sm:px-0">
            {appointments.map((appt) => (
              <div
                key={appt.id}
                className="bg-white border rounded-lg shadow-md hover:shadow-lg transition-all p-6"
              >
                <h2 className="text-xl font-semibold mb-2 text-blue-700">
                  Dr. {appt.doctorName || 'Unknown'}
                </h2>
                <p className="text-sm text-gray-600 mb-1">
                  <strong>Specialization:</strong> {appt.doctorSpecialization || 'N/A'}
                </p>
                <p className="text-sm text-gray-600 mb-1">
                  <strong>Date:</strong> {new Date(appt.date).toLocaleString()}
                </p>
                <p className="text-sm text-gray-600 mb-1">
                  <strong>Status:</strong> {appt.status}
                </p>
                <p className="text-sm text-gray-600 mb-3">
                  <strong>Reason:</strong> {appt.reason}
                </p>
              
                {appt.status === 'COMPLETED' ? (
                  <button
                    onClick={() => navigate(`/reports/${appt.id}`)}
                    className="mt-2 bg-black hover:bg-blue-700 text-white text-sm px-4 py-2 rounded"
                  >
                    📄 View Report
                  </button>
                ) : (
                  <p className="text-sm text-gray-400 mt-2 italic">No report available yet</p>
                )}
              </div>
            ))}
          </div>
        )}

        {selectedReport && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-xl relative">
              <button
                onClick={() => setSelectedReport(null)}
                className="absolute top-3 right-4 text-red-600 text-xl font-bold"
              >
                &times;
              </button>
              <h2 className="text-2xl font-bold mb-4 text-gray-800">🧾 Report Details</h2>
              <div className="space-y-2 text-gray-700">
                <p><strong>Diagnosis:</strong> {selectedReport.diagnosis}</p>
                <p><strong>Risk Level:</strong> {selectedReport.riskLevel}</p>
                <p><strong>Recommended Tests:</strong> {selectedReport.recommendedTests}</p>
                <p><strong>Medications:</strong> {selectedReport.medications}</p>
                <p><strong>Lifestyle Advice:</strong> {selectedReport.lifestyleAdvice}</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
