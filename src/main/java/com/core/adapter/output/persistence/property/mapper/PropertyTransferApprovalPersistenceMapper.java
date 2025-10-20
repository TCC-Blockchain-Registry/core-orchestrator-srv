package com.core.adapter.output.persistence.property.mapper;

import com.core.domain.model.property.PropertyTransferApprovalModel;
import com.core.adapter.output.persistence.property.entity.PropertyTransferApprovalEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between PropertyTransferApprovalModel (domain) and PropertyTransferApprovalEntity (persistence)
 */
@Component
public class PropertyTransferApprovalPersistenceMapper {
    
    /**
     * Convert PropertyTransferApprovalModel to PropertyTransferApprovalEntity
     */
    public PropertyTransferApprovalEntity toEntity(PropertyTransferApprovalModel model) {
        if (model == null) {
            return null;
        }
        
        PropertyTransferApprovalEntity entity = new PropertyTransferApprovalEntity();
        entity.setId(model.getId());
        entity.setTransferId(model.getTransferId());
        entity.setApproverType(model.getApproverType());
        entity.setApproverUserId(model.getApproverUserId());
        entity.setStatus(model.getStatus());
        entity.setComments(model.getComments());
        entity.setApprovedAt(model.getApprovedAt());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Convert PropertyTransferApprovalEntity to PropertyTransferApprovalModel
     */
    public PropertyTransferApprovalModel toDomain(PropertyTransferApprovalEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new PropertyTransferApprovalModel(
            entity.getId(),
            entity.getTransferId(),
            entity.getApproverType(),
            entity.getApproverUserId(),
            entity.getStatus(),
            entity.getComments(),
            entity.getApprovedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

