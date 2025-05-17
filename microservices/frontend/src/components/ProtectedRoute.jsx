import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const patientId = localStorage.getItem('patientId');
  const doctorId = localStorage.getItem('doctorId');

  useEffect(() => {
    if (!token) {
      navigate('/');
      return;
    }

    // Check if the user has the correct role based on stored IDs
    if (requiredRole === 'PATIENT' && !patientId) {
      navigate('/');
      return;
    }

    if (requiredRole === 'DOCTOR' && !doctorId) {
      navigate('/');
      return;
    }
  }, [token, patientId, doctorId, navigate, requiredRole]);

  // If no token, don't render the protected content
  if (!token) {
    return null;
  }

  // If role-specific check fails, don't render the protected content
  if (requiredRole === 'PATIENT' && !patientId) {
    return null;
  }

  if (requiredRole === 'DOCTOR' && !doctorId) {
    return null;
  }

  return children;
};

export default ProtectedRoute; 