import React, { useState, useEffect } from 'react';
import API from '../services/api';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import '../CSS/BookAppointment.css'; // ✅ Import CSS here

const BookAppointment = () => {
  const [doctorId, setDoctorId] = useState('');
  const [date, setDate] = useState('');
  const [reason, setReason] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const patientId = localStorage.getItem("patientId");
  const token = localStorage.getItem('token'); 

  useEffect(() => {
    if (!token) {
      navigate('/');
    } else if (!patientId) {
      navigate('/doctor/dashboard');
    }
  }, [token, patientId, navigate]);

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        const res = await API.get('/doctors/all');
        setDoctors(res.data);
        setError('');
      } catch (err) {
        console.error('Error fetching doctors:', err);
        setError('Failed to load doctors. Please try again later.');
      }
    };
    fetchDoctors();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (reason.length < 10) {
      setError('Reason must be at least 10 characters long');
      return;
    }
    
    try {
      await API.post('/appointments', {
        patientId: parseInt(patientId),
        doctorId: parseInt(doctorId),
        date: new Date(date).toISOString(),
        reason,
      });
      navigate('/patient/dashboard');
    } catch (err) {
      console.error('Error booking appointment:', err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 400) {
        setError('Please check all fields are filled correctly. Reason must be at least 10 characters.');
      } else {
        setError('Error booking appointment. Please try again.');
      }
    }
  };

  return (
    <>
      <Navbar />
      <div className="appointment-container">
        <h2>Book an Appointment</h2>

        {error && <p className="error-message">{error}</p>}

        <form onSubmit={handleSubmit} className="appointment-form">
          <div>
            <label>Select Doctor</label>
            <select
              value={doctorId}
              onChange={(e) => setDoctorId(e.target.value)}
              required
            >
              <option value="">-- Choose a Doctor --</option>
              {doctors.map((doc) => (
                <option key={doc.id} value={doc.id}>
                  {doc.name} - {doc.specialization} ({doc.email})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label>Appointment Date & Time</label>
            <input
              type="datetime-local"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </div>

          <div>
            <label>Reason for Appointment (minimum 10 characters)</label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              rows={4}
              placeholder="Briefly describe your symptoms or reason for visit..."
              required
            />
            {reason.length < 10 && reason.length > 0 && (
              <p className="error-message">
                Please enter at least {10 - reason.length} more character{10 - reason.length === 1 ? '' : 's'}
              </p>
            )}
          </div>

          <button type="submit">Confirm Appointment</button>
        </form>
      </div>
            
    </>
  );
};

export default BookAppointment;
