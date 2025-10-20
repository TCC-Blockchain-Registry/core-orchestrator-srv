package com.core.adapter.input.rest.property.dto;

import com.core.domain.model.property.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Property Response DTO
 */
@Schema(description = "Property response data")
public record PropertyResponse(
        
        @Schema(description = "Property ID", example = "1")
        Long id,
        
        @Schema(description = "Matricula ID", example = "123456")
        Long matriculaId,
        
        @Schema(description = "Folha number", example = "100")
        Long folha,
        
        @Schema(description = "Comarca name", example = "São Paulo")
        String comarca,
        
        @Schema(description = "Property address", example = "Rua Example, 123")
        String endereco,
        
        @Schema(description = "Property size in square meters", example = "250")
        Long metragem,
        
        @Schema(description = "Owner wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb")
        String proprietario,
        
        @Schema(description = "Original matricula ID", example = "123450")
        Long matriculaOrigem,
        
        @Schema(description = "Property type", example = "URBANO")
        PropertyType tipo,
        
        @Schema(description = "Property regularity status", example = "true")
        Boolean isRegular,
        
        @Schema(description = "Blockchain transaction hash", example = "0x...")
        String blockchainTxHash,
        
        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}

