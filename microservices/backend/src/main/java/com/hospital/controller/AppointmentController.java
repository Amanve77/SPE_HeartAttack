package com.hospital.controller;

import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentResponse;
import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
@Tag(name = "Appointments", description = "Appointment management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Operation(summary = "Create new appointment", description = "Create a new appointment for a patient with a doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Doctor or Patient not found"),
        @ApiResponse(responseCode = "409", description = "Doctor is not available at the requested time")
    })
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Update the status of an existing appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or appointment cannot be updated"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PreAuthorize("hasRole('DOCTOR') and @appointmentSecurity.isDoctorOfAppointment(authentication, #id)")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Parameter(description = "New appointment status") @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @GetMapping("/{id}")
    @Cacheable(value = "appointments", key = "#id")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT') and @appointmentSecurity.canAccessAppointment(authentication, #id)")
    public ResponseEntity<AppointmentResponse> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor's appointments", description = "Retrieve all appointments for a specific doctor, optionally filtered by status")
    @PreAuthorize("hasRole('DOCTOR') and @appointmentSecurity.isCurrentDoctor(authentication, #doctorId)")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Filter by appointment status") @RequestParam(required = false) AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId, status));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient's appointments", description = "Retrieve all appointments for a specific patient")
    @PreAuthorize("hasRole('PATIENT') and @appointmentSecurity.isCurrentPatient(authentication, #patientId)")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/doctor/{doctorId}/range")
    @Operation(summary = "Get doctor's appointments by date range", description = "Retrieve doctor's appointments within a specific date range")
    @PreAuthorize("hasRole('DOCTOR') and @appointmentSecurity.isCurrentDoctor(authentication, #doctorId)")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointmentsByDateRange(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Start date and time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date and time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsByDateRange(doctorId, start, end));
    }

    @GetMapping("/patient/{patientId}/range")
    @Operation(summary = "Get patient's appointments by date range", description = "Retrieve patient's appointments within a specific date range")
    @PreAuthorize("hasRole('PATIENT') and @appointmentSecurity.isCurrentPatient(authentication, #patientId)")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointmentsByDateRange(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Start date and time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date and time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getPatientAppointmentsByDateRange(patientId, start, end));
    }

    @GetMapping("/patient/{patientId}/next")
    @Operation(summary = "Get patient's next appointment", description = "Retrieve the next scheduled appointment for a specific patient")
    @PreAuthorize("hasRole('PATIENT') and @appointmentSecurity.isCurrentPatient(authentication, #patientId)")
    public ResponseEntity<AppointmentResponse> getPatientNextAppointment(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientNextAppointment(patientId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Appointment cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "400", description = "Appointment cannot be cancelled")
    })
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT') and @appointmentSecurity.canAccessAppointment(authentication, #id)")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
} 