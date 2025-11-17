package com.core.adapter.output.persistence.property.mapper;

import com.core.domain.model.property.PropertyModel;
import com.core.adapter.output.persistence.property.entity.PropertyEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between PropertyModel (domain) and PropertyEntity (persistence)
 */
@Component
public class PropertyPersistenceMapper {
    
    /**
     * Convert PropertyModel to PropertyEntity
     */
    public PropertyEntity toEntity(PropertyModel model) {
        if (model == null) {
            return null;
        }
        
        PropertyEntity entity = new PropertyEntity();
        entity.setId(model.getId());
        entity.setMatriculaId(model.getMatriculaId());
        entity.setFolha(model.getFolha());
        entity.setComarca(model.getComarca());
        entity.setEndereco(model.getEndereco());
        entity.setMetragem(model.getMetragem());
        entity.setProprietario(model.getProprietario());
        entity.setMatriculaOrigem(model.getMatriculaOrigem());
        entity.setTipo(model.getTipo());
        entity.setIsRegular(model.getIsRegular());
        entity.setBlockchainTxHash(model.getBlockchainTxHash());
        entity.setRequestHash(model.getRequestHash());
        entity.setApprovalStatus(model.getApprovalStatus());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Convert PropertyEntity to PropertyModel
     */
    public PropertyModel toDomain(PropertyEntity entity) {
        if (entity == null) {
            return null;
        }
        
        PropertyModel model = new PropertyModel(
            entity.getId(),
            entity.getMatriculaId(),
            entity.getFolha(),
            entity.getComarca(),
            entity.getEndereco(),
            entity.getMetragem(),
            entity.getProprietario(),
            entity.getMatriculaOrigem(),
            entity.getTipo(),
            entity.getIsRegular(),
            entity.getBlockchainTxHash(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
        
        // Set additional V2 fields
        model.setRequestHash(entity.getRequestHash());
        model.setApprovalStatus(entity.getApprovalStatus());
        model.setStatus(entity.getStatus());
        
        return model;
    }
}

