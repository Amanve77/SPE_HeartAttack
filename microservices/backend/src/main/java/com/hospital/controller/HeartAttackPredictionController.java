package com.hospital.controller;

import com.hospital.dto.HeartAttackPredictionRequest;
import com.hospital.service.HeartAttackPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/predict")
@CrossOrigin(origins = "http://localhost:3000")
public class HeartAttackPredictionController {

    private final HeartAttackPredictionService predictionService;

    public HeartAttackPredictionController(HeartAttackPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/heart-attack-risk")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> predictHeartAttackRisk(
            @Valid @RequestBody HeartAttackPredictionRequest request) {
        
        Map<String, Object> features = new HashMap<>();
        features.put("age", request.getAge());
        features.put("sex", request.getSex());
        features.put("cp", request.getCp());
        features.put("trtbps", request.getTrtbps());
        features.put("chol", request.getChol());
        features.put("fbs", request.getFbs());
        features.put("restecg", request.getRestecg());
        features.put("thalachh", request.getThalachh());
        features.put("exng", request.getExng());
        features.put("oldpeak", request.getOldpeak());
        features.put("slp", request.getSlp());
        features.put("caa", request.getCaa());
        features.put("thall", request.getThall());

        return ResponseEntity.ok(predictionService.predictHeartAttackRisk(features));
    }
} 