package com.core.port.output.user;

import com.core.domain.model.user.UserModel;
import java.util.Optional;

/**
 * User Repository Output Port
 * Defines the contract for user persistence operations
 */
public interface UserRepositoryPort {
    
    /**
     * Save a user to the repository
     * 
     * @param user The user model to save
     * @return The saved user model with generated ID
     */
    UserModel save(UserModel user);
    
    /**
     * Find a user by email address
     *
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<UserModel> findByEmail(String email);

    /**
     * Find a user by ID
     *
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<UserModel> findById(Long id);
}
