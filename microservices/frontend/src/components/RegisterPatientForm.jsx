import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import API from '../services/api';

export default function RegisterPatientForm() {
  const [username, setUsername] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [err, setErr] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Password validation
  const validatePassword = (password) => {
    const regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/;
    return regex.test(password);
  };

  // Email validation
  const validateEmail = (email) => {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setErr('');
    setSuccess('');
    setLoading(true);

    // Validation
    if (!validateEmail(email)) {
      setErr('Please enter a valid email address');
      setLoading(false);
      return;
    }

    if (!validatePassword(password)) {
      setErr('Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character');
      setLoading(false);
      return;
    }

    if (username.length < 3) {
      setErr('Username must be at least 3 characters long');
      setLoading(false);
      return;
    }

    if (firstName.length < 2 || lastName.length < 2) {
      setErr('First name and last name must be at least 2 characters long');
      setLoading(false);
      return;
    }

    try {
      const res = await API.post('/auth/register', {
        username,
        firstName,
        lastName,
        email,
        password,
        role: 'PATIENT',
      });

      console.log('Registration response:', res.data);
      setSuccess('Registration successful! Redirecting to login...');
      
      // Wait for 2 seconds before redirecting
      await new Promise(resolve => setTimeout(resolve, 2000));
      navigate('/login/patient');
    } catch (error) {
      console.error("Registration error:", error.response?.data);
      let errorMsg;
      
      if (error.response?.status === 401) {
        errorMsg = 'Invalid credentials. Please check your information.';
      } else if (error.response?.status === 409) {
        errorMsg = 'Email or username already exists. Please try another.';
      } else if (error.response?.data?.message) {
        errorMsg = error.response?.data?.message;
      } else if (error.response?.data?.error) {
        errorMsg = error.response?.data?.error;
      } else {
        errorMsg = 'Registration failed. Please try again.';
      }
      
      setErr(errorMsg);
      // Ensure the error message stays visible
      window.scrollTo(0, 0);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center vh-100">
      <div className="card p-4 shadow" style={{ minWidth: '350px' }}>
        <h3 className="text-center mb-3">Register as Patient</h3>
        {err && <div className="alert alert-danger">{err}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        <form onSubmit={handleRegister}>
          <div className="form-group mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="Username (min. 3 characters)"
              value={username}
              required
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
            />
          </div>
          <div className="form-group mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="First Name (min. 2 characters)"
              value={firstName}
              required
              onChange={(e) => setFirstName(e.target.value)}
              disabled={loading}
            />
          </div>
          <div className="form-group mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="Last Name (min. 2 characters)"
              value={lastName}
              required
              onChange={(e) => setLastName(e.target.value)}
              disabled={loading}
            />
          </div>
          <div className="form-group mb-3">
            <input
              type="email"
              className="form-control"
              placeholder="Email"
              value={email}
              required
              onChange={(e) => setEmail(e.target.value)}
              disabled={loading}
            />
          </div>
          <div className="form-group mb-4">
            <input
              type="password"
              className="form-control"
              placeholder="Password (min. 8 chars, 1 uppercase, 1 number, 1 special)"
              value={password}
              required
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
            />
          </div>
          <button 
            type="submit" 
            className="btn btn-primary w-100"
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register'}
          </button>
          <div className="text-center mt-3">
            <p>Already have an account? <a href="/login/patient">Login here</a></p>
          </div>
        </form>
      </div>
    </div>
  );
}
