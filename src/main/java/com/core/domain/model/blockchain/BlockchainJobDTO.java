package com.core.domain.model.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Blockchain Job Data Transfer Object
 * Represents a job to be processed by the blockchain queue worker
 */
public class BlockchainJobDTO {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("type")
    private JobType type;
    
    @JsonProperty("payload")
    private Map<String, Object> payload;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("maxAttempts")
    private Integer maxAttempts;
    
    /**
     * Constructor
     */
    public BlockchainJobDTO() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now().toString();
        this.maxAttempts = 3; // Default retry attempts
    }
    
    public BlockchainJobDTO(JobType type, Map<String, Object> payload) {
        this();
        this.type = type;
        this.payload = payload;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public JobType getType() {
        return type;
    }
    
    public void setType(JobType type) {
        this.type = type;
    }
    
    public Map<String, Object> getPayload() {
        return payload;
    }
    
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    @Override
    public String toString() {
        return "BlockchainJobDTO{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", payload=" + payload +
                ", createdAt='" + createdAt + '\'' +
                ", maxAttempts=" + maxAttempts +
                '}';
    }
}

