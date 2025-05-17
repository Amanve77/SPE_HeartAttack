package com.hospital.service.impl;

import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentResponse;
import com.hospital.exception.BadRequestException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                DoctorRepository doctorRepository,
                                PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));

        // Validate appointment date
        if (request.getDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment date must be in the future");
        }

        // Check for conflicting appointments
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorAndDateBetween(
                doctor,
                request.getDate().minusHours(1),
                request.getDate().plusHours(1)
        );

        if (!doctorAppointments.isEmpty()) {
            throw new BadRequestException("Doctor has another appointment at this time");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(request.getDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        return AppointmentResponse.fromAppointment(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status of cancelled appointment");
        }

        appointment.setStatus(status);
        return AppointmentResponse.fromAppointment(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .map(AppointmentResponse::fromAppointment)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId, Appointment.AppointmentStatus status) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        List<Appointment> appointments;
        if (status != null) {
            appointments = appointmentRepository.findByDoctorAndStatus(doctor, status);
        } else {
            appointments = appointmentRepository.findByDoctor(doctor);
        }

        return appointments.stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        return appointmentRepository.findByPatient(patient).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointmentsByDateRange(Long doctorId, LocalDateTime start, LocalDateTime end) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }

        return appointmentRepository.findByDoctorAndDateBetween(doctor, start, end).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getPatientAppointmentsByDateRange(Long patientId, LocalDateTime start, LocalDateTime end) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }

        return appointmentRepository.findByPatientAndDateBetween(patient, start, end).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed appointment");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }
    @Override
    public AppointmentResponse getPatientNextAppointment(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        Appointment appointment = appointmentRepository.findFirstByPatientAndDateAfterOrderByDateAsc(patient, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "next appointment for patient", patientId));

        return AppointmentResponse.fromAppointment(appointment);
    }


} 