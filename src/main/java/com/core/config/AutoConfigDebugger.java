package com.core.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@Component
public class AutoConfigDebugger {

    private static final Logger logger = LoggerFactory.getLogger(AutoConfigDebugger.class);

    @PostConstruct
    public void listAutoConfigFiles() {
        logger.info("[AUTOCONFIG-DEBUG] Searching for AutoConfiguration.imports files...");
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
            
            int count = 0;
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                count++;
                logger.info("[AUTOCONFIG-DEBUG] Found AutoConfiguration.imports #{}: {}", count, url);
            }
            
            if (count == 0) {
                logger.warn("[AUTOCONFIG-DEBUG] NO AutoConfiguration.imports files found!");
            } else {
                logger.info("[AUTOCONFIG-DEBUG] Total AutoConfiguration.imports files found: {}", count);
            }
            
        } catch (IOException e) {
            logger.error("[AUTOCONFIG-DEBUG] Error searching for AutoConfiguration.imports: {}", e.getMessage(), e);
        }
    }
}

