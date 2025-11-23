package com.core.adapter.input.rest.webhook;

import com.core.adapter.input.rest.webhook.dto.BlockchainUpdateRequest;
import com.core.domain.service.property.PropertyService;
import com.core.domain.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/webhooks/blockchain")
@Tag(name = "Blockchain Webhooks", description = "Endpoints para receber atualizações da blockchain")
public class BlockchainWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainWebhookController.class);
    private final PropertyService propertyService;
    private final TransferService transferService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String offchainApiUrl = "http://offchain-api:3000";

    public BlockchainWebhookController(PropertyService propertyService, TransferService transferService) {
        this.propertyService = propertyService;
        this.transferService = transferService;
    }

    @PatchMapping("/properties/{id}")
    @Operation(summary = "Atualizar hash de transação blockchain de uma propriedade")
    public ResponseEntity<Void> updatePropertyBlockchainTx(
            @PathVariable Long id,
            @RequestBody BlockchainUpdateRequest request
    ) {
        logger.info("📨 Webhook received: Update property {} with txHash {}, requestHash {}, status {}", 
            id, request.getTransactionHash(), request.getRequestHash(), request.getApprovalStatus());

        try {
            // Atualizar txHash (sempre)
            propertyService.updateBlockchainTxHash(id, request.getTransactionHash());
            
            // Atualizar requestHash e approvalStatus se fornecidos (Sistema V2)
            if (request.getRequestHash() != null) {
                propertyService.updateRequestHash(id, request.getRequestHash(), request.getApprovalStatus());
                logger.info("✅ Property {} updated with requestHash for V2 approval system", id);
                
                // 🎯 AUTO-APPROVE: Iniciar aprovações automáticas em background
                String requestHash = request.getRequestHash();
                logger.info("🤖 AUTO-APPROVE: Iniciando aprovações automáticas para requestHash {}", requestHash);
                
                new Thread(() -> {
                    try {
                        // Aguardar 2 segundos para garantir que o registro foi salvo
                        Thread.sleep(2000);
                        
                        String baseUrl = offchainApiUrl + "/api/approvals/v2/registration/" + requestHash;
                        
                        // 1. Aprovação Financial
                        logger.info("  🏦 1/3 - Aprovando como Financial...");
                        HttpResponse<String> resp1 = httpClient.send(
                            HttpRequest.newBuilder()
                                .uri(URI.create(baseUrl + "/financial"))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build(),
                            HttpResponse.BodyHandlers.ofString()
                        );
                        logger.info("  ✅ Financial: {}", resp1.statusCode());
                        
                        Thread.sleep(1000);
                        
                        // 2. Aprovação Registry Office
                        logger.info("  🏛️ 2/3 - Aprovando como Registry Office...");
                        HttpResponse<String> resp2 = httpClient.send(
                            HttpRequest.newBuilder()
                                .uri(URI.create(baseUrl + "/registry-office"))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build(),
                            HttpResponse.BodyHandlers.ofString()
                        );
                        logger.info("  ✅ Registry Office: {}", resp2.statusCode());
                        
                        Thread.sleep(1000);
                        
                        // 3. Aprovação Municipality (auto-executa)
                        logger.info("  🏙️ 3/3 - Aprovando como Municipality (AUTO-EXECUTA)...");
                        HttpResponse<String> resp3 = httpClient.send(
                            HttpRequest.newBuilder()
                                .uri(URI.create(baseUrl + "/municipality"))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build(),
                            HttpResponse.BodyHandlers.ofString()
                        );
                        logger.info("  ✅ Municipality: {}", resp3.statusCode());
                        
                        logger.info("🎉 AUTO-APPROVE: Todas as aprovações concluídas para property {}", id);
                        
                    } catch (Exception e) {
                        logger.error("❌ AUTO-APPROVE: Erro ao processar aprovações automáticas: {}", e.getMessage());
                    }
                }).start();
            }
            
            logger.info("✅ Property {} updated successfully", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update property {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/transferred")
    @Operation(summary = "Notificar transferência de propriedade concluída na blockchain")
    public ResponseEntity<Void> handlePropertyTransferred(
            @RequestBody PropertyTransferredRequest request
    ) {
        logger.info("📨 Webhook received: Property {} transferred from {} to {}",
            request.getMatriculaId(), request.getFrom(), request.getTo());

        try {
            // Update owner and set status to OK
            propertyService.updateOwner(request.getMatriculaId(), request.getTo());

            logger.info("✅ Property {} transferred successfully to {}",
                request.getMatriculaId(), request.getTo());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update property {} transfer: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/transfer-configured")
    @Operation(summary = "Notificar que transferência foi configurada na blockchain")
    public ResponseEntity<Void> handleTransferConfigured(
            @RequestBody TransferConfiguredRequest request
    ) {
        logger.info("📨 Webhook received: Transfer configured for property {} from {} to {}",
            request.getMatriculaId(), request.getSeller(), request.getBuyer());

        try {
            // Set status to EM_TRANSFERENCIA
            propertyService.initiateTransfer(request.getMatriculaId());

            logger.info("✅ Property {} status updated to EM_TRANSFERENCIA", request.getMatriculaId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update property {} status: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/transfers/{id}")
    @Operation(summary = "Atualizar hash de transação blockchain de uma transferência")
    public ResponseEntity<Void> updateTransferBlockchainTx(
            @PathVariable Long id,
            @RequestBody BlockchainUpdateRequest request
    ) {
        logger.info("📨 Webhook received: Update transfer {} with txHash {}",
            id, request.getTransactionHash());

        try {
            // Update transfer with blockchain transaction hash
            transferService.updateBlockchainTxHash(id, request.getTransactionHash());

            logger.info("✅ Transfer {} updated successfully", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update transfer {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO classes for webhook requests

    public static class PropertyTransferredRequest {
        private Long matriculaId;
        private String from;
        private String to;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class TransferConfiguredRequest {
        private Long matriculaId;
        private String seller;
        private String buyer;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getSeller() { return seller; }
        public void setSeller(String seller) { this.seller = seller; }

        public String getBuyer() { return buyer; }
        public void setBuyer(String buyer) { this.buyer = buyer; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }
}

