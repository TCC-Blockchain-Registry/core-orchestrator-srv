package com.core.adapter.input.rest.property.swagger;

import com.core.adapter.input.rest.property.dto.TransferCompleteRequest;
import com.core.adapter.input.rest.property.dto.TransferInitiateRequest;
import com.core.adapter.input.rest.property.dto.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Swagger API interface for Property Transfer endpoints
 */
@Tag(name = "Property Transfer", description = "APIs for property transfer management")
public interface PropertyTransferSwaggerApi {
    
    @Operation(
        summary = "Initiate property transfer",
        description = "Initiates a new property transfer. Property must not have any active transfer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transfer initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Property already has an active transfer"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> initiateTransfer(
        @RequestBody(description = "Transfer initiation data", required = true)
        TransferInitiateRequest request
    );
    
    @Operation(
        summary = "Complete transfer with blockchain",
        description = "Completes a transfer by registering it on blockchain. Updates property owner."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data or transfer not approved"),
        @ApiResponse(responseCode = "404", description = "Transfer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> completeTransfer(
        @Parameter(description = "Transfer ID", required = true)
        Long id,
        @RequestBody(description = "Blockchain transaction data", required = true)
        TransferCompleteRequest request
    );
    
    @Operation(
        summary = "Approve transfer",
        description = "Approves a pending transfer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer approved successfully"),
        @ApiResponse(responseCode = "400", description = "Transfer cannot be approved"),
        @ApiResponse(responseCode = "404", description = "Transfer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TransferResponse> approveTransfer(
        @Parameter(description = "Transfer ID", required = true)
        Long id
    );
    
    @Operation(
        summary = "Get all transfers",
        description = "Retrieves all property transfers"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TransferResponse>> getAllTransfers();
    
    @Operation(
        summary = "Get transfers by property ID",
        description = "Retrieves all transfers for a specific property"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<TransferResponse>> getTransfersByPropertyId(
        @Parameter(description = "Property ID", required = true)
        Long propertyId
    );
}

