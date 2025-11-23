package com.core.domain.model.transfer;

import java.time.LocalDateTime;

/**
 * Transfer Domain Model
 * Represents a property ownership transfer request in the system
 */
public class TransferModel {

    private Long id;
    private Long matriculaId;          // Property being transferred
    private Long sellerId;             // Current owner (FK to users)
    private Long buyerId;              // New owner (FK to users)
    private TransferStatus status;     // Transfer lifecycle status
    private String blockchainTxHash;   // Transaction hash from blockchain
    private String requestHash;        // Approval system request hash (V2)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public TransferModel() {
        this.status = TransferStatus.PENDENTE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public TransferModel(Long matriculaId, Long sellerId, Long buyerId) {
        this();
        this.matriculaId = matriculaId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    // Full constructor
    public TransferModel(Long id, Long matriculaId, Long sellerId, Long buyerId,
                        TransferStatus status, String blockchainTxHash, String requestHash,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.matriculaId = matriculaId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.status = status;
        this.blockchainTxHash = blockchainTxHash;
        this.requestHash = requestHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Business methods

    /**
     * Updates the transfer status and timestamp
     */
    public void updateStatus(TransferStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Sets the blockchain transaction hash
     */
    public void setTransactionHash(String txHash) {
        this.blockchainTxHash = txHash;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Sets the request hash for V2 approval system
     */
    public void setApprovalRequestHash(String requestHash) {
        this.requestHash = requestHash;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks transfer as completed
     */
    public void complete() {
        this.status = TransferStatus.CONCLUIDA;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if transfer is in final state
     */
    public boolean isCompleted() {
        return this.status == TransferStatus.CONCLUIDA;
    }

    /**
     * Checks if transfer can be approved (must be waiting for approvals)
     */
    public boolean canBeApproved() {
        return this.status == TransferStatus.AGUARDANDO_APROVACOES;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
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

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
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
