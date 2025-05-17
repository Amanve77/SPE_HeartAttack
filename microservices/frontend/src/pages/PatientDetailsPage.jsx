import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';
import Navbar from '../components/Navbar';
import '../CSS/PatientDetailPage.css'
const PatientDetailsPage = () => {
  const { patientId } = useParams();
  const navigate = useNavigate();
  const [patient, setPatient] = useState(null);
  const [reports, setReports] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const doctorId = localStorage.getItem('doctorId');
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      navigate("/");
    } else if (!doctorId) {
      navigate("/patient/dashboard");
    }
  }, [token, doctorId, navigate]);

  useEffect(() => {
    if (patientId && doctorId) {
      setLoading(true);
      setError(null);

      API.get(`/appointments/doctor/${doctorId}`)
        .then(response => {
          const patientAppointments = response.data.filter(
            appt => appt.patientId.toString() === patientId.toString()
          );

          if (patientAppointments.length > 0) {
            const latestAppointment = patientAppointments[0];
            setPatient({
              id: patientId,
              name: latestAppointment.patientName,
              email: latestAppointment.patientUsername,
            });
            setAppointments(patientAppointments);

            const appointmentIds = patientAppointments.map(appt => appt.id);
            return Promise.all(
              appointmentIds.map(appointmentId =>
                API.get(`/reports/appointment/${appointmentId}`).catch(err => {
                  if (err.response?.status === 404) {
                    return { data: null };
                  }
                  throw err;
                })
              )
            );
          } else {
            throw new Error('No appointments found for this patient');
          }
        })
        .then(reportsResponses => {
          const validReports = reportsResponses
            .filter(response => response.data)
            .map(response => response.data);
          setReports(validReports);
          setLoading(false);
        })
        .catch(err => {
          console.error('Error loading patient details:', err);
          setError(err.response?.data?.message || err.message || 'Error loading patient details');
          setLoading(false);
        });
    }
  }, [patientId, doctorId]);

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="loading-container">
          <h3>Loading patient details...</h3>
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Navbar />
        <div className="error-container">
          <h3>Error</h3>
          <p>{error}</p>
          <button className="back-button" onClick={() => navigate('/doctor/dashboard')}>Back to Dashboard</button>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="patient-details-container">
        <h2 className="page-title">Patient Details</h2>
        <button className="back-button" onClick={() => navigate('/doctor/dashboard')}>Back to Dashboard</button>

        {patient ? (
          <div className="patient-info">
            <p><strong>Name:</strong> {patient.name}</p>
            <p><strong>Email:</strong> {patient.email}</p>

            <h3 className="section-title">Appointments History</h3>
            {appointments.map(appointment => (
              <div key={appointment.id} className="appointment-card2">
                <p><strong>Date:</strong> {new Date(appointment.date).toLocaleString()}</p>
                <p><strong>Status:</strong> {appointment.status}</p>
                <p><strong>Reason:</strong> {appointment.reason}</p>
                {appointment.status === 'SCHEDULED' && (
                  <button className="report-button" onClick={() => navigate(`/doctor/appointment/${appointment.id}/report`)}>
                    Create Report
                  </button>
                )}
              </div>
            ))}

            <h3 className="section-title">Medical Reports</h3>
            {reports.length > 0 ? (
              reports.map(report => (
                <div key={report.id} className="report-card2">
                  <p><strong>Date:</strong> {new Date(report.createdAt).toLocaleString()}</p>
                  <p><strong>Diagnosis:</strong> {report.diagnosis}</p>
                  <p><strong>Risk Level:</strong> {report.riskLevel}</p>
                  {report.medications && <p><strong>Medications:</strong> {report.medications}</p>}
                  {report.recommendedTests && <p><strong>Recommended Tests:</strong> {report.recommendedTests}</p>}
                  {report.lifestyleAdvice && <p><strong>Lifestyle Advice:</strong> {report.lifestyleAdvice}</p>}
                </div>
              ))
            ) : (
              <p>No medical reports available.</p>
            )}

            <button className="analyze-button" onClick={() => navigate(`/doctor/patient/${patientId}/analyze`)}>
              Run Heart Attack Analyzer
            </button>
          </div>
        ) : (
          <p>No patient data available.</p>
        )}
      </div>
    </>
  );
};

export default PatientDetailsPage;
