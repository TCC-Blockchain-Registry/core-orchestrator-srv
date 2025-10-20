package com.core.domain.model.property;

import java.time.LocalDateTime;

/**
 * Property Transfer Approval Domain Model
 * Represents an approval from a specific approver type
 * Each transfer requires approvals from: Cartório, Bank, and Prefeitura
 */
public class PropertyTransferApprovalModel {
    
    private Long id;
    private Long transferId;           // FK para PropertyTransfer
    private ApproverType approverType; // Tipo do aprovador
    private Long approverUserId;       // ID do usuário que aprovou/rejeitou
    private ApprovalStatus status;     // Status da aprovação
    private String comments;           // Comentários opcionais
    private LocalDateTime approvedAt;  // Data da aprovação/rejeição
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyTransferApprovalModel() {
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with all fields
    public PropertyTransferApprovalModel(Long id, Long transferId, ApproverType approverType,
                                        Long approverUserId, ApprovalStatus status, 
                                        String comments, LocalDateTime approvedAt,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.transferId = transferId;
        this.approverType = approverType;
        this.approverUserId = approverUserId;
        this.status = status;
        this.comments = comments;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public void approve(Long userId, String comments) {
        this.status = ApprovalStatus.APPROVED;
        this.approverUserId = userId;
        this.comments = comments;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void reject(Long userId, String comments) {
        this.status = ApprovalStatus.REJECTED;
        this.approverUserId = userId;
        this.comments = comments;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTransferId() {
        return transferId;
    }
    
    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }
    
    public ApproverType getApproverType() {
        return approverType;
    }
    
    public void setApproverType(ApproverType approverType) {
        this.approverType = approverType;
    }
    
    public Long getApproverUserId() {
        return approverUserId;
    }
    
    public void setApproverUserId(Long approverUserId) {
        this.approverUserId = approverUserId;
    }
    
    public ApprovalStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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

