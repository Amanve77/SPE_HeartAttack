// src/pages/BookedAppointments.js
import React, { useEffect, useState } from 'react';
import API from '../services/api';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';

const BookedAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const patientId = localStorage.getItem('patientId');
  const doctorId = localStorage.getItem('doctorId');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    // Redirect logic
    if (!token) {
      navigate("/");
      return;
    }
    
    // If user is a doctor, redirect to doctor dashboard
    if (doctorId) {
      navigate("/doctor/dashboard");
      return;
    }
    
    // If user is neither a patient nor a doctor, redirect to home
    if (!patientId) {
      navigate("/");
      return;
    }
  }, [token, patientId, doctorId, navigate]);

  useEffect(() => {
    // Only fetch appointments if we have a patientId
    if (patientId && !doctorId) {
      setLoading(true);
      API.get(`/appointments/patient/${patientId}`)
        .then(res => {
          const pendingAppointments = res.data.filter(app => 
            app.status === 'SCHEDULED' || app.status === 'CONFIRMED'
          );
          setAppointments(pendingAppointments);
          setLoading(false);
        })
        .catch(err => {
          console.error('Error fetching appointments:', err);
          setError('Failed to load appointments. Please try again later.');
          setLoading(false);
        });
    }
  }, [patientId, doctorId]);

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="max-w-4xl mx-auto p-6 complete-padding">
          <p className="text-center">Loading appointments...</p>
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Navbar />
        <div className="max-w-4xl mx-auto p-6 complete-padding">
          <p className="text-center text-red-600">{error}</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="max-w-4xl mx-auto p-6 complete-padding">
        <h2 className="text-2xl font-bold mb-6 text-center">📋 Your Pending Appointments</h2>
        {appointments.length === 0 ? (
          <p className="text-center text-gray-600">No pending appointments found.</p>
        ) : (
          <ul className="space-y-4">
            {appointments.map(app => (
              <li key={app.id} className="border rounded p-4 shadow-sm">
                <p><strong>Doctor:</strong> {app.doctorName || 'N/A'}</p>
                <p><strong>Specialization:</strong> {app.doctorSpecialization || 'N/A'}</p>
                <p><strong>Date:</strong> {new Date(app.date).toLocaleString()}</p>
                <p><strong>Status:</strong> {app.status}</p>
                <p><strong>Reason:</strong> {app.reason}</p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  );
};

export default BookedAppointments;
