package com.core.adapter.input.rest.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Update Wallet Address Request DTO
 */
@Schema(description = "Update user wallet address request data")
public record UpdateWalletRequest(

        @Schema(description = "User's Ethereum wallet address", example = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb", required = true)
        String walletAddress
) {
}
