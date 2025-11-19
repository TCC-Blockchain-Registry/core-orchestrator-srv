package com.core.adapter.input.rest.user.mapper;

import com.core.adapter.input.rest.user.dto.UserListResponse;
import com.core.adapter.input.rest.user.dto.UserLoginResponse;
import com.core.adapter.input.rest.user.dto.UserRegistrationResponse;
import com.core.domain.model.user.UserModel;

/**
 * User Response Mapper
 * Maps UserModel to REST API response DTOs
 */
public class UserResponseMapper {
    
    /**
     * Map UserModel to UserRegistrationResponse
     */
    public static UserRegistrationResponse toRegistrationResponse(UserModel user) {
        return new UserRegistrationResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf(),
            user.getWalletAddress(),
            user.getActive(),
            user.getCreatedAt()
        );
    }
    
    /**
     * Map UserModel to UserLoginResponse
     */
    public static UserLoginResponse toLoginResponse(UserModel user, String token) {
        return new UserLoginResponse(
            token,
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf(),
            user.getWalletAddress(),
            user.getActive(),
            user.getCreatedAt(),
            "Login successful"
        );
    }
    
    /**
     * Map UserModel to UserListResponse
     */
    public static UserListResponse toListResponse(UserModel user) {
        return new UserListResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf(),
            user.getWalletAddress(),
            user.getActive(),
            user.getCreatedAt()
        );
    }
}

