package com.core.adapter.output.persistence.property.entity;

import com.core.domain.model.property.ApprovalStatus;
import com.core.domain.model.property.ApproverType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Property Transfer Approval Entity for Hibernate persistence
 * Stores approvals from Cartório, Bank, and Prefeitura
 */
@Entity
@Table(name = "property_transfer_approvals")
public class PropertyTransferApprovalEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "transfer_id", nullable = false)
    private Long transferId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approver_type", nullable = false, length = 20)
    private ApproverType approverType;
    
    @Column(name = "approver_user_id")
    private Long approverUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApprovalStatus status;
    
    @Column(name = "comments", length = 500)
    private String comments;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyTransferApprovalEntity() {
    }
    
    // Constructor with all fields
    public PropertyTransferApprovalEntity(Long transferId, ApproverType approverType,
                                         Long approverUserId, ApprovalStatus status,
                                         String comments, LocalDateTime approvedAt,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.transferId = transferId;
        this.approverType = approverType;
        this.approverUserId = approverUserId;
        this.status = status;
        this.comments = comments;
        this.approvedAt = approvedAt;
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

