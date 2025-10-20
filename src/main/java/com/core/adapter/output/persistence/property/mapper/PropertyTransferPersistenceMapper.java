package com.core.adapter.output.persistence.property.mapper;

import com.core.domain.model.property.PropertyTransferModel;
import com.core.adapter.output.persistence.property.entity.PropertyTransferEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between PropertyTransferModel (domain) and PropertyTransferEntity (persistence)
 */
@Component
public class PropertyTransferPersistenceMapper {
    
    /**
     * Convert PropertyTransferModel to PropertyTransferEntity
     */
    public PropertyTransferEntity toEntity(PropertyTransferModel model) {
        if (model == null) {
            return null;
        }
        
        PropertyTransferEntity entity = new PropertyTransferEntity();
        entity.setId(model.getId());
        entity.setPropertyId(model.getPropertyId());
        entity.setFromProprietario(model.getFromProprietario());
        entity.setToProprietario(model.getToProprietario());
        entity.setStatus(model.getStatus());
        entity.setBlockchainTxHash(model.getBlockchainTxHash());
        entity.setCompletedAt(model.getCompletedAt());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Convert PropertyTransferEntity to PropertyTransferModel
     */
    public PropertyTransferModel toDomain(PropertyTransferEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new PropertyTransferModel(
            entity.getId(),
            entity.getPropertyId(),
            entity.getFromProprietario(),
            entity.getToProprietario(),
            entity.getStatus(),
            entity.getBlockchainTxHash(),
            entity.getCompletedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

