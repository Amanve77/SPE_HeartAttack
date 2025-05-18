package com.hospital.controller;

import com.hospital.config.TestConfig;
import com.hospital.config.TestSecurityConfig;
import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentResponse;
import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.model.User;
import com.hospital.model.User.Role;
import com.hospital.security.CustomUserDetails;
import com.hospital.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import({TestSecurityConfig.class, TestConfig.class})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private AppointmentRequest appointmentRequest;
    private AppointmentResponse appointmentResponse;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CustomUserDetails doctorUserDetails;
    private CustomUserDetails patientUserDetails;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().plusDays(1);
        endTime = startTime.plusHours(1);

        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setDoctorId(1L);
        appointmentRequest.setPatientId(1L);
        appointmentRequest.setDate(startTime);
        appointmentRequest.setReason("Regular checkup");

        appointmentResponse = new AppointmentResponse();
        appointmentResponse.setId(1L);
        appointmentResponse.setDoctorId(1L);
        appointmentResponse.setDoctorUsername("doctor1");
        appointmentResponse.setDoctorName("Dr. John Doe");
        appointmentResponse.setDoctorSpecialization("Cardiology");
        appointmentResponse.setPatientId(1L);
        appointmentResponse.setPatientUsername("patient1");
        appointmentResponse.setPatientName("John Smith");
        appointmentResponse.setDate(startTime);
        appointmentResponse.setReason("Regular checkup");
        appointmentResponse.setStatus(AppointmentStatus.SCHEDULED);
        appointmentResponse.setCreatedAt(LocalDateTime.now());
        appointmentResponse.setUpdatedAt(LocalDateTime.now());

        User doctorUser = new User();
        doctorUser.setId(1L);
        doctorUser.setUsername("doctor1");
        doctorUser.setPassword("password");
        doctorUser.setEmail("doctor1@example.com");
        doctorUser.setRole(Role.DOCTOR);

        User patientUser = new User();
        patientUser.setId(2L);
        patientUser.setUsername("patient1");
        patientUser.setPassword("password");
        patientUser.setEmail("patient1@example.com");
        patientUser.setRole(Role.PATIENT);

        doctorUserDetails = new CustomUserDetails(doctorUser, 1L, null);
        patientUserDetails = new CustomUserDetails(patientUser, null, 1L);
    }




    @Test
    void getDoctorAppointmentsSuccess() throws Exception {
        authenticate(doctorUserDetails);

        when(appointmentService.getDoctorAppointments(eq(1L), any()))
                .thenReturn(Collections.singletonList(appointmentResponse));

        mockMvc.perform(get("/api/appointments/doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getPatientAppointmentsSuccess() throws Exception {
        authenticate(patientUserDetails);

        when(appointmentService.getPatientAppointments(1L))
                .thenReturn(Collections.singletonList(appointmentResponse));

        mockMvc.perform(get("/api/appointments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }



    @Test
    void getPatientNextAppointmentSuccess() throws Exception {
        authenticate(patientUserDetails);

        when(appointmentService.getPatientNextAppointment(1L))
                .thenReturn(appointmentResponse);

        mockMvc.perform(get("/api/appointments/patient/1/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getDoctorAppointmentsByDateRangeSuccess() throws Exception {
        authenticate(doctorUserDetails);

        when(appointmentService.getDoctorAppointmentsByDateRange(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(appointmentResponse));

        mockMvc.perform(get("/api/appointments/doctor/1/range")
                        .param("start", startTime.toString())
                        .param("end", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // Utility method to manually set authenticated user in Spring Security Context
    private void authenticate(CustomUserDetails userDetails) {
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}