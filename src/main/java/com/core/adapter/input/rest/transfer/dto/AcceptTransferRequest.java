package com.core.adapter.input.rest.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for buyer accepting a transfer
 */
@Schema(description = "Request for buyer to accept a property transfer")
public record AcceptTransferRequest(

        @Schema(description = "Buyer user ID (must match the transfer's buyer)", example = "2", required = true)
        Long buyerId
) {
}
