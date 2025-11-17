package com.core.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Service
 * Handles JWT token generation and validation
 */
@Service
public class JwtService {
    
    @Value("${jwt.secret:my-super-secret-key-for-jwt-token-generation-minimum-256-bits}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;
    
    /**
     * Generate JWT token for a user
     * 
     * @param userId User's ID
     * @param email User's email
     * @param role User's role
     * @return JWT token string
     */
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        
        return createToken(claims, email);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

