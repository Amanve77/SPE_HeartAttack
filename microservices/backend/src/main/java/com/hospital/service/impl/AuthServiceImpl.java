package com.hospital.service.impl;

import com.hospital.dto.JwtAuthResponse;
import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.exception.BadRequestException;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import com.hospital.security.CustomUserDetails;
import com.hospital.security.JwtTokenProvider;
import com.hospital.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         DoctorRepository doctorRepository,
                         PatientRepository patientRepository,
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        // First check if user exists with the given email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Validate that the user has the correct role
        if (user.getRole() != loginRequest.getRoleEnum()) {
            throw new BadRequestException("Invalid login attempt. Please use the correct login option for your role.");
        }

        // Proceed with authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return JwtAuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(User.Role.valueOf(userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")))
                .userId(userDetails.getId())
                .doctorId(userDetails.getDoctorId())
                .patientId(userDetails.getPatientId())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        // Create user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(registerRequest.getRoleEnum())
                .build();

        // Save user
        userRepository.save(user);

        // If user is a patient, create patient record
        if (user.getRole() == User.Role.PATIENT) {
            Patient patient = Patient.builder()
                    .user(user)
                    .build();
            patientRepository.save(patient);
        }
    }
} 