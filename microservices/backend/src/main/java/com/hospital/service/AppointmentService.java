package com.hospital.service;

import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentResponse;
import com.hospital.model.Appointment;
import com.hospital.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse updateAppointmentStatus(Long id, Appointment.AppointmentStatus status);
    AppointmentResponse getAppointment(Long id);
    List<AppointmentResponse> getDoctorAppointments(Long doctorId, Appointment.AppointmentStatus status);
    List<AppointmentResponse> getPatientAppointments(Long patientId);
    List<AppointmentResponse> getDoctorAppointmentsByDateRange(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<AppointmentResponse> getPatientAppointmentsByDateRange(Long patientId, LocalDateTime start, LocalDateTime end);
    void cancelAppointment(Long id);
    AppointmentResponse getPatientNextAppointment(Long patientId);
} 