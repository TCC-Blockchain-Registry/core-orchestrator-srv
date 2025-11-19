package com.core.adapter.input.rest.property.swagger;

import com.core.adapter.input.rest.property.dto.PropertyRegistrationRequest;
import com.core.adapter.input.rest.property.dto.PropertyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Swagger API interface for Property endpoints
 */
@Tag(name = "Property Management", description = "APIs for property registration and management")
public interface PropertySwaggerApi {
    
    @Operation(
        summary = "Register a new property",
        description = "Registers a new property in the system. Will be registered on blockchain later."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Property registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Property with this matricula already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<PropertyResponse> registerProperty(
        @RequestBody(description = "Property registration data", required = true)
        PropertyRegistrationRequest request
    );
    
    @Operation(
        summary = "Get property by ID",
        description = "Retrieves a property by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Property found"),
        @ApiResponse(responseCode = "404", description = "Property not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<PropertyResponse> getPropertyById(
        @Parameter(description = "Property ID", required = true)
        Long id
    );
    
    @Operation(
        summary = "Get all properties",
        description = "Retrieves all properties in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Properties retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<PropertyResponse>> getAllProperties();
}

