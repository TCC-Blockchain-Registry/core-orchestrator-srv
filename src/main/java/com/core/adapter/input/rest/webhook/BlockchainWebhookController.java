package com.core.adapter.input.rest.webhook;

import com.core.adapter.input.rest.webhook.dto.BlockchainUpdateRequest;
import com.core.domain.service.property.PropertyService;
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
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String offchainApiUrl = "http://offchain-api:3000";

    public BlockchainWebhookController(PropertyService propertyService) {
        this.propertyService = propertyService;
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
}

