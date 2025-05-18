package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class HeartAttackPredictionRequest {
    @NotNull
    @Min(value = 0, message = "Age must be greater than or equal to 0")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private Integer age;
    
    @NotNull
    private Integer sex; // 0 = female, 1 = male
    
    @NotNull
    private Integer cp; // chest pain type
    
    @NotNull
    private Integer trtbps; // resting blood pressure (renamed from trestbps)
    
    @NotNull
    private Integer chol; // serum cholesterol
    
    @NotNull
    private Integer fbs; // fasting blood sugar
    
    @NotNull
    private Integer restecg; // resting ECG results
    
    @NotNull
    private Integer thalachh; // maximum heart rate achieved (renamed from thalach)
    
    @NotNull
    private Integer exng; // exercise induced angina (renamed from exang)
    
    @NotNull
    private Double oldpeak; // ST depression induced by exercise
    
    @NotNull
    private Integer slp; // slope of peak exercise ST segment (renamed from slope)
    
    @NotNull
    private Integer caa; // number of major vessels (renamed from ca)
    
    @NotNull
    private Integer thall; // thalassemia (renamed from thal)
} 