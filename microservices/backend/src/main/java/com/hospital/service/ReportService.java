package com.hospital.service;

import com.hospital.dto.ReportRequest;
import com.hospital.dto.ReportResponse;

import java.util.List;

public interface ReportService {
    ReportResponse createReport(ReportRequest request);
    ReportResponse getReport(Long id);
    ReportResponse getReportByAppointment(Long appointmentId);
    List<ReportResponse> getDoctorReports(Long doctorId);
    List<ReportResponse> getPatientReports(Long patientId);
    ReportResponse updateReport(Long id, ReportRequest request);
    void deleteReport(Long id);
} 