package com.core.adapter.input.rest.user.swagger;

import com.core.adapter.input.rest.user.dto.UpdateWalletRequest;
import com.core.adapter.input.rest.user.dto.UserLoginRequest;
import com.core.adapter.input.rest.user.dto.UserLoginResponse;
import com.core.adapter.input.rest.user.dto.UserRegistrationRequest;
import com.core.adapter.input.rest.user.dto.UserRegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Swagger API interface for User endpoints
 */
@Tag(name = "User Management", description = "APIs for user registration, authentication and management")
public interface UserSwaggerApi {
    
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information. Passwords are automatically encrypted using BCrypt."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User with this email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UserRegistrationResponse> registerUser(
        @RequestBody(description = "User registration data", required = true)
        UserRegistrationRequest request
    );
    
    @Operation(
        summary = "User login",
        description = "Authenticates a user with email and password. Password is verified using BCrypt."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UserLoginResponse> loginUser(
        @RequestBody(description = "User login credentials", required = true)
        UserLoginRequest request
    );

    @Operation(
        summary = "Update user wallet address",
        description = "Updates the Ethereum wallet address for a specific user. Used when a user connects their MetaMask wallet."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallet address updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid wallet address format"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UserRegistrationResponse> updateWallet(
        @Parameter(description = "User ID", required = true)
        String userId,
        @RequestBody(description = "Wallet address update data", required = true)
        UpdateWalletRequest request
    );
}
