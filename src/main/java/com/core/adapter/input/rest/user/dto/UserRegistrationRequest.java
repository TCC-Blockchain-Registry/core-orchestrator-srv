package com.core.adapter.input.rest.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User Registration Request DTO
 */
@Schema(description = "User registration request data")
public record UserRegistrationRequest(
        
        @Schema(description = "User's full name", example = "John Doe", required = true)
        String name,
        
        @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
        String email,
        
        @Schema(description = "User's CPF (Brazilian tax ID)", example = "12345678900")
        String cpf,
        
        @Schema(description = "User's password", example = "securePassword123", required = true)
        String password,
        
        @Schema(description = "User's Ethereum wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb", required = true)
        String walletAddress
) {
}
