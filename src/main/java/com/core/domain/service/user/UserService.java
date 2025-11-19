package com.core.domain.service.user;

import com.core.config.JwtService;
import com.core.domain.model.user.LoginResult;
import com.core.domain.model.user.UserModel;
import com.core.port.input.user.UserUseCase;
import com.core.port.output.user.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final JwtService jwtService;
    
    public UserService(
            UserRepositoryPort userRepositoryPort, 
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    /**
     * Get JwtService instance
     * Used for direct token operations when needed
     */
    public JwtService getJwtService() {
        return jwtService;
    }
    
    @Override
    public UserModel registerUser(String name, String email, String cpf, String password, String walletAddress) {
        // Validate input parameters
        validateUserInput(name, email, cpf, password, walletAddress);
        
        // Check if user already exists
        Optional<UserModel> existingUser = userRepositoryPort.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(password);
        
        // Create new user with encrypted password
        UserModel newUser = new UserModel(name, email, cpf, encryptedPassword, walletAddress);
        
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
    
    /**
     * Login user and generate JWT token
     * 
     * @param email User's email
     * @param password User's password
     * @return LoginResult containing user data and JWT token
     */
    public LoginResult loginUserWithToken(String email, String password) {
        // Authenticate user
        UserModel user = loginUser(email, password);
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        
        return new LoginResult(user, token);
    }
    
    @Override
    public List<UserModel> getAllUsers() {
        return userRepositoryPort.findAll();
    }
    
    @Override
    public UserModel updateWalletAddress(Long userId, String walletAddress) {
        // Validate wallet address
        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Wallet address cannot be empty");
        }
        
        if (!walletAddress.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum wallet address format");
        }
        
        // Find user
        Optional<UserModel> userOptional = userRepositoryPort.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        UserModel user = userOptional.get();
        
        // Update wallet address
        user.setWalletAddress(walletAddress);
        
        // Save updated user
        return userRepositoryPort.save(user);
    }
    
    @Override
    public UserModel getUserById(Long userId) {
        // Find user by ID
        Optional<UserModel> userOptional = userRepositoryPort.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        return userOptional.get();
    }
    
    private void validateUserInput(String name, String email, String cpf, String password, String walletAddress) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Validate CPF if provided
        if (cpf != null && !cpf.trim().isEmpty()) {
            // Remove any non-digit characters
            String cleanCpf = cpf.replaceAll("\\D", "");
            if (cleanCpf.length() != 11) {
                throw new IllegalArgumentException("CPF must have exactly 11 digits");
            }
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        // Wallet address is now required
        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Wallet address cannot be empty");
        }
        
        if (!walletAddress.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum wallet address format");
        }
    }
}
