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
                if (userRepositoryPort.findByEmail(DEFAULT_ADMIN_EMAIL).isEmpty()) {
                    logger.info("Creating default admin user...");

                    String encryptedPassword = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);

                    UserModel adminUser = new UserModel(
                        DEFAULT_ADMIN_NAME,
                        DEFAULT_ADMIN_EMAIL,
                        "000.000.000-00",  // Dummy CPF for admin
                        encryptedPassword,
                        null,
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

