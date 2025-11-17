package com.core.domain.model.blockchain;

/**
 * Blockchain Job Types
 * Enum representing the different types of blockchain operations
 */
public enum JobType {
    /**
     * Register a new property on blockchain
     */
    REGISTER_PROPERTY,
    
    /**
     * Configure a property transfer with approvers
     */
    CONFIGURE_TRANSFER,
    
    /**
     * Approver approves a transfer
     */
    APPROVE_TRANSFER,
    
    /**
     * Buyer accepts a transfer
     */
    ACCEPT_TRANSFER,
    
    /**
     * Execute the final transfer
     */
    EXECUTE_TRANSFER,
    
    /**
     * Register a new approver entity
     */
    REGISTER_APPROVER,
    
    /**
     * Freeze a property
     */
    FREEZE_PROPERTY,
    
    /**
     * Unfreeze a property
     */
    UNFREEZE_PROPERTY
}

