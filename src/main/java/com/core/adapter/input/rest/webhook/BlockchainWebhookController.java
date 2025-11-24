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
    private static final String EXPECTED_API_KEY = "development-webhook-key-12345";

    private final PropertyService propertyService;
    private final TransferService transferService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String offchainApiUrl = "http://offchain-api:3000";

    public BlockchainWebhookController(PropertyService propertyService, TransferService transferService) {
        this.propertyService = propertyService;
        this.transferService = transferService;
    }

    /**
     * Validate webhook API key from X-Api-Key header
     */
    private boolean isValidApiKey(String apiKey) {
        return EXPECTED_API_KEY.equals(apiKey);
    }

    @PatchMapping("/properties/{id}")
    @Operation(summary = "Atualizar hash de transação blockchain de uma propriedade")
    public ResponseEntity<Void> updatePropertyBlockchainTx(
            @PathVariable Long id,
            @RequestBody BlockchainUpdateRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Update property {} with txHash {}, requestHash {}, status {}",
            id, request.getTransactionHash(), request.getRequestHash(), request.getApprovalStatus());

        // Validate API Key
        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

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

                        String baseUrl = offchainApiUrl + "/api/properties/approvals/" + requestHash;
                        
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

    @PostMapping("/properties/registration-requested")
    @Operation(summary = "Notificar solicitação de registro de propriedade iniciada")
    public ResponseEntity<Void> handleRegistrationRequested(
            @RequestBody RegistrationRequestedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Registration requested for property {}, requestHash {}",
            request.getMatriculaId(), request.getRequestHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            // Update property with requestHash (using matriculaId from webhook)
            propertyService.updateRequestHashByMatricula(request.getMatriculaId(), request.getRequestHash(), "PENDING_APPROVALS");

            logger.info("✅ Property {} registration request recorded with hash {}",
                request.getMatriculaId(), request.getRequestHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record registration request for property {}: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/registration-executed")
    @Operation(summary = "Notificar execução de registro de propriedade concluída")
    public ResponseEntity<Void> handleRegistrationExecuted(
            @RequestBody RegistrationExecutedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Registration executed for property {}, requestHash {}",
            request.getMatriculaId(), request.getRequestHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            // Update property status to EXECUTED (using matriculaId from webhook)
            propertyService.updateRequestHashByMatricula(request.getMatriculaId(), request.getRequestHash(), "EXECUTED");

            logger.info("✅ Property {} registration executed successfully",
                request.getMatriculaId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record registration execution for property {}: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/transferred")
    @Operation(summary = "Notificar transferência de propriedade concluída na blockchain")
    public ResponseEntity<Void> handlePropertyTransferred(
            @RequestBody PropertyTransferredRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Property {} transferred from {} to {}",
            request.getMatriculaId(), request.getFrom(), request.getTo());

        // Validate API Key
        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

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
            @RequestBody TransferConfiguredRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Transfer configured for property {} from {} to {}",
            request.getMatriculaId(), request.getSeller(), request.getBuyer());

        // Validate API Key
        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            // Update transfer record with requestHash and txHash
            transferService.updateRequestHashByMatriculaId(
                request.getMatriculaId(),
                request.getTransferHash(),
                request.getTransactionHash()
            );

            // Set property status to EM_TRANSFERENCIA
            propertyService.initiateTransfer(request.getMatriculaId());

            logger.info("✅ Transfer configured for property {}: requestHash={}, txHash={}",
                request.getMatriculaId(), request.getTransferHash(), request.getTransactionHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update transfer for property {}: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transfers/transfer-requested")
    @Operation(summary = "Notificar solicitação de transferência iniciada")
    public ResponseEntity<Void> handleTransferRequested(
            @RequestBody TransferRequestedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Transfer requested for property {} from {} to {}, transferHash {}",
            request.getMatriculaId(), request.getFrom(), request.getTo(), request.getTransferHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            // Update transfer record with requestHash and txHash
            transferService.updateRequestHashByMatriculaId(
                request.getMatriculaId(),
                request.getTransferHash(),
                request.getTransactionHash()
            );

            logger.info("✅ Transfer request recorded for property {}: requestHash={}, txHash={}",
                request.getMatriculaId(), request.getTransferHash(), request.getTransactionHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record transfer request for property {}: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transfers/transfer-executed")
    @Operation(summary = "Notificar execução de transferência concluída")
    public ResponseEntity<Void> handleTransferExecuted(
            @RequestBody TransferExecutedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Transfer executed for property {} from {} to {}, transferHash {}",
            request.getMatriculaId(), request.getFrom(), request.getTo(), request.getTransferHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            // Update property owner and status
            propertyService.updateOwner(request.getMatriculaId(), request.getTo());

            logger.info("✅ Transfer executed successfully for property {}, new owner: {}",
                request.getMatriculaId(), request.getTo());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record transfer execution for property {}: {}",
                request.getMatriculaId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/transfers/{id}")
    @Operation(summary = "Atualizar hash de transação blockchain de uma transferência")
    public ResponseEntity<Void> updateTransferBlockchainTx(
            @PathVariable Long id,
            @RequestBody BlockchainUpdateRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Update transfer {} with txHash {}",
            id, request.getTransactionHash());

        // Validate API Key
        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

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

    @PostMapping("/properties/issued")
    @Operation(summary = "Notificar que tokens foram emitidos para uma propriedade")
    public ResponseEntity<Void> handlePropertyIssued(
            @RequestBody PropertyIssuedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Property issued {} to owner {}",
            request.getMatricula(), request.getOwner());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            propertyService.markAsIssued(Long.parseLong(request.getMatricula()), request.getOwner());
            logger.info("✅ Property {} marked as issued", request.getMatricula());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to mark property {} as issued: {}", request.getMatricula(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/frozen")
    @Operation(summary = "Notificar alteração de status de congelamento de propriedade")
    public ResponseEntity<Void> handlePropertyFrozen(
            @RequestBody PropertyFrozenRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Property {} frozen status: {}",
            request.getMatricula(), request.getFrozen());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            propertyService.updateFrozenStatus(Long.parseLong(request.getMatricula()), request.getFrozen());
            logger.info("✅ Property {} frozen status updated", request.getMatricula());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update frozen status for property {}: {}",
                request.getMatricula(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/registration-approved")
    @Operation(summary = "Notificar aprovação de registro de propriedade")
    public ResponseEntity<Void> handleRegistrationApproved(
            @RequestBody RegistrationApprovedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Registration approved by {} - {}",
            request.getInstitution(), request.getApprover());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            propertyService.recordRegistrationApproval(
                request.getRequestHash(),
                request.getInstitution(),
                request.getApprover()
            );
            logger.info("✅ Registration approval recorded for hash {}", request.getRequestHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record registration approval: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transfers/transfer-approved")
    @Operation(summary = "Notificar aprovação de transferência")
    public ResponseEntity<Void> handleTransferApproved(
            @RequestBody TransferApprovedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Transfer approved by {} - {}",
            request.getInstitution(), request.getApprover());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            transferService.recordTransferApproval(
                request.getRequestHash(),
                request.getInstitution(),
                request.getApprover()
            );
            logger.info("✅ Transfer approval recorded for hash {}", request.getRequestHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record transfer approval: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/registered")
    @Operation(summary = "Notificar registro de propriedade no módulo de compliance")
    public ResponseEntity<Void> handlePropertyRegistered(
            @RequestBody PropertyRegisteredRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Property registered {} by {}",
            request.getMatricula(), request.getProprietario());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            propertyService.updateRegistryData(Long.parseLong(request.getMatricula()), request);
            logger.info("✅ Property {} registry data updated", request.getMatricula());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update registry data for property {}: {}",
                request.getMatricula(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/properties/updated")
    @Operation(summary = "Notificar atualização de propriedade")
    public ResponseEntity<Void> handlePropertyUpdated(
            @RequestBody PropertyUpdatedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Property {} updated", request.getMatricula());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            propertyService.updateRegularStatus(
                Long.parseLong(request.getMatricula()),
                request.getIsRegular()
            );
            logger.info("✅ Property {} regularity updated", request.getMatricula());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to update property {}: {}",
                request.getMatricula(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/approvals/approved")
    @Operation(summary = "Notificar aprovação registrada no módulo de aprovações")
    public ResponseEntity<Void> handleApprovalRecorded(
            @RequestBody ApprovalRecordedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Approval recorded for transfer {}",
            request.getTransferHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            transferService.recordApprovalProgress(request.getTransferHash(), request);
            logger.info("✅ Approval progress recorded for hash {}", request.getTransferHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record approval progress: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/approvals/buyer-accepted")
    @Operation(summary = "Notificar aceitação do comprador")
    public ResponseEntity<Void> handleBuyerAccepted(
            @RequestBody BuyerAcceptedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Buyer {} accepted transfer {}",
            request.getBuyer(), request.getTransferHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            transferService.recordBuyerAcceptance(request.getTransferHash(), request.getBuyer());
            logger.info("✅ Buyer acceptance recorded for hash {}", request.getTransferHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to record buyer acceptance: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/approvals/config-cleared")
    @Operation(summary = "Notificar limpeza de configuração de transferência")
    public ResponseEntity<Void> handleTransferConfigCleared(
            @RequestBody TransferConfigClearedRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey
    ) {
        logger.info("📨 Webhook received: Transfer config cleared for {}",
            request.getTransferHash());

        if (!isValidApiKey(apiKey)) {
            logger.warn("⛔ Unauthorized webhook call: Invalid or missing API key");
            return ResponseEntity.status(401).build();
        }

        try {
            transferService.clearTransferConfig(request.getTransferHash());
            logger.info("✅ Transfer config cleared for hash {}", request.getTransferHash());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("❌ Failed to clear transfer config: {}", e.getMessage());
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
        private String transferHash;  // Request hash from blockchain (approval request)
        private String seller;
        private String buyer;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getSeller() { return seller; }
        public void setSeller(String seller) { this.seller = seller; }

        public String getBuyer() { return buyer; }
        public void setBuyer(String buyer) { this.buyer = buyer; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class RegistrationRequestedRequest {
        private Long matriculaId;
        private String requestHash;
        private String beneficiary;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getRequestHash() { return requestHash; }
        public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

        public String getBeneficiary() { return beneficiary; }
        public void setBeneficiary(String beneficiary) { this.beneficiary = beneficiary; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class RegistrationExecutedRequest {
        private Long matriculaId;
        private String requestHash;
        private String beneficiary;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getRequestHash() { return requestHash; }
        public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

        public String getBeneficiary() { return beneficiary; }
        public void setBeneficiary(String beneficiary) { this.beneficiary = beneficiary; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class TransferRequestedRequest {
        private Long matriculaId;
        private String transferHash;
        private String from;
        private String to;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class TransferExecutedRequest {
        private Long matriculaId;
        private String transferHash;
        private String from;
        private String to;
        private String transactionHash;

        public Long getMatriculaId() { return matriculaId; }
        public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class PropertyIssuedRequest {
        private String matricula;
        private String owner;
        private String transactionHash;

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class PropertyFrozenRequest {
        private String matricula;
        private Boolean frozen;
        private String transactionHash;

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public Boolean getFrozen() { return frozen; }
        public void setFrozen(Boolean frozen) { this.frozen = frozen; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class RegistrationApprovedRequest {
        private String requestHash;
        private String institution;
        private String approver;
        private String transactionHash;

        public String getRequestHash() { return requestHash; }
        public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }

        public String getApprover() { return approver; }
        public void setApprover(String approver) { this.approver = approver; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class TransferApprovedRequest {
        private String requestHash;
        private String institution;
        private String approver;
        private String transactionHash;

        public String getRequestHash() { return requestHash; }
        public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }

        public String getApprover() { return approver; }
        public void setApprover(String approver) { this.approver = approver; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class PropertyRegisteredRequest {
        private String matricula;
        private String proprietario;
        private String folha;
        private String comarca;
        private String endereco;
        private String metragem;
        private String tipo;
        private Boolean isRegular;
        private String transactionHash;

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public String getProprietario() { return proprietario; }
        public void setProprietario(String proprietario) { this.proprietario = proprietario; }

        public String getFolha() { return folha; }
        public void setFolha(String folha) { this.folha = folha; }

        public String getComarca() { return comarca; }
        public void setComarca(String comarca) { this.comarca = comarca; }

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }

        public String getMetragem() { return metragem; }
        public void setMetragem(String metragem) { this.metragem = metragem; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public Boolean getIsRegular() { return isRegular; }
        public void setIsRegular(Boolean isRegular) { this.isRegular = isRegular; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class PropertyUpdatedRequest {
        private String matricula;
        private Boolean isRegular;
        private String transactionHash;

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public Boolean getIsRegular() { return isRegular; }
        public void setIsRegular(Boolean isRegular) { this.isRegular = isRegular; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class ApprovalRecordedRequest {
        private String transferHash;
        private String transactionHash;

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class BuyerAcceptedRequest {
        private String transferHash;
        private String buyer;
        private String transactionHash;

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getBuyer() { return buyer; }
        public void setBuyer(String buyer) { this.buyer = buyer; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }

    public static class TransferConfigClearedRequest {
        private String transferHash;
        private String transactionHash;

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }
}

