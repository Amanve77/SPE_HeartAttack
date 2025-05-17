package com.hospital.repository;

import com.hospital.model.Report;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByDoctor(Doctor doctor);
    List<Report> findByPatient(Patient patient);
    Optional<Report> findByAppointmentId(Long appointmentId);
} 