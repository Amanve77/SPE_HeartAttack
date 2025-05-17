package com.hospital.repository;

import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByBloodGroup(String bloodGroup);
    
    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth >= :startDate AND p.dateOfBirth <= :endDate")
    List<Patient> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.appointments a WHERE a.doctor.id = :doctorId")
    List<Patient> findByDoctorId(Long doctorId);

    Optional<Patient> findByUser(User user);
    Optional<Patient> findByUserUsername(String username);
    Optional<Patient> findByUserEmail(String email);
} 