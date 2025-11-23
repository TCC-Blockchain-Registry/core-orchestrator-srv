package com.core.adapter.input.rest.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for initiating a property transfer
 */
@Schema(description = "Request to initiate a property transfer")
public record InitiateTransferRequest(

        @Schema(description = "Property matricula ID", example = "123456", required = true)
        Long matriculaId,

        @Schema(description = "Buyer user ID", example = "2", required = true)
        Long buyerId
) {
}
