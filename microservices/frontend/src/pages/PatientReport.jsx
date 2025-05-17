import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';
import Navbar from '../components/Navbar';

export default function PatientReport() {
  const { reportId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) {
      navigate('/');
    }
  }, [token, navigate]);

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const res = await API.get(`/patient/report/${reportId}`);
        setReport(res.data);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching report:', err);
        setLoading(false);
      }
    };

    fetchReport();
  }, [reportId]);

  if (loading) return <div className="p-6 text-center">Loading report...</div>;
  if (!report) return <div className="p-6 text-center text-red-500">Report not found.</div>;

  return (
    <>
      <Navbar />
      <div className="max-w-3xl mx-auto p-6 complete-padding">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">🧾 Report Details</h1>
        <div className="space-y-4 text-gray-700 bg-white p-6 rounded-lg shadow-md">
          <p><strong>Diagnosis:</strong> {report.diagnosis}</p>
          <p><strong>Risk Level:</strong> {report.riskLevel}</p>
          <p><strong>Recommended Tests:</strong> {report.recommendedTests}</p>
          <p><strong>Medications:</strong> {report.medications}</p>
          <p><strong>Lifestyle Advice:</strong> {report.lifestyleAdvice}</p>
        </div>
        <button
          className="mt-6 bg-blue-600 hover:bg-blue-700 text-black px-4 py-2 rounded"
          onClick={() => navigate('/patient/dashboard')}
        >
          Back to Dashboard
        </button>
      </div>
    </>
  );
}
