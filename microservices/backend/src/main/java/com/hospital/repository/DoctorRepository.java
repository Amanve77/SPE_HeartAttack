package com.hospital.repository;

import com.hospital.model.Doctor;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialization(String specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.available = true")
    List<Doctor> findAvailableDoctors();
    
    List<Doctor> findByDepartment(String department);

    Optional<Doctor> findByUser(User user);
    Optional<Doctor> findByUserUsername(String username);
    Optional<Doctor> findByUserEmail(String email);
} 