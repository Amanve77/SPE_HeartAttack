package com.hospital.security;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {
    
    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    
    // JWT token validity in milliseconds (24 hours)
    @Bean
    public long jwtExpirationInMs() {
        return 24 * 60 * 60 * 1000;
    }
} 