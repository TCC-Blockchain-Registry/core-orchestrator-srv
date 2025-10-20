package com.core.domain.model.property;

/**
 * Approval Status
 * Status of an individual approval in a property transfer
 */
public enum ApprovalStatus {
    /**
     * Waiting for approval
     */
    PENDING,
    
    /**
     * Approved by the approver
     */
    APPROVED,
    
    /**
     * Rejected by the approver
     */
    REJECTED
}

