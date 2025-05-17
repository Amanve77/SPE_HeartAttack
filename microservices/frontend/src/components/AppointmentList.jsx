import { useNavigate } from 'react-router-dom';
import '../CSS/AppointmentList.css'; // Import the CSS

const AppointmentList = ({ appointments }) => {
  const navigate = useNavigate();

  const pendingAppointments = appointments.filter(appt => appt.status === 'SCHEDULED');

  return (
    <div className="appointment-list-container">
      <h3 className="appointment-title">🕒 Pending Appointments</h3>

      {pendingAppointments.length === 0 ? (
        <p className="no-appointments">No pending appointments at the moment.</p>
      ) : (
        <ul className="appointment-list">
          {pendingAppointments.map((appt) => (
            <li key={appt.id} className="appointment-item">
              <div
                className="appointment-card"
                onClick={() => navigate(`/doctor/patient/${appt.patientId}`)}
              >
                <div className="appointment-header">
                  <div>
                    <h4 className="patient-name">{appt.patientName || 'Unknown Patient'}</h4>
                    <p className="appointment-date">
                      {new Date(appt.date).toLocaleString()}
                    </p>
                  </div>
                  <span className="appointment-status">
                    Status: {appt.status}
                  </span>
                </div>
                <div className="appointment-reason">
                  <strong>Reason:</strong> {appt.reason}
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default AppointmentList;
