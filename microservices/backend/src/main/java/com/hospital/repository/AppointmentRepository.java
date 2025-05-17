package com.hospital.repository;

import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctorAndDateBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientAndDateBetween(Patient patient, LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.date >= :now ORDER BY a.date ASC")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.date >= :now ORDER BY a.date ASC")
    List<Appointment> findDoctorUpcomingAppointments(@Param("doctor") Doctor doctor, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.date >= :now ORDER BY a.date ASC")
    List<Appointment> findPatientUpcomingAppointments(@Param("patient") Patient patient, @Param("now") LocalDateTime now);

    List<Appointment> findByPatientIdAndDateBetweenOrderByDateAsc(Long patientId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndDateAfterOrderByDateAsc(Long patientId, LocalDateTime date);
    Optional<Appointment> findFirstByPatientAndDateAfterOrderByDateAsc(Patient patient, LocalDateTime now);

} 