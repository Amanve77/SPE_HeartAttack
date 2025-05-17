package com.hospital.dto;

import com.hospital.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private User.Role role;
    private Long userId;
    private Long doctorId;  // Only set for doctors
    private Long patientId; // Only set for patients
} 