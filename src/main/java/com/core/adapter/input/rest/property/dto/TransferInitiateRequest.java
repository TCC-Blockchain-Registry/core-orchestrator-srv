package com.core.adapter.input.rest.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Transfer Initiate Request DTO
 */
@Schema(description = "Property transfer initiation request")
public record TransferInitiateRequest(
        
        @Schema(description = "Property ID to transfer", example = "1", required = true)
        Long propertyId,
        
        @Schema(description = "Current owner wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb", required = true)
        String fromProprietario,
        
        @Schema(description = "New owner wallet address", example = "0x123d35Cc6634C0532925a3b844Bc9e7595f0aaa", required = true)
        String toProprietario
) {
}

