import React, { useEffect, useState } from 'react';
import API from '../services/api';
import AppointmentList from '../components/AppointmentList';
import PatientDetails from '../components/PatientDetails';
import CompletedAppointments from '../components/CompletedAppointments';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';

const DoctorDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [showCompleted, setShowCompleted] = useState(false);
  const doctorId = localStorage.getItem('doctorId');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    if(!token) {
      navigate("/");
    } else if(!doctorId) {
      navigate("/patient/dashboard");
    }
  }, [token, doctorId, navigate]);

  useEffect(() => {
    if (!showCompleted && doctorId) {
      API.get(`/appointments/doctor/${doctorId}`)
        .then(res => {
          console.log('Appointments response:', res.data);
          setAppointments(res.data);
        })
        .catch(err => {
          console.error('Error fetching appointments:', err);
          if (err.response?.status === 403) {
            console.error('Authorization error - doctorId:', doctorId);
          }
        });
    }
  }, [showCompleted, doctorId]);

  return (
    <div style={styles.container}>
      <Navbar
        showCompleted={showCompleted}
        toggleCompletedView={() => {
          setShowCompleted(!showCompleted);
          setSelectedPatient(null);
        }}
      />

      <div style={styles.content}>
        {!showCompleted ? (
          <>
            <AppointmentList appointments={appointments} onSelectPatient={setSelectedPatient} />
            {selectedPatient && (
              <div style={styles.patientDetails}>
                <PatientDetails patientId={selectedPatient} doctorId={doctorId} />
              </div>
            )}
          </>
        ) : (
          <CompletedAppointments doctorId={doctorId} />
        )}
      </div>
    </div>
  );
};

const styles = {
  container: {
    fontFamily: '"Helvetica Neue", Helvetica, Arial, sans-serif',
    backgroundColor: '#f4f7fc',
    minHeight: '100vh',
    paddingBottom: '20px',
  },
  content: {
    maxWidth: '100%',
   
   
    padding: '0 20px',
  },
  patientDetails: {
    marginTop: '30px',
    padding: '20px',
    backgroundColor: '#fff',
    borderRadius: '8px',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
  },
};

export default DoctorDashboard;
