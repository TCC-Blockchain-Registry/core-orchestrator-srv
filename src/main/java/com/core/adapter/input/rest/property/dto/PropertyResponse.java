package com.core.adapter.input.rest.property.dto;

import com.core.domain.model.property.PropertyStatus;
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
        
        @Schema(description = "Owner user ID", example = "1")
        Long proprietario,
        
        @Schema(description = "Original matricula ID", example = "123450")
        Long matriculaOrigem,
        
        @Schema(description = "Property type", example = "URBANO")
        PropertyType tipo,
        
        @Schema(description = "Property regularity status", example = "true")
        Boolean isRegular,
        
        @Schema(description = "Blockchain transaction hash", example = "0x...")
        String blockchainTxHash,

        @Schema(description = "Property status", example = "PENDENTE", allowableValues = {"PENDENTE", "PROCESSANDO_REGISTRO", "EM_TRANSFERENCIA", "OK"})
        PropertyStatus status,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}

