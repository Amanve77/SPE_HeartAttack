import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import API from '../services/api';
import Navbar from '../components/Navbar';
import '../CSS/HeartAttackAnalyzerPage.css';

const HeartAttackAnalyzerPage = () => {
  const { patientId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);
  const [formData, setFormData] = useState({
    age: '',
    sex: '1',
    cp: '0',
    trtbps: '',
    chol: '',
    fbs: '0',
    restecg: '0',
    thalachh: '',
    exng: '0',
    oldpeak: '',
    slp: '0',
    caa: '0',
    thall: '3'
  });

  const doctorId = localStorage.getItem('doctorId');
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      navigate("/")
    } else if (!doctorId) {
      navigate("/patient/dashboard");
    }
  }, [token, doctorId, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await API.post('/predict/heart-attack-risk', {
        ...formData,
        age: parseInt(formData.age),
        sex: parseInt(formData.sex),
        cp: parseInt(formData.cp),
        trtbps: parseInt(formData.trtbps),
        chol: parseInt(formData.chol),
        fbs: parseInt(formData.fbs),
        restecg: parseInt(formData.restecg),
        thalachh: parseInt(formData.thalachh),
        exng: parseInt(formData.exng),
        oldpeak: parseFloat(formData.oldpeak),
        slp: parseInt(formData.slp),
        caa: parseInt(formData.caa),
        thall: parseInt(formData.thall)
      });
      
      setResult(response.data);
    } catch (err) {
      console.error('Error predicting heart attack risk:', err);
      setError(err.response?.data?.message || 'Error analyzing heart attack risk');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateReport = async () => {
    if (result) {
      try {
        const response = await API.get(`/appointments/patient/${patientId}/next`);
        const nextAppointmentId = response.data.id;
        
        if (!nextAppointmentId) {
          setError('No upcoming appointments found for this patient. Please schedule an appointment first.');
          return;
        }

        navigate(`/doctor/appointment/${nextAppointmentId}/report`, {
          state: {
            diagnosis: result.diagnosis,
            riskLevel: result.riskLevel
          }
        });
      } catch (err) {
        console.error('Error fetching next appointment:', err);
        setError('Failed to create report. Please ensure the patient has an upcoming appointment.');
      }
    }
  };

  return (
    <>
      <Navbar />
      <div className="form-container">
        <div className="form-card">
          <h2 className="form-title">❤️ Heart Attack Risk Analyzer</h2>

          {error && (
            <div className="error-message">
              <p>{error}</p>
            </div>
          )}

          {result && (
            <div className="result-card">
              <h3>Analysis Results</h3>
              <p><strong>Risk Level:</strong> {result.riskLevel}</p>
              <p><strong>Risk Score:</strong> {(result.riskScore * 100).toFixed(2)}%</p>
              <p><strong>Diagnosis:</strong> {result.diagnosis}</p>
              <button onClick={handleCreateReport} className="create-report-btn">
                Create Report with These Results
              </button>
            </div>
          )}

          <form onSubmit={handleSubmit} className="analyzer-form">
            <div className="form-grid">
              <div className="form-field">
                <label>Age</label>
                <input
                  type="number"
                  name="age"
                  value={formData.age}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Sex</label>
                <select
                  name="sex"
                  value={formData.sex}
                  onChange={handleInputChange}
                >
                  <option value="1">Male</option>
                  <option value="0">Female</option>
                </select>
              </div>

              <div className="form-field">
                <label>Chest Pain Type</label>
                <select
                  name="cp"
                  value={formData.cp}
                  onChange={handleInputChange}
                >
                  <option value="0">Typical Angina</option>
                  <option value="1">Atypical Angina</option>
                  <option value="2">Non-anginal Pain</option>
                  <option value="3">Asymptomatic</option>
                </select>
              </div>

              <div className="form-field">
                <label>Resting Blood Pressure</label>
                <input
                  type="number"
                  name="trtbps"
                  value={formData.trtbps}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Serum Cholesterol</label>
                <input
                  type="number"
                  name="chol"
                  value={formData.chol}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Fasting Blood Sugar</label>
                <select
                  name="fbs"
                  value={formData.fbs}
                  onChange={handleInputChange}
                >
                  <option value="0">≤ 120 mg/dl</option>
                  <option value="1">&gt; 120 mg/dl</option>
                </select>
              </div>

              <div className="form-field">
                <label>Resting ECG Results</label>
                <select
                  name="restecg"
                  value={formData.restecg}
                  onChange={handleInputChange}
                >
                  <option value="0">Normal</option>
                  <option value="1">ST-T Wave Abnormality</option>
                  <option value="2">Left Ventricular Hypertrophy</option>
                </select>
              </div>

              <div className="form-field">
                <label>Maximum Heart Rate</label>
                <input
                  type="number"
                  name="thalachh"
                  value={formData.thalachh}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Exercise Induced Angina</label>
                <select
                  name="exng"
                  value={formData.exng}
                  onChange={handleInputChange}
                >
                  <option value="0">No</option>
                  <option value="1">Yes</option>
                </select>
              </div>

              <div className="form-field">
                <label>ST Depression</label>
                <input
                  type="number"
                  step="0.1"
                  name="oldpeak"
                  value={formData.oldpeak}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Slope of Peak Exercise ST Segment</label>
                <select
                  name="slp"
                  value={formData.slp}
                  onChange={handleInputChange}
                >
                  <option value="0">Upsloping</option>
                  <option value="1">Flat</option>
                  <option value="2">Downsloping</option>
                </select>
              </div>

              <div className="form-field">
                <label>Number of Major Vessels</label>
                <select
                  name="caa"
                  value={formData.caa}
                  onChange={handleInputChange}
                >
                  <option value="0">0</option>
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                </select>
              </div>

              <div className="form-field">
                <label>Thalassemia</label>
                <select
                  name="thall"
                  value={formData.thall}
                  onChange={handleInputChange}
                >
                  <option value="3">Normal</option>
                  <option value="6">Fixed Defect</option>
                  <option value="7">Reversible Defect</option>
                </select>
              </div>
            </div>

            <div className="submit-btn-container">
              <button
                type="submit"
                disabled={loading}
                className="submit-btn"
              >
                {loading ? 'Analyzing...' : 'Analyze Risk'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default HeartAttackAnalyzerPage;
