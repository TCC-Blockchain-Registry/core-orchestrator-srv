package com.core.domain.model.property;

import java.time.LocalDateTime;

/**
 * Property Transfer Domain Model
 * Represents a property ownership transfer process
 * Transfer is only completed when registered on blockchain
 */
public class PropertyTransferModel {
    
    private Long id;
    private Long propertyId;
    private String fromProprietario;
    private String toProprietario;
    private TransferStatus status;
    private String blockchainTxHash;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PropertyTransferModel() {
        this.status = TransferStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PropertyTransferModel(Long id, Long propertyId, String fromProprietario, 
                                String toProprietario, TransferStatus status, 
                                String blockchainTxHash, LocalDateTime completedAt,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.fromProprietario = fromProprietario;
        this.toProprietario = toProprietario;
        this.status = status;
        this.blockchainTxHash = blockchainTxHash;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void approve() {
        this.status = TransferStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void reject() {
        this.status = TransferStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = TransferStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void completeWithBlockchain(String txHash) {
        this.status = TransferStatus.COMPLETED;
        this.blockchainTxHash = txHash;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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

