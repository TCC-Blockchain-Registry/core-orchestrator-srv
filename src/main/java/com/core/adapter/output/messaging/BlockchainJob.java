package com.core.adapter.output.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Blockchain Job DTO for RabbitMQ messages
 *
 * Represents a job to be processed by the Queue Worker
 */
public class BlockchainJob {

    private String id;
    private String type;
    private Object payload;
    private LocalDateTime createdAt;
    private Integer maxAttempts;

    public BlockchainJob() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.maxAttempts = 3;
    }

    public BlockchainJob(String type, Object payload) {
        this();
        this.type = type;
        this.payload = payload;
    }

    public BlockchainJob(String type, Object payload, Integer maxAttempts) {
        this(type, payload);
        this.maxAttempts = maxAttempts;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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
        return "BlockchainJob{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", maxAttempts=" + maxAttempts +
                '}';
    }
}
