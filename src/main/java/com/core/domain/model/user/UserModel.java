package com.core.domain.model.user;

import java.time.LocalDateTime;

/**
 * User Domain Model
 * Represents a user entity in the domain layer
 */
public class UserModel {

    private Long id;
    private String name;
    private String email;
    private String cpf;
    private String password;
    private String walletAddress;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public UserModel() {
        this.role = UserRole.USER;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for registration
    public UserModel(String name, String email, String cpf, String password, String walletAddress) {
        this();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.walletAddress = walletAddress;
    }

    // Constructor for registration with role
    public UserModel(String name, String email, String cpf, String password, String walletAddress, UserRole role) {
        this();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.walletAddress = walletAddress;
        this.role = role;
    }

    // Constructor with all fields
    public UserModel(Long id, String name, String email, String cpf, String password, String walletAddress,
                    UserRole role, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.walletAddress = walletAddress;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Business methods
    public static UserModel registerUser(String name, String email, String cpf, String password, String walletAddress) {
        return new UserModel(name, email, cpf, password, walletAddress);
    }
    
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getWalletAddress() {
        return walletAddress;
    }
    
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
