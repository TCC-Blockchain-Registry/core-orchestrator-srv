package com.core.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActuatorTestConfig {

    private static final Logger logger = LoggerFactory.getLogger(ActuatorTestConfig.class);

    @PostConstruct
    public void checkActuatorClasses() {
        logger.info("[ACTUATOR-TEST] Checking if Actuator classes can be loaded...");
        
        try {
            Class.forName("org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping");
            logger.info("[ACTUATOR-TEST] WebMvcEndpointHandlerMapping CAN be loaded");
        } catch (ClassNotFoundException e) {
            logger.error("[ACTUATOR-TEST] WebMvcEndpointHandlerMapping CANNOT be loaded: {}", e.getMessage());
        }
        
        try {
            Class.forName("org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties");
            logger.info("[ACTUATOR-TEST] WebEndpointProperties CAN be loaded");
        } catch (ClassNotFoundException e) {
            logger.error("[ACTUATOR-TEST] WebEndpointProperties CANNOT be loaded: {}", e.getMessage());
        }
        
        try {
            Class.forName("org.springframework.boot.actuate.health.HealthEndpoint");
            logger.info("[ACTUATOR-TEST] HealthEndpoint CAN be loaded");
        } catch (ClassNotFoundException e) {
            logger.error("[ACTUATOR-TEST] HealthEndpoint CANNOT be loaded: {}", e.getMessage());
        }
    }
}

