package com.hospital.security;

import com.hospital.model.User;
import com.hospital.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("appointmentSecurity")
public class AppointmentSecurity {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentSecurity.class);
    private final AppointmentRepository appointmentRepository;

    public AppointmentSecurity(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean isCurrentDoctor(Authentication authentication, Long doctorId) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            logger.debug("Checking doctor access - User Role: {}, Requested Doctor ID: {}", 
                userDetails.getAuthorities().iterator().next().getAuthority(), doctorId);
            
            Long authenticatedDoctorId = userDetails.getDoctorId();
            boolean matches = doctorId != null && doctorId.equals(authenticatedDoctorId);
            
            logger.debug("Doctor ID comparison - User's Doctor ID: {}, Requested ID: {}, Matches: {}", 
                authenticatedDoctorId, doctorId, matches);
            
            if (!matches) {
                logger.debug("Access denied - Doctor IDs do not match or doctor not found");
            }
            
            return matches;
        }
        return false;
    }

    public boolean isCurrentPatient(Authentication authentication, Long patientId) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            logger.debug("Checking patient access - User Role: {}, Requested Patient ID: {}", 
                userDetails.getAuthorities().iterator().next().getAuthority(), patientId);
            
            Long authenticatedPatientId = userDetails.getPatientId();
            boolean matches = patientId != null && patientId.equals(authenticatedPatientId);
            
            logger.debug("Patient ID comparison - User's Patient ID: {}, Requested ID: {}, Matches: {}", 
                authenticatedPatientId, patientId, matches);
            
            if (!matches) {
                logger.debug("Access denied - Patient IDs do not match or patient not found");
            }
            
            return matches;
        }
        return false;
    }

    public boolean isDoctorOfAppointment(Authentication authentication, Long appointmentId) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_DOCTOR")) {
                return false;
            }

            return appointmentRepository.findById(appointmentId)
                    .map(appointment -> appointment.getDoctor().getId().equals(userDetails.getDoctorId()))
                    .orElse(false);
        }
        return false;
    }

    public boolean canAccessAppointment(Authentication authentication, Long appointmentId) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            
            return appointmentRepository.findById(appointmentId)
                    .map(appointment -> {
                        if (role.equals("ROLE_DOCTOR")) {
                            return appointment.getDoctor().getId().equals(userDetails.getDoctorId());
                        } else if (role.equals("ROLE_PATIENT")) {
                            return appointment.getPatient().getId().equals(userDetails.getPatientId());
                        }
                        return false;
                    })
                    .orElse(false);
        }
        return false;
    }
} 