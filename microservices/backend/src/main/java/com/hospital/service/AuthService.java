package com.hospital.service;

import com.hospital.dto.JwtAuthResponse;
import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
    JwtAuthResponse register(RegisterRequest registerRequest);
} 