package com.core.adapter.input.rest.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Transfer Complete Request DTO
 */
@Schema(description = "Property transfer completion request (blockchain registration)")
public record TransferCompleteRequest(
        
        @Schema(description = "Blockchain transaction hash", example = "0x1234567890abcdef...", required = true)
        String blockchainTxHash
) {
}

