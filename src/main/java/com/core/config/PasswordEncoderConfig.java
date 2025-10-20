package com.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 * Configures BCrypt password encoder for the application
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * Creates a BCryptPasswordEncoder bean
     * BCrypt is a strong hashing algorithm designed for password storage
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

