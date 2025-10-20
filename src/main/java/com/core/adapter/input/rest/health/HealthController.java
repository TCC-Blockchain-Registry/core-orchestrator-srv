package com.core.adapter.input.rest.health;

import com.core.adapter.input.rest.health.swagger.HealthSwaggerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for application status
 */
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController implements HealthSwaggerApi {

    @GetMapping
    @Override
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("application", "Core Orchestrator Service");
        health.put("version", "1.0-SNAPSHOT");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("java_version", System.getProperty("java.version"));
        health.put("profile", "default");
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/ping")
    @Override
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    
    @GetMapping("/info")
    @Override
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("app_name", "Core Orchestrator Service");
        info.put("description", "Microservice for core orchestration using hexagonal architecture");
        info.put("java_version", System.getProperty("java.version"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_arch", System.getProperty("os.arch"));
        info.put("uptime_ms", System.currentTimeMillis());
        info.put("memory_total_mb", Runtime.getRuntime().totalMemory() / (1024 * 1024));
        info.put("memory_free_mb", Runtime.getRuntime().freeMemory() / (1024 * 1024));
        info.put("processors", Runtime.getRuntime().availableProcessors());
        
        return ResponseEntity.ok(info);
    }
}
