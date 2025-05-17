package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }
} 