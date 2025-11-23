package com.core.adapter.input.rest.property.mapper;

import com.core.adapter.input.rest.property.dto.PropertyResponse;
import com.core.domain.model.property.PropertyModel;

/**
 * Property Response Mapper
 * Maps PropertyModel (domain) to PropertyResponse (REST API DTO)
 *
 * This mapper follows the same pattern as UserResponseMapper,
 * providing a clean separation between domain models and API responses.
 */
public class PropertyResponseMapper {

    /**
     * Map PropertyModel to PropertyResponse
     *
     * Used for all property-related endpoints:
     * - Property registration response
     * - Get property by ID
     * - List all properties
     * - Get user's properties
     *
     * @param property Domain model from the business layer
     * @return PropertyResponse DTO for REST API
     */
    public static PropertyResponse toResponse(PropertyModel property) {
        return new PropertyResponse(
            property.getId(),
            property.getMatriculaId(),
            property.getFolha(),
            property.getComarca(),
            property.getEndereco(),
            property.getMetragem(),
            property.getProprietario(),
            property.getMatriculaOrigem(),
            property.getTipo(),
            property.getIsRegular(),
            property.getBlockchainTxHash(),
            property.getStatus(),
            property.getCreatedAt(),
            property.getUpdatedAt()
        );
    }
}
