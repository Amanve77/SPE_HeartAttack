package com.hospital.dto;

import com.hospital.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(DOCTOR|PATIENT)$", message = "Role must be either 'DOCTOR' or 'PATIENT'")
    private String role;

    public User.Role getRoleEnum() {
        return User.Role.valueOf(role);
    }
} 