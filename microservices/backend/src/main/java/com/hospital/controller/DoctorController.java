package com.hospital.controller;

import com.hospital.dto.DoctorResponse;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorResponse> response = doctors.stream()
            .map(doctor -> DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName())
                .email(doctor.getUser().getEmail())
                .specialization(doctor.getSpecialization())
                .department(doctor.getDepartment())
                .available(doctor.isAvailable())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
} 