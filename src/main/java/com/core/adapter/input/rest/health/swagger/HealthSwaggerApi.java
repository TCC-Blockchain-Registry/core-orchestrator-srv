package com.core.adapter.input.rest.health.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Swagger documentation interface for Health Check API
 */
@Tag(name = "Health Check", description = "APIs for application health monitoring and status")
public interface HealthSwaggerApi {
    
    @Operation(
        summary = "Application health check",
        description = "Returns the current status and basic information about the application"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Application is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = """
                {
                  "status": "UP",
                  "application": "Core Orchestrator Service",
                  "version": "1.0-SNAPSHOT",
                  "timestamp": "2023-12-01T10:30:00",
                  "java_version": "21.0.1",
                  "profile": "default"
                }
                """)
            )
        )
    })
    ResponseEntity<Map<String, Object>> healthCheck();
    
    @Operation(
        summary = "Simple ping endpoint",
        description = "Returns 'pong' to verify the application is responding"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Application is responding",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string", example = "pong")
            )
        )
    })
    ResponseEntity<String> ping();
    
    @Operation(
        summary = "Detailed application information",
        description = "Returns detailed system and application information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "System information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = """
                {
                  "app_name": "Core Orchestrator Service",
                  "description": "Microservice for core orchestration using hexagonal architecture",
                  "java_version": "21.0.1",
                  "os_name": "Linux",
                  "os_arch": "amd64",
                  "uptime_ms": 1234567890,
                  "memory_total_mb": 512,
                  "memory_free_mb": 256,
                  "processors": 4
                }
                """)
            )
        )
    })
    ResponseEntity<Map<String, Object>> info();
}
