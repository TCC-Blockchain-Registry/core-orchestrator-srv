package com.core.adapter.input.rest.property.dto;

import com.core.domain.model.property.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Property Registration Request DTO
 */
@Schema(description = "Property registration request data")
public record PropertyRegistrationRequest(
        
        @Schema(description = "Matricula ID (unique identifier)", example = "123456", required = true)
        Long matriculaId,
        
        @Schema(description = "Folha number", example = "100", required = true)
        Long folha,
        
        @Schema(description = "Comarca name", example = "São Paulo", required = true)
        String comarca,
        
        @Schema(description = "Property address", example = "Rua Example, 123", required = true)
        String endereco,
        
        @Schema(description = "Property size in square meters", example = "250", required = true)
        Long metragem,
        
        @Schema(description = "Owner user ID", example = "1", required = true)
        Long proprietario,
        
        @Schema(description = "Original matricula ID (if applicable)", example = "123450")
        Long matriculaOrigem,
        
        @Schema(description = "Property type", example = "URBANO", required = true, allowableValues = {"URBANO", "RURAL", "LITORAL"})
        PropertyType tipo,
        
        @Schema(description = "Property regularity status", example = "true")
        Boolean isRegular
) {
}

