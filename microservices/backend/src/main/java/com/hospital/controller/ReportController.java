package com.hospital.controller;

import com.hospital.dto.ReportRequest;
import com.hospital.dto.ReportResponse;
import com.hospital.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ReportResponse> getReportByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(reportService.getReportByAppointment(appointmentId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') and @userSecurity.hasDoctorId(#doctorId)")
    public ResponseEntity<List<ReportResponse>> getDoctorReports(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reportService.getDoctorReports(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') and @userSecurity.hasPatientId(#patientId)")
    public ResponseEntity<List<ReportResponse>> getPatientReports(@PathVariable Long patientId) {
        return ResponseEntity.ok(reportService.getPatientReports(patientId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
} 