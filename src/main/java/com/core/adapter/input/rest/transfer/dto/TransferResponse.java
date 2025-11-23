package com.core.adapter.input.rest.transfer.dto;

import com.core.domain.model.transfer.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Transfer Response DTO
 */
@Schema(description = "Property transfer response data")
public record TransferResponse(

        @Schema(description = "Transfer ID", example = "1")
        Long id,

        @Schema(description = "Property matricula ID being transferred", example = "123456")
        Long matriculaId,

        @Schema(description = "Seller user ID", example = "1")
        Long sellerId,

        @Schema(description = "Buyer user ID", example = "2")
        Long buyerId,

        @Schema(description = "Transfer status", example = "AGUARDANDO_APROVACOES", allowableValues = {"PENDENTE", "CONFIGURANDO", "AGUARDANDO_APROVACOES", "CONCLUIDA"})
        TransferStatus status,

        @Schema(description = "Blockchain transaction hash", example = "0x...")
        String blockchainTxHash,

        @Schema(description = "Approval system request hash", example = "0x...")
        String requestHash,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {
}
