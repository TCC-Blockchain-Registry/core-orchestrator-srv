package com.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for Spring Boot Application
 */
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {

    @Test
    public void contextLoads() {
        // This test verifies that the Spring context loads successfully
        // Uses H2 in-memory database for testing
    }
}
