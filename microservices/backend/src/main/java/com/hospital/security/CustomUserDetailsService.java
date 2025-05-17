package com.hospital.security;

import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                  DoctorRepository doctorRepository,
                                  PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Long doctorId = null;
        Long patientId = null;

        if (user.getRole() == User.Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new UsernameNotFoundException("Doctor not found for user: " + email));
            doctorId = doctor.getId();
        } else if (user.getRole() == User.Role.PATIENT) {
            Patient patient = patientRepository.findByUser(user)
                    .orElseThrow(() -> new UsernameNotFoundException("Patient not found for user: " + email));
            patientId = patient.getId();
        }

        return new CustomUserDetails(user, doctorId, patientId);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with id : " + id)
                );

        return user;
    }
} 