package com.hospital.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class HeartAttackPredictionService {
    private final RestTemplate restTemplate;
    private final String predictionServiceUrl = "http://192.168.49.2:32077/predict";

    public HeartAttackPredictionService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> predictHeartAttackRisk(Map<String, Object> features) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(features, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                predictionServiceUrl,
                request,
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to make prediction: " + e.getMessage(), e);
        }
    }
} 