package com.core.adapter.input.rest.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User Login Request DTO
 */
@Schema(description = "User login request data")
public record UserLoginRequest(
        
        @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
        String email,
        
        @Schema(description = "User's password", example = "securePassword123", required = true)
        String password
) {
}

