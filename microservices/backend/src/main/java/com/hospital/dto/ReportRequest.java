package com.hospital.dto;

import com.hospital.model.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotNull(message = "Risk level is required")
    private Report.RiskLevel riskLevel;

    private String recommendedTests;
    private String medications;
    private String lifestyleAdvice;
} 