package com.core.port.input.property;

import com.core.domain.model.property.PropertyTransferModel;

import java.util.List;

/**
 * Property Transfer Use Case Input Port
 * Defines operations for property transfer management
 */
public interface PropertyTransferUseCase {
    
    /**
     * Initiate a property transfer
     * Business rule: Property must not have any active transfer (PENDING or APPROVED)
     * 
     * @throws IllegalStateException if property already has an active transfer
     */
    PropertyTransferModel initiateTransfer(Long propertyId, String fromProprietario, 
                                          String toProprietario);
    
    /**
     * Complete transfer with blockchain transaction
     * Updates property owner and sets transfer as COMPLETED
     */
    PropertyTransferModel completeTransfer(Long transferId, String blockchainTxHash);
    
    /**
     * Approve transfer (when all approvals are granted)
     */
    PropertyTransferModel approveTransfer(Long transferId);
    
    /**
     * Reject transfer
     */
    PropertyTransferModel rejectTransfer(Long transferId);
    
    /**
     * Cancel transfer
     */
    PropertyTransferModel cancelTransfer(Long transferId);
    
    /**
     * Find transfer by ID
     */
    PropertyTransferModel findById(Long id);
    
    /**
     * Find all transfers for a property
     */
    List<PropertyTransferModel> findByPropertyId(Long propertyId);
    
    /**
     * Find all transfers
     */
    List<PropertyTransferModel> findAll();
    
    /**
     * Check if property has active transfer
     */
    boolean hasActiveTransfer(Long propertyId);
}

