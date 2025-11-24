package com.core.adapter.input.rest.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for initiating a property transfer
 * Supports identification of buyer by CPF or wallet address
 */
@Schema(description = "Request to initiate a property transfer")
public record InitiateTransferRequest(

        @Schema(description = "Property matricula ID", example = "123456", required = true)
        Long matriculaId,

        @Schema(description = "Buyer CPF (11 digits, no formatting). Either buyerCpf or buyerWalletAddress must be provided", example = "12345678901")
        String buyerCpf,

        @Schema(description = "Buyer wallet address (0x...). Either buyerCpf or buyerWalletAddress must be provided", example = "0x1234567890abcdef1234567890abcdef12345678")
        String buyerWalletAddress
) {
    /**
     * Validates that exactly one buyer identification method is provided
     */
    public void validate() {
        if ((buyerCpf == null || buyerCpf.isBlank()) &&
            (buyerWalletAddress == null || buyerWalletAddress.isBlank())) {
            throw new IllegalArgumentException("Either buyerCpf or buyerWalletAddress must be provided");
        }

        if ((buyerCpf != null && !buyerCpf.isBlank()) &&
            (buyerWalletAddress != null && !buyerWalletAddress.isBlank())) {
            throw new IllegalArgumentException("Provide either buyerCpf or buyerWalletAddress, not both");
        }

        // Validate CPF format if provided
        if (buyerCpf != null && !buyerCpf.isBlank()) {
            if (!buyerCpf.matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF must contain exactly 11 digits");
            }
        }

        // Validate wallet address format if provided
        if (buyerWalletAddress != null && !buyerWalletAddress.isBlank()) {
            if (!buyerWalletAddress.matches("^0x[a-fA-F0-9]{40}$")) {
                throw new IllegalArgumentException("Invalid wallet address format. Must be 0x followed by 40 hex characters");
            }
        }
    }
}
