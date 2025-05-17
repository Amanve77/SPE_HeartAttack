package com.hospital.config;

import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Add a test doctor if none exists
        if (doctorRepository.count() == 0) {
            // Create user for doctor
            User doctorUser = User.builder()
                .username("doctor")
                .email("doctor@test.com")
                .password(passwordEncoder.encode("doctor123"))
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.DOCTOR)
                .enabled(true)
                .build();
            
            doctorUser = userRepository.save(doctorUser);

            // Create doctor profile
            Doctor doctor = Doctor.builder()
                .user(doctorUser)
                .specialization("Cardiology")
                .department("Cardiology")
                .available(true)
                .build();
            
            doctorRepository.save(doctor);
        }

        // Add a test patient if none exists
        if (patientRepository.count() == 0) {
            // Create user for patient
            User patientUser = User.builder()
                .username("patient")
                .email("patient@test.com")
                .password(passwordEncoder.encode("patient123"))
                .firstName("Jane")
                .lastName("Smith")
                .role(User.Role.PATIENT)
                .enabled(true)
                .build();
            
            patientUser = userRepository.save(patientUser);

            // Create patient profile
            Patient patient = Patient.builder()
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .bloodGroup("O+")
                .build();
            
            patientRepository.save(patient);
        }
    }
} 