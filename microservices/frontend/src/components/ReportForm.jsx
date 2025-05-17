import React, { useState } from 'react';
import API from '../services/api';
import { useNavigate } from 'react-router-dom';
import '../CSS/ReportForm.css';

const ReportForm = ({ appointmentId }) => {
  const [diagnosis, setDiagnosis] = useState('');
  const [riskLevel, setRiskLevel] = useState('LOW');
  const [recommendedTests, setRecommendedTests] = useState('');
  const [medications, setMedications] = useState('');
  const [lifestyleAdvice, setLifestyleAdvice] = useState('');
  const [error, setError] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await API.put(`/appointments/${appointmentId}/status?status=COMPLETED`);
      await API.post('/reports', {
        appointmentId,
        diagnosis,
        riskLevel,
        recommendedTests,
        medications,
        lifestyleAdvice,
      });

      setSubmitted(true);
      setTimeout(() => navigate('/doctor/dashboard'), 2000);
    } catch (err) {
      console.error('Error submitting report:', err);
      setError(err.response?.data?.message || 'Error submitting report');
    }
  };

  if (submitted) {
    return (
      <div className="report-success">
        ✅ Report submitted successfully!
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="report-form">
      <h2 className="form-title">📝 Submit Patient Report</h2>

      {error && <div className="form-error">{error}</div>}

      <div className="form-group">
        <label>Final Diagnosis</label>
        <textarea
          value={diagnosis}
          onChange={(e) => setDiagnosis(e.target.value)}
          rows={3}
          required
        />
      </div>

      <div className="form-group">
        <label>Risk Level</label>
        <select value={riskLevel} onChange={(e) => setRiskLevel(e.target.value)}>
          <option value="LOW">Low</option>
          <option value="MODERATE">Moderate</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </div>

      <div className="form-group">
        <label>Recommended Tests</label>
        <textarea
          value={recommendedTests}
          onChange={(e) => setRecommendedTests(e.target.value)}
          rows={2}
        />
      </div>

      <div className="form-group">
        <label>Medications Prescribed</label>
        <textarea
          value={medications}
          onChange={(e) => setMedications(e.target.value)}
          rows={2}
        />
      </div>

      <div className="form-group">
        <label>Lifestyle Advice</label>
        <textarea
          value={lifestyleAdvice}
          onChange={(e) => setLifestyleAdvice(e.target.value)}
          rows={2}
        />
      </div>

      <div className="form-actions">
        <button type="submit">📤 Submit Report</button>
      </div>
    </form>
  );
};

export default ReportForm;
