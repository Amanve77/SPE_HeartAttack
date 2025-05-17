// src/components/Navbar.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../images/logo1.png';
import '../CSS/Navbar.css'; // Importing the CSS

export default function Navbar({ showCompleted, toggleCompletedView }) {
  const navigate = useNavigate();

  const doctorId = localStorage.getItem('doctorId');
  const patientId = localStorage.getItem('patientId');

  const isDoctor = !!doctorId;
  const isPatient = !!patientId && !doctorId;
  const isLoggedIn = isDoctor || isPatient;

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  return (
    <div className="navbar">
      {/* Logo */}
      <a
        href={
          isDoctor
            ? '/doctor/dashboard'
            : isPatient
            ? '/patient/dashboard'
            : '/'
        }
        className="logo"
      >
        <img src={logo} alt="Logo" />
        <span>YourDost</span>
      </a>

      {/* Navigation Buttons */}
      <div className="nav-buttons">
        {!isLoggedIn && (
          <>
            <a href="#overview">Overview</a>
            <a href="#Symptoms">Symptoms</a>
            <a href="#Consultation">Consultation</a>
            <a href="#Causes">Causes</a>
            <a href="#Risk-factors">Risk Factors</a>
            <a href="#Complications">Complications</a>
            <a href="#Prevention">Prevention</a>
          </>
        )}

        {isDoctor && (
          <button className="btn-success" onClick={toggleCompletedView}>
            {showCompleted
              ? 'Back to Pending Appointments'
              : 'View Completed Appointments'}
          </button>
        )}

        {isPatient && (
          <>
            <button className="btn-primary" onClick={() => navigate('/book-appointment')}>
              Book Appointment
            </button>
            <button className="btn-info" onClick={() => navigate('/my-appointments')}>
              Show Booked Appointments
            </button>
          </>
        )}

        {isLoggedIn && (
          <button className="btn-danger" onClick={handleLogout}>
            Logout
          </button>
        )}
      </div>
    </div>
  );
}