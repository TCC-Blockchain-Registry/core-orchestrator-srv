package com.core.adapter.input.rest.transfer.swagger;

import com.core.adapter.input.rest.transfer.dto.AcceptTransferRequest;
import com.core.adapter.input.rest.transfer.dto.ApproveTransferRequest;
import com.core.adapter.input.rest.transfer.dto.InitiateTransferRequest;
import com.core.adapter.input.rest.transfer.dto.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Swagger API interface for Transfer endpoints
 */
@Tag(name = "Transfers", description = "Property ownership transfer operations")
public interface TransferSwaggerApi {

    @Operation(
        summary = "Initiate a property transfer",
        description = "Creates a transfer request and publishes CONFIGURE_TRANSFER job to blockchain"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transfer initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Property already has an active transfer or invalid state"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> initiateTransfer(
        @RequestBody(description = "Transfer initiation data", required = true)
        InitiateTransferRequest request
    );

    @Operation(
        summary = "Get transfer by ID",
        description = "Retrieve transfer details including status and participants"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer found"),
        @ApiResponse(responseCode = "404", description = "Transfer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> getTransferById(
        @Parameter(description = "Transfer ID", required = true)
        Long id
    );

    @Operation(
        summary = "Get transfers by property matricula ID",
        description = "List all transfer requests for a specific property"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TransferResponse>> getTransfersByProperty(
        @Parameter(description = "Property matricula ID", required = true)
        Long matriculaId
    );

    @Operation(
        summary = "Approve a transfer",
        description = "Approver (Cartório, Prefeitura, or Instituição Financeira) approves the transfer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Transfer not found"),
        @ApiResponse(responseCode = "409", description = "Transfer cannot be approved in current state"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> approveTransfer(
        @Parameter(description = "Transfer ID", required = true)
        Long id,
        @RequestBody(description = "Approval data", required = true)
        ApproveTransferRequest request
    );

    @Operation(
        summary = "Accept a transfer",
        description = "Buyer accepts the property transfer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer accepted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or buyer mismatch"),
        @ApiResponse(responseCode = "404", description = "Transfer not found"),
        @ApiResponse(responseCode = "409", description = "Transfer cannot be accepted in current state"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> acceptTransfer(
        @Parameter(description = "Transfer ID", required = true)
        Long id,
        @RequestBody(description = "Acceptance data", required = true)
        AcceptTransferRequest request
    );

    @Operation(
        summary = "Get all transfers",
        description = "List all transfers in the system (admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TransferResponse>> getAllTransfers();
}
