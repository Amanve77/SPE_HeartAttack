package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospital.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", 
            message = "Username must be 3-20 characters long and can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(DOCTOR|PATIENT)$", message = "Role must be either 'DOCTOR' or 'PATIENT'")
    private String role;

    // Doctor specific fields
    @Size(min = 2, max = 50, message = "Specialization must be between 2 and 50 characters")
    private String specialization;

    @Size(min = 2, max = 50, message = "Department must be between 2 and 50 characters")
    private String department;

    @JsonIgnore
    public User.Role getRoleEnum() {
        return User.Role.valueOf(role);
    }
} 