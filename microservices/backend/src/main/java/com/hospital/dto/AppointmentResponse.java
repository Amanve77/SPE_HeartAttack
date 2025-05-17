package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorUsername;
    private String doctorName;
    private String doctorSpecialization;
    private Long patientId;
    private String patientUsername;
    private String patientName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String reason;
    private AppointmentStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static AppointmentResponse fromAppointment(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorUsername(appointment.getDoctor().getUsername())
                .doctorName(appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .patientId(appointment.getPatient().getId())
                .patientUsername(appointment.getPatient().getUsername())
                .patientName(appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName())
                .date(appointment.getDate())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
} 