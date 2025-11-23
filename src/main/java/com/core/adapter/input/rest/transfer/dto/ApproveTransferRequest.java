package com.core.adapter.input.rest.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for approving a transfer
 */
@Schema(description = "Request for an approver to approve a transfer")
public record ApproveTransferRequest(

        @Schema(description = "Approver user ID (Cartório, Prefeitura, or Instituição Financeira)", example = "3", required = true)
        Long approverId
) {
}
