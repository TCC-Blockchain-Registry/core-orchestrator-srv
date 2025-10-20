package com.core.adapter.input.rest.property.dto;

import com.core.domain.model.property.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Transfer Response DTO
 */
@Schema(description = "Property transfer response data")
public record TransferResponse(
        
        @Schema(description = "Transfer ID", example = "1")
        Long id,
        
        @Schema(description = "Property ID", example = "1")
        Long propertyId,
        
        @Schema(description = "From proprietario wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb")
        String fromProprietario,
        
        @Schema(description = "To proprietario wallet address", example = "0x123d35Cc6634C0532925a3b844Bc9e7595f0aaa")
        String toProprietario,
        
        @Schema(description = "Transfer status", example = "PENDING")
        TransferStatus status,
        
        @Schema(description = "Blockchain transaction hash", example = "0x...")
        String blockchainTxHash,
        
        @Schema(description = "Completion timestamp")
        LocalDateTime completedAt,
        
        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}

