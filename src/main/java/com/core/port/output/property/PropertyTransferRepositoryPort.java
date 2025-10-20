package com.core.port.output.property;

import com.core.domain.model.property.PropertyTransferModel;
import java.util.List;
import java.util.Optional;

/**
 * Property Transfer Repository Output Port
 * Defines persistence operations for property transfers
 */
public interface PropertyTransferRepositoryPort {
    
    /**
     * Save a property transfer
     */
    PropertyTransferModel save(PropertyTransferModel transfer);
    
    /**
     * Find transfer by ID
     */
    Optional<PropertyTransferModel> findById(Long id);
    
    /**
     * Find all transfers for a specific property
     */
    List<PropertyTransferModel> findByPropertyId(Long propertyId);
    
    /**
     * Find all transfers from a proprietario
     */
    List<PropertyTransferModel> findByFromProprietario(String fromProprietario);
    
    /**
     * Find all transfers to a proprietario
     */
    List<PropertyTransferModel> findByToProprietario(String toProprietario);
    
    /**
     * Find all transfers
     */
    List<PropertyTransferModel> findAll();
}

