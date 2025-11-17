package com.core.adapter.input.rest.user.dto;

import com.core.domain.model.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * User Login Response DTO
 */
@Schema(description = "User login response data")
public record UserLoginResponse(
        
        @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        
        @Schema(description = "User's unique identifier", example = "1")
        Long id,
        
        @Schema(description = "User's full name", example = "John Doe")
        String name,
        
        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "User's CPF (Brazilian tax ID)", example = "12345678900")
        String cpf,
        
        @Schema(description = "User's Ethereum wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb")
        String walletAddress,
        
        @Schema(description = "User's role", example = "USER")
        UserRole role,
        
        @Schema(description = "User's account status", example = "true")
        Boolean active,
        
        @Schema(description = "Account creation timestamp", example = "2023-12-01T10:30:00")
        LocalDateTime createdAt,
        
        @Schema(description = "Login success message", example = "Login successful")
        String message
) {
}

