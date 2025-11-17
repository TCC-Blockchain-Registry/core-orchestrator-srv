package com.core.config;

import com.core.domain.model.user.UserModel;
import com.core.domain.model.user.UserRole;
import com.core.port.output.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Admin User Initializer
 * Creates a default admin user on application startup if it doesn't exist
 */
@Configuration
public class AdminUserInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);
    
    private static final String DEFAULT_ADMIN_NAME = "Administrator";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@core-orchestrator.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    
    @Bean
    CommandLineRunner initAdminUser(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // Check if admin user already exists
                if (userRepositoryPort.findByEmail(DEFAULT_ADMIN_EMAIL).isEmpty()) {
                    logger.info("Creating default admin user...");
                    
                    // Encrypt admin password
                    String encryptedPassword = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);
                    
                    // Create admin user with encrypted password (no wallet address)
                    UserModel adminUser = new UserModel(
                        DEFAULT_ADMIN_NAME,
                        DEFAULT_ADMIN_EMAIL,
                        null,  // Admin doesn't need CPF
                        encryptedPassword,
                        null,  // Admin doesn't need wallet initially
                        UserRole.ADMIN
                    );
                    
                    userRepositoryPort.save(adminUser);
                    
                    logger.info("✅ Default admin user created successfully!");
                    logger.info("📧 Email: {}", DEFAULT_ADMIN_EMAIL);
                    logger.info("🔑 Password: {}", DEFAULT_ADMIN_PASSWORD);
                    logger.warn("⚠️  IMPORTANT: Change the admin password after first login!");
                } else {
                    logger.info("Admin user already exists. Skipping creation.");
                }
            } catch (Exception e) {
                logger.error("❌ Failed to create admin user: {}", e.getMessage());
            }
        };
    }
}

