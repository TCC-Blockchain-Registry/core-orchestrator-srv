package com.core.adapter.input.rest.webhook;

import com.core.adapter.input.rest.webhook.dto.BlockchainUpdateRequest;
import com.core.domain.service.property.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/blockchain")
@Tag(name = "Blockchain Webhooks", description = "Endpoints para receber atualizações da blockchain")
public class BlockchainWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainWebhookController.class);
    private final PropertyService propertyService;

    public BlockchainWebhookController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PatchMapping("/properties/{id}")
    @Operation(summary = "Atualizar hash de transação blockchain de uma propriedade")
    public ResponseEntity<Void> updatePropertyBlockchainTx(
            @PathVariable Long id,
            @RequestBody BlockchainUpdateRequest request
    ) {
        logger.info("📨 Webhook received: Update property {} with txHash {}", id, request.getTransactionHash());

        try {
            propertyService.updateBlockchainTxHash(id, request.getTransactionHash());
            logger.info("✅ Property {} updated successfully", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update property {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

