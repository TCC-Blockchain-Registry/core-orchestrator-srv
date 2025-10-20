package com.core.adapter.output.persistence.user.mapper;

import com.core.domain.model.user.UserModel;
import com.core.adapter.output.persistence.user.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between UserModel (domain) and UserEntity (persistence)
 */
@Component
public class UserPersistenceMapper {
    
    /**
     * Convert UserModel to UserEntity
     * 
     * @param userModel The domain model
     * @return The persistence entity
     */
    public UserEntity toEntity(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setId(userModel.getId());
        entity.setName(userModel.getName());
        entity.setEmail(userModel.getEmail());
        entity.setPassword(userModel.getPassword());
        entity.setWalletAddress(userModel.getWalletAddress());
        entity.setRole(userModel.getRole());
        entity.setActive(userModel.getActive());
        entity.setCreatedAt(userModel.getCreatedAt());
        entity.setUpdatedAt(userModel.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Convert UserEntity to UserModel
     * 
     * @param userEntity The persistence entity
     * @return The domain model
     */
    public UserModel toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        
        return new UserModel(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.getWalletAddress(),
            userEntity.getRole(),
            userEntity.getActive(),
            userEntity.getCreatedAt(),
            userEntity.getUpdatedAt()
        );
    }
}
