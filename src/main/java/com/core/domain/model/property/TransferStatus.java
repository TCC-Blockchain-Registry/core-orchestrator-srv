package com.core.domain.model.property;

/**
 * Property Transfer Status
 * Represents the current status of a property transfer
 */
public enum TransferStatus {
    /**
     * Transfer initiated, waiting for approvals
     */
    PENDING,
    
    /**
     * All approvals granted, waiting for blockchain registration
     */
    APPROVED,
    
    /**
     * Registered on blockchain, transfer completed
     */
    COMPLETED,
    
    /**
     * Transfer rejected by one of the approvers
     */
    REJECTED,
    
    /**
     * Transfer cancelled by the requester
     */
    CANCELLED
}

