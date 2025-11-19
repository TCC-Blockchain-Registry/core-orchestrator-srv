package com.core.domain.model.user;

/**
 * Login Result
 * Encapsulates the result of a successful login (user data + JWT token)
 */
public class LoginResult {
    
    private final UserModel user;
    private final String token;
    
    public LoginResult(UserModel user, String token) {
        this.user = user;
        this.token = token;
    }
    
    public UserModel getUser() {
        return user;
    }
    
    public String getToken() {
        return token;
    }
}

