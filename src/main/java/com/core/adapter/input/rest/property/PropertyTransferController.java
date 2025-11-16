package com.core.adapter.input.rest.property;

import com.core.adapter.input.rest.property.dto.TransferCompleteRequest;
import com.core.adapter.input.rest.property.dto.TransferInitiateRequest;
import com.core.adapter.input.rest.property.dto.TransferResponse;
import com.core.adapter.input.rest.property.swagger.PropertyTransferSwaggerApi;
import com.core.domain.model.property.PropertyTransferModel;
import com.core.port.input.property.PropertyTransferUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Property Transfer REST Controller
 * Handles property transfer-related HTTP requests
 */
@RestController
@RequestMapping("/api/property-transfers")
@CrossOrigin(origins = "*")
public class PropertyTransferController implements PropertyTransferSwaggerApi {
    
    private final PropertyTransferUseCase transferUseCase;
    
    public PropertyTransferController(PropertyTransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }
    
    @Override
    @PostMapping("/initiate")
    public ResponseEntity<TransferResponse> initiateTransfer(@RequestBody TransferInitiateRequest request) {
        try {
            PropertyTransferModel transfer = transferUseCase.initiateTransfer(
                request.propertyId(),
                request.fromProprietario(),
                request.toProprietario()
            );
            
            TransferResponse response = toResponse(transfer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            // Property has active transfer
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @PostMapping("/{id}/complete")
    public ResponseEntity<TransferResponse> completeTransfer(@PathVariable Long id,
                                                             @RequestBody TransferCompleteRequest request) {
        try {
            PropertyTransferModel transfer = transferUseCase.completeTransfer(
                id,
                request.blockchainTxHash()
            );
            
            return ResponseEntity.ok(toResponse(transfer));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @PostMapping("/{id}/approve")
    public ResponseEntity<TransferResponse> approveTransfer(@PathVariable Long id) {
        try {
            PropertyTransferModel transfer = transferUseCase.approveTransfer(id);
            return ResponseEntity.ok(toResponse(transfer));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping
    public ResponseEntity<List<TransferResponse>> getAllTransfers() {
        try {
            List<TransferResponse> transfers = transferUseCase.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping("/by-property/{propertyId}")
    public ResponseEntity<List<TransferResponse>> getTransfersByPropertyId(@PathVariable Long propertyId) {
        try {
            List<TransferResponse> transfers = transferUseCase.findByPropertyId(propertyId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<TransferResponse> getTransferStatus(@PathVariable Long id) {
        try {
            PropertyTransferModel transfer = transferUseCase.findById(id);
            return ResponseEntity.ok(toResponse(transfer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private TransferResponse toResponse(PropertyTransferModel transfer) {
        return new TransferResponse(
            transfer.getId(),
            transfer.getPropertyId(),
            transfer.getFromProprietario(),
            transfer.getToProprietario(),
            transfer.getStatus(),
            transfer.getBlockchainTxHash(),
            transfer.getCompletedAt(),
            transfer.getCreatedAt(),
            transfer.getUpdatedAt()
        );
    }
}

