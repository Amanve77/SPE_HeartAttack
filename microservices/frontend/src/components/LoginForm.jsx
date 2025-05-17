import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import API from '../services/api';
import 'bootstrap/dist/css/bootstrap.min.css';

export default function LoginForm() {
  const { role } = useParams();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [err, setErr] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErr('');
    
    try {
      const res = await API.post('/auth/login', {
        email,
        password,
        role: role.toUpperCase(),
      });
      
      // Log the response for debugging
      console.log('Login response:', res.data);
      
      // Store the complete token with Bearer prefix
      const token = `${res.data.tokenType} ${res.data.token}`;
      console.log('Storing token:', token);
      localStorage.setItem('token', token);
      console.log(role);
      if (role.toUpperCase() === 'DOCTOR') {
        localStorage.setItem('doctorId', res.data.doctorId);
        navigate('/doctor/dashboard');
      } else {
        localStorage.setItem('patientId', res.data.patientId);
        navigate('/patient/dashboard');
      }
    } catch (error) {
      console.error('Login error:', error);
      setErr(error.response?.data?.message || 'Invalid Credentials or Role');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center vh-100">
      <div className="card p-4 shadow" style={{ minWidth: '350px' }}>
        <h3 className="text-center mb-3">Login as {role.charAt(0).toUpperCase() + role.slice(1)}</h3>
        {err && <div className="alert alert-danger">{err}</div>}
        
        {/* Test credentials hint */}
        <div className="alert alert-info mb-3">
          <small>
            Test Credentials:<br />
            Email: {role === 'doctor' ? 'doctor@test.com' : 'patient@test.com'}<br />
            Password: test123
          </small>
        </div>

        <form onSubmit={handleLogin}>
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
              placeholder="Password"
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
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        {/* 👇 Render Register button only for patient role */}
        {role === 'patient' && (
          <div className="text-center mt-3">
            <p className="mb-1">New user?</p>
            <button
              type="button"
              className="btn btn-outline-secondary w-100"
              onClick={() => navigate('/register/patient')}
              disabled={loading}
            >
              Register as Patient
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
