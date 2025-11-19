package com.core.port.output.property;

import com.core.domain.model.property.PropertyModel;
import java.util.List;
import java.util.Optional;

/**
 * Property Repository Output Port
 * Defines persistence operations for properties
 */
public interface PropertyRepositoryPort {
    
    /**
     * Save a property
     */
    PropertyModel save(PropertyModel property);
    
    /**
     * Find property by ID
     */
    Optional<PropertyModel> findById(Long id);
    
    /**
     * Find property by matricula ID
     */
    Optional<PropertyModel> findByMatriculaId(Long matriculaId);
    
    /**
     * Find all properties
     */
    List<PropertyModel> findAll();
    
    /**
     * Find properties by proprietario (user ID)
     */
    List<PropertyModel> findByProprietario(Long proprietario);
    
    /**
     * Find properties by comarca
     */
    List<PropertyModel> findByComarca(String comarca);
    
    /**
     * Delete property by ID
     */
    void deleteById(Long id);
}

