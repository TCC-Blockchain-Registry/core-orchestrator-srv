package com.core.adapter.input.rest.user;

import com.core.adapter.input.rest.user.dto.UserLoginRequest;
import com.core.adapter.input.rest.user.dto.UserLoginResponse;
import com.core.adapter.input.rest.user.dto.UserRegistrationRequest;
import com.core.adapter.input.rest.user.dto.UserRegistrationResponse;
import com.core.adapter.input.rest.user.swagger.UserSwaggerApi;
import com.core.config.JwtService;
import com.core.domain.model.user.UserModel;
import com.core.port.input.user.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User REST Controller
 * Handles user-related HTTP requests
 */
@RestController
@RequestMapping("/api/users")
public class UserController implements UserSwaggerApi {
    
    private final UserUseCase userUseCase;
    private final JwtService jwtService;
    
    public UserController(UserUseCase userUseCase, JwtService jwtService) {
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
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
                request.walletAddress(),
                request.role()
            );
            
            UserRegistrationResponse response = new UserRegistrationResponse(
                registeredUser.getId(),
                registeredUser.getName(),
                registeredUser.getEmail(),
                registeredUser.getCpf(),
                registeredUser.getWalletAddress(),
                registeredUser.getRole(),
                registeredUser.getActive(),
                registeredUser.getCreatedAt()
            );
            
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
            UserModel authenticatedUser = userUseCase.loginUser(
                request.email(),
                request.password()
            );
            
            // Generate JWT token
            String token = jwtService.generateToken(
                authenticatedUser.getId(),
                authenticatedUser.getEmail(),
                authenticatedUser.getRole().name()
            );
            
            UserLoginResponse response = new UserLoginResponse(
                token,
                authenticatedUser.getId(),
                authenticatedUser.getName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getCpf(),
                authenticatedUser.getWalletAddress(),
                authenticatedUser.getRole(),
                authenticatedUser.getActive(),
                authenticatedUser.getCreatedAt(),
                "Login successful"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}