package com.core.adapter.input.rest.transfer;

import com.core.adapter.input.rest.transfer.dto.AcceptTransferRequest;
import com.core.adapter.input.rest.transfer.dto.ApproveTransferRequest;
import com.core.adapter.input.rest.transfer.dto.InitiateTransferRequest;
import com.core.adapter.input.rest.transfer.dto.TransferResponse;
import com.core.adapter.input.rest.transfer.mapper.TransferResponseMapper;
import com.core.adapter.input.rest.transfer.swagger.TransferSwaggerApi;
import com.core.domain.model.transfer.TransferModel;
import com.core.port.input.transfer.TransferUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Property Transfer operations
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferController implements TransferSwaggerApi {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);
    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @Override
    @PostMapping("/initiate")
    public ResponseEntity<TransferResponse> initiateTransfer(@RequestBody InitiateTransferRequest request) {
        logger.info("POST /api/transfers/initiate - matriculaId={}, buyerId={}",
            request.matriculaId(), request.buyerId());

        try {
            TransferModel transfer = transferUseCase.initiateTransfer(
                request.matriculaId(),
                request.buyerId()
            );

            logger.info("Transfer initiated successfully: id={}", transfer.getId());

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransferResponseMapper.toResponse(transfer));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            logger.error("Invalid state: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Failed to initiate transfer: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getTransferById(@PathVariable Long id) {
        logger.info("GET /api/transfers/{}", id);

        try {
            TransferModel transfer = transferUseCase.findById(id);
            return ResponseEntity.ok(TransferResponseMapper.toResponse(transfer));

        } catch (IllegalArgumentException e) {
            logger.error("Transfer not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get transfer: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @GetMapping("/property/{matriculaId}")
    public ResponseEntity<List<TransferResponse>> getTransfersByProperty(@PathVariable Long matriculaId) {
        logger.info("GET /api/transfers/property/{}", matriculaId);

        try {
            List<TransferModel> transfers = transferUseCase.findByMatriculaId(matriculaId);
            List<TransferResponse> response = transfers.stream()
                .map(TransferResponseMapper::toResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get transfers for property {}: {}", matriculaId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @PostMapping("/{id}/approve")
    public ResponseEntity<TransferResponse> approveTransfer(
            @PathVariable Long id,
            @RequestBody ApproveTransferRequest request) {

        logger.info("POST /api/transfers/{}/approve - approverId={}", id, request.approverId());

        try {
            TransferModel transfer = transferUseCase.approveTransfer(id, request.approverId());

            logger.info("Transfer {} approved by approver {}", id, request.approverId());

            return ResponseEntity.ok(TransferResponseMapper.toResponse(transfer));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            logger.error("Invalid state: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Failed to approve transfer: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @PostMapping("/{id}/accept")
    public ResponseEntity<TransferResponse> acceptTransfer(
            @PathVariable Long id,
            @RequestBody AcceptTransferRequest request) {

        logger.info("POST /api/transfers/{}/accept - buyerId={}", id, request.buyerId());

        try {
            TransferModel transfer = transferUseCase.acceptTransfer(id, request.buyerId());

            logger.info("Transfer {} accepted by buyer {}", id, request.buyerId());

            return ResponseEntity.ok(TransferResponseMapper.toResponse(transfer));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            logger.error("Invalid state: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Failed to accept transfer: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @GetMapping
    public ResponseEntity<List<TransferResponse>> getAllTransfers() {
        logger.info("GET /api/transfers");

        try {
            List<TransferModel> transfers = transferUseCase.findAll();
            List<TransferResponse> response = transfers.stream()
                .map(TransferResponseMapper::toResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get all transfers: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
