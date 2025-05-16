// src/components/Navbar.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../images/logo1.png';

export default function Navbar({ showCompleted, toggleCompletedView }) {
  const navigate = useNavigate();

const doctorId = localStorage.getItem('doctorId');
const patientId = localStorage.getItem('patientId');

const isDoctor = !!doctorId;
const isPatient = !!patientId && !doctorId; // ✅ Only true if not a doctor
const isLoggedIn = isDoctor || isPatient;


  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };


  return (
    <div className="bg-white shadow-sm sticky-top py-2 d-flex align-items-center justify-content-between flex-wrap border-top border-bottom z-3 px-4">
      {/* Logo */}
      <a
  href={
    isDoctor
      ? '/doctor/dashboard'
      : isPatient
      ? '/patient/dashboard'
      : '/'
  }
  className="d-flex align-items-center text-decoration-none"
>
  


        <img src={logo} alt="Logo" height="40" className="me-3" />
        <span className="fw-bold text-primary">YourDost</span>
      </a>

      {/* Navigation Buttons */}
      <div className="d-flex gap-3 flex-wrap justify-content-center">
        {!isLoggedIn && (
          <>
            <a className="btn btn-light" href="#overview">Overview</a>
            <a className="btn btn-light" href="#Symptoms">Symptoms</a>
            <a className="btn btn-light" href="#Consulation">Consultation</a>
            <a className="btn btn-light" href="#Causes">Causes</a>
            <a className="btn btn-light" href="#Risk-factors">Risk Factors</a>
            <a className="btn btn-light" href="#Complications">Complications</a>
            <a className="btn btn-light" href="#Prevention">Prevention</a>
          </>
        )}

        {isDoctor && (
          <button
            className="btn btn-success"
            onClick={toggleCompletedView}
          >
            {showCompleted ? '⬅️ Back to Pending Appointments' : '✅ View Completed Appointments'}
          </button>
        )}

        {isPatient && (
          <>
            <button
              className="btn btn-primary"
              onClick={() => navigate('/book-appointment')}
            >
              📅 Book Appointment
            </button>
            <button
              className="btn btn-info"
              onClick={() => navigate('/my-appointments')}
            >
              📋 Show Booked Appointments
            </button>
          </>
        )}

        {isLoggedIn && (
          <button
            className="btn btn-danger"
            onClick={handleLogout}
          >
            🚪 Logout
          </button>
        )}
      </div>
    </div>
  );
}
