package com.core.adapter.input.rest.approval;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.service.property.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/properties/{propertyId}/approvals")
@Tag(name = "Property Approvals", description = "Endpoints para aprovações V2 do sistema de registro")
public class PropertyApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyApprovalController.class);
    private final PropertyService propertyService;
    private final RestTemplate restTemplate;
    private final String offchainApiUrl;

    public PropertyApprovalController(PropertyService propertyService) {
        this.propertyService = propertyService;
        this.restTemplate = new RestTemplate();
        this.offchainApiUrl = System.getenv().getOrDefault("OFFCHAIN_API_URL", "http://offchain-api:3000");
    }

    @PostMapping("/financial")
    @Operation(summary = "Aprovar como Instituição Financeira")
    public ResponseEntity<Map<String, Object>> approveAsFinancial(@PathVariable Long propertyId) {
        logger.info("📨 Aprovação Financial solicitada para property {}", propertyId);

        try {
            // 1. Buscar propriedade do banco
            PropertyModel property = propertyService.findById(propertyId);
            
            if (property.getRequestHash() == null) {
                logger.error("❌ Property {} não possui requestHash", propertyId);
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Property não possui requestHash. Aguarde o webhook da blockchain."));
            }

            // 2. Fazer requisição para o Offchain API
            String url = String.format("%s/api/approvals/v2/registration/%s/financial", 
                offchainApiUrl, property.getRequestHash());
            
            logger.info("🔗 Chamando Offchain API: {}", url);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Aprovação Financial executada com sucesso para property {}", propertyId);
                return ResponseEntity.ok(createSuccessResponse("Aprovado pela Instituição Financeira", response.getBody()));
            } else {
                logger.error("❌ Falha na aprovação: status={}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode())
                    .body(createErrorResponse("Falha ao aprovar: " + response.getStatusCode()));
            }

        } catch (Exception e) {
            logger.error("❌ Erro ao aprovar property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao processar aprovação: " + e.getMessage()));
        }
    }

    @PostMapping("/registry-office")
    @Operation(summary = "Aprovar como Cartório")
    public ResponseEntity<Map<String, Object>> approveAsRegistryOffice(@PathVariable Long propertyId) {
        logger.info("📨 Aprovação Registry Office solicitada para property {}", propertyId);

        try {
            PropertyModel property = propertyService.findById(propertyId);
            
            if (property.getRequestHash() == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Property não possui requestHash. Aguarde o webhook da blockchain."));
            }

            String url = String.format("%s/api/approvals/v2/registration/%s/registry-office", 
                offchainApiUrl, property.getRequestHash());
            
            logger.info("🔗 Chamando Offchain API: {}", url);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Aprovação Registry Office executada com sucesso para property {}", propertyId);
                return ResponseEntity.ok(createSuccessResponse("Aprovado pelo Cartório", response.getBody()));
            } else {
                return ResponseEntity.status(response.getStatusCode())
                    .body(createErrorResponse("Falha ao aprovar: " + response.getStatusCode()));
            }

        } catch (Exception e) {
            logger.error("❌ Erro ao aprovar property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao processar aprovação: " + e.getMessage()));
        }
    }

    @PostMapping("/municipality")
    @Operation(summary = "Aprovar como Prefeitura (Executa automaticamente)")
    public ResponseEntity<Map<String, Object>> approveAsMunicipality(@PathVariable Long propertyId) {
        logger.info("📨 Aprovação Municipality solicitada para property {}", propertyId);

        try {
            PropertyModel property = propertyService.findById(propertyId);
            
            if (property.getRequestHash() == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Property não possui requestHash. Aguarde o webhook da blockchain."));
            }

            String url = String.format("%s/api/approvals/v2/registration/%s/municipality", 
                offchainApiUrl, property.getRequestHash());
            
            logger.info("🔗 Chamando Offchain API: {}", url);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Aprovação Municipality executada com sucesso para property {}", propertyId);
                logger.info("🎉 Propriedade será executada automaticamente após esta aprovação");
                
                // Atualizar status no banco
                property.setApprovalStatus("EXECUTED");
                propertyService.updateRequestHash(propertyId, property.getRequestHash(), "EXECUTED");
                
                return ResponseEntity.ok(createSuccessResponse(
                    "Aprovado pela Prefeitura. Propriedade executada automaticamente!", 
                    response.getBody()
                ));
            } else {
                return ResponseEntity.status(response.getStatusCode())
                    .body(createErrorResponse("Falha ao aprovar: " + response.getStatusCode()));
            }

        } catch (Exception e) {
            logger.error("❌ Erro ao aprovar property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao processar aprovação: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Verificar status das aprovações")
    public ResponseEntity<Map<String, Object>> getApprovalStatus(@PathVariable Long propertyId) {
        logger.info("📊 Status de aprovação solicitado para property {}", propertyId);

        try {
            PropertyModel property = propertyService.findById(propertyId);
            
            if (property.getRequestHash() == null) {
                return ResponseEntity.ok(createStatusResponse(property, null));
            }

            // Buscar status no blockchain
            String url = String.format("%s/api/approvals/v2/registration/%s/status", 
                offchainApiUrl, property.getRequestHash());
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            return ResponseEntity.ok(createStatusResponse(property, response.getBody()));

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar status property {}: {}", propertyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao buscar status: " + e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }

    private Map<String, Object> createStatusResponse(PropertyModel property, Object blockchainStatus) {
        Map<String, Object> response = new HashMap<>();
        response.put("propertyId", property.getId());
        response.put("matriculaId", property.getMatriculaId());
        response.put("requestHash", property.getRequestHash());
        response.put("approvalStatus", property.getApprovalStatus());
        response.put("blockchainTxHash", property.getBlockchainTxHash());
        
        if (blockchainStatus != null) {
            response.put("blockchainStatus", blockchainStatus);
        }
        
        return response;
    }
}

