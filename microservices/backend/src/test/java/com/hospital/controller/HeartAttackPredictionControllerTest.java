package com.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.config.TestConfig;
import com.hospital.config.TestSecurityConfig;
import com.hospital.dto.HeartAttackPredictionRequest;
import com.hospital.service.HeartAttackPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeartAttackPredictionController.class)
@Import({TestSecurityConfig.class, TestConfig.class})
class HeartAttackPredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HeartAttackPredictionService predictionService;

    private HeartAttackPredictionRequest predictionRequest;
    private Map<String, Object> predictionResponse;

    @BeforeEach
    void setUp() {
        predictionRequest = new HeartAttackPredictionRequest();
        predictionRequest.setAge(65);
        predictionRequest.setSex(1);
        predictionRequest.setCp(3);
        predictionRequest.setTrtbps(145);
        predictionRequest.setChol(233);
        predictionRequest.setFbs(1);
        predictionRequest.setRestecg(0);
        predictionRequest.setThalachh(150);
        predictionRequest.setExng(0);
        predictionRequest.setOldpeak(2.3);
        predictionRequest.setSlp(0);
        predictionRequest.setCaa(0);
        predictionRequest.setThall(1);

        predictionResponse = new HashMap<>();
        predictionResponse.put("prediction", 1);
        predictionResponse.put("probability", 0.85);
    }

    @Test
    @WithMockUser(username = "doctor1", roles = "DOCTOR")
    void predictHeartAttackRiskSuccess() throws Exception {
        when(predictionService.predictHeartAttackRisk(any(Map.class))).thenReturn(predictionResponse);

        mockMvc.perform(post("/api/predict/heart-attack-risk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(predictionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prediction").value(1))
                .andExpect(jsonPath("$.probability").value(0.85));
    }

    @Test
    @WithMockUser(username = "doctor1", roles = "DOCTOR")
    void predictHeartAttackRiskWithInvalidData() throws Exception {
        predictionRequest.setAge(-1); // Invalid age

        mockMvc.perform(post("/api/predict/heart-attack-risk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(predictionRequest)))
                .andExpect(status().isBadRequest());
    }
}