package com.hospital.dto;

import com.hospital.model.Report;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private Long appointmentId;
    private String diagnosis;
    private String riskLevel;
    private String recommendedTests;
    private String medications;
    private String lifestyleAdvice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReportResponse fromReport(Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setDoctorId(report.getDoctor().getId());
        response.setDoctorName(report.getDoctor().getUser().getFirstName() + " " + report.getDoctor().getUser().getLastName());
        response.setPatientId(report.getPatient().getId());
        response.setPatientName(report.getPatient().getUser().getFirstName() + " " + report.getPatient().getUser().getLastName());
        response.setAppointmentId(report.getAppointment().getId());
        response.setDiagnosis(report.getDiagnosis());
        response.setRiskLevel(report.getRiskLevel().name());
        response.setRecommendedTests(report.getRecommendedTests());
        response.setMedications(report.getMedications());
        response.setLifestyleAdvice(report.getLifestyleAdvice());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());
        return response;
    }
} 