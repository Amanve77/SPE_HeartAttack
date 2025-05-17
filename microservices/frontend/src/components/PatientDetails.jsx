import React, { useEffect, useState } from 'react';
import API from '../services/api';
import ReportForm from './ReportForm';

const PatientDetails = ({ patientId, doctorId }) => {
  const [patientInfo, setPatientInfo] = useState(null);
  const [reports, setReports] = useState([]);
  const [analysis, setAnalysis] = useState(null);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    API.get(`/doctor/patient/${patientId}/history`)
      .then((res) => {
        setPatientInfo(res.data.patient);
        setReports(res.data.reports);
      })
      .catch((err) => console.error('Error loading history:', err));
  }, [patientId]);
  console.log("Patient:: ", patientInfo)
  const handleRunAnalyzer = () => {
    API.post(`/doctor/analyze/${patientId}`)
      .then((res) => {
        setAnalysis(res.data);
        setShowForm(true);
      })
      .catch((err) => console.error('Error running analyzer:', err));
  };

  return (
    <div className="mt-6 p-4 border rounded shadow">
      <h3 className="text-lg font-semibold mb-2">Patient Details</h3>
      {patientInfo ? (
        <>
          <p><strong>Name:</strong> {patientInfo.name}</p>
          <p><strong>Email:</strong> {patientInfo.email}</p>

          <h4 className="mt-4 font-semibold">Medical Reports:</h4>
          {reports.length > 0 ? (
            <ul className="list-disc pl-6">
              {reports.map((report) => (
                <li key={report._id}>
                  <strong>Diagnosis:</strong> {report.diagnosis} | <strong>Risk:</strong> {report.riskLevel}
                </li>
              ))}
            </ul>
          ) : (
            <p>No reports yet.</p>
          )}

          <button
            onClick={handleRunAnalyzer}
            className="mt-4 bg-green-600 text-white px-4 py-2 rounded"
          >
            Run Heart Attack Analyzer
          </button>

          {analysis && showForm && (
            <>
              <p className="mt-4 text-blue-600 font-medium">
                Prediction: {analysis.diagnosis} | Risk Level: {analysis.riskLevel}
              </p>
              <ReportForm
                patientId={patientId}
                doctorId={doctorId}
                prefill={analysis}
              />
            </>
          )}
        </>
      ) : (
        <p>Loading patient info...</p>
      )}
    </div>
  );
};

export default PatientDetails;
