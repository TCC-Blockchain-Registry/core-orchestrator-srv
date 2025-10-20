package com.core.port.input.user;

import com.core.domain.model.user.UserModel;
import com.core.domain.model.user.UserRole;

/**
 * User Use Case Input Port
 * Defines the operations that can be performed on users
 */
public interface UserUseCase {
    
    /**
     * Register a new user in the system
     * 
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @param walletAddress User's Ethereum wallet address (optional)
     * @param role User's role (defaults to USER if null)
     * @return The registered user model
     * @throws IllegalArgumentException if validation fails
     */
    UserModel registerUser(String name, String email, String password, String walletAddress, UserRole role);
    
    /**
     * Authenticate a user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return The authenticated user model
     * @throws IllegalArgumentException if credentials are invalid
     */
    UserModel loginUser(String email, String password);
}
