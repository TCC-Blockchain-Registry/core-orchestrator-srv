package com.core.adapter.input.rest.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Webhook Controller
 * Receives blockchain event notifications
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/blockchain/property-issued")
    public ResponseEntity<Map<String, String>> onPropertyIssued(
            @RequestBody WebhookEvent event) {

        logger.info("Received PROPERTY_ISSUED webhook: matriculaId={}, txHash={}",
                event.getMatriculaId(), event.getTxHash());

        // NOTE: Blockchain sync deferred - requires Besu network running
        // TODO: Update database with blockchain confirmation
        // propertyService.updateBlockchainStatus(event.getMatriculaId(), event.getTxHash(), "confirmed");

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Property issued event processed"
        ));
    }

    @PostMapping("/blockchain/property-transferred")
    public ResponseEntity<Map<String, String>> onPropertyTransferred(
            @RequestBody WebhookEvent event) {

        logger.info("Received PROPERTY_TRANSFERRED webhook: matriculaId={}, from={}, to={}, txHash={}",
                event.getMatriculaId(), event.getFrom(), event.getTo(), event.getTxHash());

        // NOTE: Blockchain sync deferred - requires Besu network running
        // TODO: Update database with new owner
        // transferService.completeTransfer(event.getTransferId(), event.getTxHash());

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Property transferred event processed"
        ));
    }

    @PostMapping("/blockchain/transfer-configured")
    public ResponseEntity<Map<String, String>> onTransferConfigured(
            @RequestBody WebhookEvent event) {

        logger.info("Received TRANSFER_CONFIGURED webhook: transferId={}, matriculaId={}",
                event.getTransferId(), event.getMatriculaId());

        // NOTE: Blockchain sync deferred - requires Besu network running
        // TODO: Update transfer status
        // transferService.updateStatus(event.getTransferId(), "configured");

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Transfer configured event processed"
        ));
    }

    @PostMapping("/blockchain/transfer-approved")
    public ResponseEntity<Map<String, String>> onTransferApproved(
            @RequestBody WebhookEvent event) {

        logger.info("Received TRANSFER_APPROVED webhook: transferId={}, approver={}",
                event.getTransferId(), event.getApprover());

        // NOTE: Blockchain sync deferred - requires Besu network running
        // TODO: Update approval status
        // transferService.recordApproval(event.getTransferId(), event.getApprover());

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Transfer approval recorded"
        ));
    }

    @PostMapping("/blockchain/buyer-accepted")
    public ResponseEntity<Map<String, String>> onBuyerAccepted(
            @RequestBody WebhookEvent event) {

        logger.info("Received BUYER_ACCEPTED webhook: transferId={}, buyer={}",
                event.getTransferId(), event.getBuyer());

        // NOTE: Blockchain sync deferred - requires Besu network running
        // TODO: Update buyer acceptance
        // transferService.recordBuyerAcceptance(event.getTransferId());

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Buyer acceptance recorded"
        ));
    }

    @PostMapping("/blockchain/event")
    public ResponseEntity<Map<String, String>> onGenericEvent(
            @RequestBody Map<String, Object> payload) {

        logger.info("Received generic blockchain event: {}", payload);

        return ResponseEntity.ok(Map.of(
                "status", "received",
                "message", "Generic event processed"
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "webhook-receiver"
        ));
    }
}
