package com.hospital.service.impl;

import com.hospital.dto.ReportRequest;
import com.hospital.dto.ReportResponse;
import com.hospital.exception.BadRequestException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.Report;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.ReportRepository;
import com.hospital.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository) {
        this.reportRepository = reportRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public ReportResponse createReport(ReportRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));

        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        // Check if appointment is completed
        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot create report for non-completed appointment");
        }

        // Check if report already exists for this appointment
        if (reportRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new BadRequestException("Report already exists for this appointment");
        }

        Report report = Report.builder()
                .doctor(doctor)
                .patient(patient)
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .riskLevel(request.getRiskLevel())
                .recommendedTests(request.getRecommendedTests())
                .medications(request.getMedications())
                .lifestyleAdvice(request.getLifestyleAdvice())
                .build();

        return ReportResponse.fromReport(reportRepository.save(report));
    }

    @Override
    public ReportResponse getReport(Long id) {
        return reportRepository.findById(id)
                .map(ReportResponse::fromReport)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));
    }

    @Override
    public ReportResponse getReportByAppointment(Long appointmentId) {
        return reportRepository.findByAppointmentId(appointmentId)
                .map(ReportResponse::fromReport)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "appointmentId", appointmentId));
    }

    @Override
    public List<ReportResponse> getDoctorReports(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        return reportRepository.findByDoctor(doctor).stream()
                .map(ReportResponse::fromReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getPatientReports(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        return reportRepository.findByPatient(patient).stream()
                .map(ReportResponse::fromReport)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponse updateReport(Long id, ReportRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));

        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        // Check if appointment is completed
        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot update report for non-completed appointment");
        }

        report.setDoctor(doctor);
        report.setPatient(patient);
        report.setAppointment(appointment);
        report.setDiagnosis(request.getDiagnosis());
        report.setRiskLevel(request.getRiskLevel());
        report.setRecommendedTests(request.getRecommendedTests());
        report.setMedications(request.getMedications());
        report.setLifestyleAdvice(request.getLifestyleAdvice());

        return ReportResponse.fromReport(reportRepository.save(report));
    }

    @Override
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report", "id", id);
        }
        reportRepository.deleteById(id);
    }
} 