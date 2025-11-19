package com.core.adapter.input.rest.user;

import com.core.adapter.input.rest.user.dto.UpdateWalletRequest;
import com.core.adapter.input.rest.user.dto.UserListResponse;
import com.core.adapter.input.rest.user.dto.UserLoginRequest;
import com.core.adapter.input.rest.user.dto.UserLoginResponse;
import com.core.adapter.input.rest.user.dto.UserRegistrationRequest;
import com.core.adapter.input.rest.user.dto.UserRegistrationResponse;
import com.core.adapter.input.rest.user.mapper.UserResponseMapper;
import com.core.adapter.input.rest.user.swagger.UserSwaggerApi;
import com.core.domain.model.user.LoginResult;
import com.core.domain.model.user.UserModel;
import com.core.domain.service.user.UserService;
import com.core.port.input.user.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User REST Controller
 * Handles user-related HTTP requests
 */
@RestController
@RequestMapping("/api/users")
public class UserController implements UserSwaggerApi {
    
    private final UserUseCase userUseCase;
    private final UserService userService;
    
    public UserController(UserUseCase userUseCase, UserService userService) {
        this.userUseCase = userUseCase;
        this.userService = userService;
    }
    
    @Override
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            UserModel registeredUser = userUseCase.registerUser(
                request.name(),
                request.email(),
                request.cpf(),
                request.password(),
                request.walletAddress()
            );
            
            UserRegistrationResponse response = UserResponseMapper.toRegistrationResponse(registeredUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("message", "Internal server error"));
        }
    }
    
    @Override
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@RequestBody UserLoginRequest request) {
        try {
            // Login and generate JWT token (handled in service layer)
            LoginResult loginResult = userService.loginUserWithToken(
                request.email(),
                request.password()
            );
            
            // Map to response DTO using mapper
            UserLoginResponse response = UserResponseMapper.toLoginResponse(
                loginResult.getUser(),
                loginResult.getToken()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        try {
            List<UserModel> users = userUseCase.getAllUsers();
            
            List<UserListResponse> response = users.stream()
                    .map(UserResponseMapper::toListResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @PutMapping("/{userId}/wallet")
    public ResponseEntity<UserListResponse> updateWalletAddress(
            @PathVariable Long userId,
            @RequestBody UpdateWalletRequest request) {
        try {
            UserModel updatedUser = userUseCase.updateWalletAddress(userId, request.walletAddress());
            
            UserListResponse response = UserResponseMapper.toListResponse(updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get current user profile from JWT token
     */
    @GetMapping("/me")
    public ResponseEntity<UserListResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            String token = authHeader.replace("Bearer ", "");
            
            // Extract userId from token using JwtService
            Long userId = userService.getJwtService().extractUserId(token);
            
            // Get user by ID
            UserModel user = userUseCase.getUserById(userId);
            
            UserListResponse response = UserResponseMapper.toListResponse(user);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}