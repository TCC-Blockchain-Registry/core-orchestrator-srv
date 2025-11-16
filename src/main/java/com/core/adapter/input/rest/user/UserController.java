package com.core.adapter.input.rest.user;

import com.core.adapter.input.rest.user.dto.UpdateWalletRequest;
import com.core.adapter.input.rest.user.dto.UserLoginRequest;
import com.core.adapter.input.rest.user.dto.UserLoginResponse;
import com.core.adapter.input.rest.user.dto.UserRegistrationRequest;
import com.core.adapter.input.rest.user.dto.UserRegistrationResponse;
import com.core.adapter.input.rest.user.swagger.UserSwaggerApi;
import com.core.domain.model.user.UserModel;
import com.core.domain.service.auth.JwtService;
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
@CrossOrigin(origins = "*")
public class UserController implements UserSwaggerApi {

    private final UserUseCase userUseCase;
    private final JwtService jwtService;

    public UserController(UserUseCase userUseCase, JwtService jwtService) {
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
    }
    
    @Override
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationRequest request) {
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
                registeredUser.getWalletAddress(),
                registeredUser.getRole(),
                registeredUser.getActive(),
                registeredUser.getCreatedAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @Override
    @PutMapping("/{id}/wallet")
    public ResponseEntity<UserRegistrationResponse> updateWallet(
            @PathVariable("id") String userId,
            @RequestBody UpdateWalletRequest request) {
        try {
            UserModel updatedUser = userUseCase.updateWalletAddress(userId, request.walletAddress());

            UserRegistrationResponse response = new UserRegistrationResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getWalletAddress(),
                updatedUser.getRole(),
                updatedUser.getActive(),
                updatedUser.getCreatedAt()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}