package com.core.port.input.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyType;

import java.util.List;

/**
 * Property Use Case Input Port
 * Defines operations for property management
 */
public interface PropertyUseCase {
    
    /**
     * Register a new property
     */
    PropertyModel registerProperty(Long matriculaId, Long folha, String comarca, 
                                   String endereco, Long metragem, String proprietario,
                                   Long matriculaOrigem, PropertyType tipo, Boolean isRegular);
    
    /**
     * Find property by ID
     */
    PropertyModel findById(Long id);
    
    /**
     * Find property by matricula ID
     */
    PropertyModel findByMatriculaId(Long matriculaId);
    
    /**
     * Find all properties
     */
    List<PropertyModel> findAll();
    
    /**
     * Find properties by proprietario (wallet address)
     */
    List<PropertyModel> findByProprietario(String proprietario);
    
    /**
     * Find properties by comarca
     */
    List<PropertyModel> findByComarca(String comarca);
    
    /**
     * Update property blockchain transaction hash
     */
    PropertyModel updateBlockchainTxHash(Long id, String txHash);
}

