package com.core.adapter.output.persistence.transfer.mapper;

import com.core.domain.model.transfer.TransferModel;
import com.core.adapter.output.persistence.transfer.entity.TransferEntity;
import com.core.adapter.output.persistence.user.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between TransferModel (domain) and TransferEntity (persistence)
 */
@Component
public class TransferPersistenceMapper {

    /**
     * Convert TransferModel to TransferEntity
     */
    public TransferEntity toEntity(TransferModel model) {
        if (model == null) {
            return null;
        }

        TransferEntity entity = new TransferEntity();
        entity.setId(model.getId());
        entity.setMatriculaId(model.getMatriculaId());

        // Create references to UserEntity (JPA will handle the FKs)
        if (model.getSellerId() != null) {
            UserEntity sellerRef = new UserEntity();
            sellerRef.setId(model.getSellerId());
            entity.setSeller(sellerRef);
        }

        if (model.getBuyerId() != null) {
            UserEntity buyerRef = new UserEntity();
            buyerRef.setId(model.getBuyerId());
            entity.setBuyer(buyerRef);
        }

        entity.setStatus(model.getStatus());
        entity.setBlockchainTxHash(model.getBlockchainTxHash());
        entity.setRequestHash(model.getRequestHash());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());

        return entity;
    }

    /**
     * Convert TransferEntity to TransferModel
     */
    public TransferModel toDomain(TransferEntity entity) {
        if (entity == null) {
            return null;
        }

        // Extract user IDs from UserEntity relationships
        Long sellerId = (entity.getSeller() != null)
            ? entity.getSeller().getId()
            : null;

        Long buyerId = (entity.getBuyer() != null)
            ? entity.getBuyer().getId()
            : null;

        return new TransferModel(
            entity.getId(),
            entity.getMatriculaId(),
            sellerId,
            buyerId,
            entity.getStatus(),
            entity.getBlockchainTxHash(),
            entity.getRequestHash(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
