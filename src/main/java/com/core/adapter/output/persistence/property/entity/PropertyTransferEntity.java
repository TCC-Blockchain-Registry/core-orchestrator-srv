package com.core.adapter.output.persistence.property.entity;

import com.core.domain.model.property.TransferStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Property Transfer Entity for Hibernate persistence
 * Stores property ownership transfer process
 */
@Entity
@Table(name = "property_transfers")
public class PropertyTransferEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "property_id", nullable = false)
    private Long propertyId;
    
    @Column(name = "from_proprietario", nullable = false, length = 42)
    private String fromProprietario;
    
    @Column(name = "to_proprietario", nullable = false, length = 42)
    private String toProprietario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransferStatus status;
    
    @Column(name = "blockchain_tx_hash", length = 66)
    private String blockchainTxHash;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyTransferEntity() {
    }
    
    // Constructor with all fields
    public PropertyTransferEntity(Long propertyId, String fromProprietario, String toProprietario,
                                 TransferStatus status, String blockchainTxHash, 
                                 LocalDateTime completedAt, LocalDateTime createdAt, 
                                 LocalDateTime updatedAt) {
        this.propertyId = propertyId;
        this.fromProprietario = fromProprietario;
        this.toProprietario = toProprietario;
        this.status = status;
        this.blockchainTxHash = blockchainTxHash;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public String getFromProprietario() {
        return fromProprietario;
    }
    
    public void setFromProprietario(String fromProprietario) {
        this.fromProprietario = fromProprietario;
    }
    
    public String getToProprietario() {
        return toProprietario;
    }
    
    public void setToProprietario(String toProprietario) {
        this.toProprietario = toProprietario;
    }
    
    public TransferStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransferStatus status) {
        this.status = status;
    }
    
    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }
    
    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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

