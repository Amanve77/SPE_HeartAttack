import React, { useEffect, useState } from 'react';
import API from '../services/api';

const CompletedAppointments = ({ doctorId }) => {
  const [completedData, setCompletedData] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (doctorId) {
      API.get(`appointments/doctor/${doctorId}?status=COMPLETED`)
        .then(res => {
          console.log('Completed appointments:', res.data);
          setCompletedData(res.data);
          setError(null);
        })
        .catch(err => {
          console.error('Error fetching completed appointments:', err);
          setError('Failed to load completed appointments');
          if (err.response?.status === 403) {
            console.error('Authorization error - doctorId:', doctorId);
          }
        });
    }
  }, [doctorId]);

  if (error) {
    return (
      <div className="mt-6">
        <h3 className="text-2xl font-bold text-gray-800 mb-6">✅ Completed Appointments</h3>
        <p className="text-red-500 text-center">{error}</p>
      </div>
    );
  }

  return (
    <div className="mt-6">
      <h3 className="text-2xl font-bold text-gray-800 mb-6">✅ Completed Appointments</h3>

      {completedData.length === 0 ? (
        <p className="text-gray-500 italic text-center">No completed appointments found.</p>
      ) : (
        <div className="grid gap-6 md:grid-cols-2">
          {completedData.map((appointment) => (
            <div
              key={appointment.id}
              className="bg-white border rounded-lg shadow-md hover:shadow-lg transition p-6 space-y-2"
            >
              <div className="flex justify-between items-start">
                <div>
                  <h4 className="text-lg font-semibold text-blue-700">
                    {appointment.patientName || 'Unknown Patient'}
                  </h4>
                  <p className="text-sm text-gray-600">
                    {new Date(appointment.date).toLocaleString()}
                  </p>
                </div>
                <span className="bg-green-100 text-green-800 text-xs font-medium px-3 py-1 rounded-full">
                  {appointment.status}
                </span>
              </div>
              <p className="text-gray-700"><strong>Reason:</strong> {appointment.reason}</p>
              {appointment.doctorSpecialization && (
                <p className="text-gray-600 text-sm">
                  <strong>Specialization:</strong> {appointment.doctorSpecialization}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CompletedAppointments;
