import React, { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ReportForm from '../components/ReportForm';
import Navbar from '../components/Navbar';

const ReportFormPage = () => {
  const { appointmentId } = useParams();
  const doctorId = localStorage.getItem('doctorId');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      navigate("/")
    } else if (!doctorId) {
      navigate("/patient/dashboard");
    }
  }, [token, doctorId, navigate])

  return (
    <>
      <Navbar />
      <div className="p-6 complete-padding">
        <h2 className="text-2xl font-bold mb-4">Create Report</h2>
        <ReportForm doctorId={doctorId} appointmentId={appointmentId} />
      </div>
    </>
  );
};

export default ReportFormPage;
