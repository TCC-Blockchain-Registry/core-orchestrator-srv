package com.core.domain.service.user;

import com.core.domain.model.user.UserModel;
import com.core.domain.model.user.UserRole;
import com.core.port.input.user.UserUseCase;
import com.core.port.output.user.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * User Domain Service
 * Implements business logic for user operations
 */
@Service
public class UserService implements UserUseCase {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserModel registerUser(String name, String email, String cpf, String password, String walletAddress, UserRole role) {
        // Validate input parameters
        validateUserInput(name, email, password, walletAddress);

        // Check if user already exists
        Optional<UserModel> existingUser = userRepositoryPort.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        // Default to USER role if not specified
        UserRole userRole = (role != null) ? role : UserRole.USER;

        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(password);

        // Create new user with encrypted password
        UserModel newUser = new UserModel(name, email, cpf, encryptedPassword, walletAddress, userRole);
        
        // Save and return
        return userRepositoryPort.save(newUser);
    }
    
    @Override
    public UserModel loginUser(String email, String password) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Find user by email
        Optional<UserModel> userOptional = userRepositoryPort.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        UserModel user = userOptional.get();
        
        // Check if user is active
        if (!user.getActive()) {
            throw new IllegalArgumentException("User account is inactive");
        }
        
        // Verify password using BCrypt
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return user;
    }

    @Override
    public UserModel updateWalletAddress(String userId, String walletAddress) {
        // Validate input
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Wallet address cannot be empty");
        }

        // Validate wallet address format
        if (!walletAddress.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum wallet address format");
        }

        // Convert userId from String to Long
        Long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        // Find user by ID
        Optional<UserModel> userOptional = userRepositoryPort.findById(userIdLong);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserModel user = userOptional.get();

        // Update wallet address
        user.setWalletAddress(walletAddress);

        // Save and return updated user
        return userRepositoryPort.save(user);
    }

    private void validateUserInput(String name, String email, String password, String walletAddress) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        // Validate wallet address if provided
        if (walletAddress != null && !walletAddress.trim().isEmpty()) {
            if (!walletAddress.matches("^0x[a-fA-F0-9]{40}$")) {
                throw new IllegalArgumentException("Invalid Ethereum wallet address format");
            }
        }
    }
}
