import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import API from '../services/api';
import '../CSS/ReportDetails.css'; // Make sure this path matches your file structure
import Navbar from '../components/Navbar';

export default function ReportDetails() {
  const { id } = useParams();
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const res = await API.get(`/reports/${id}`);
        setReport(res.data);
      } catch (err) {
        console.error('Error fetching report:', err);
        setError('Could not fetch report');
      } finally {
        setLoading(false);
      }
    };

    fetchReport();
  }, [id]);

  if (loading) return <div className="report-loading">Loading report...</div>;
  if (error) return <div className="report-error">{error}</div>;
  if (!report) return <div className="report-empty">No report found.</div>;

  return (
    <>
    <Navbar/>
    <div className="report-container">
      <h1 className="report-title">📋 Report Details</h1>
      <div className="report-card">
        <p><strong>🩺 Diagnosis:</strong> {report.diagnosis}</p>
        <p><strong>⚠️ Risk Level:</strong> {report.riskLevel}</p>
        <p><strong>🧪 Recommended Tests:</strong> {report.recommendedTests}</p>
        <p><strong>💊 Medications:</strong> {report.medications}</p>
        <p><strong>🏃 Lifestyle Advice:</strong> {report.lifestyleAdvice}</p>
      </div>
    </div>
    </>
  );
}
