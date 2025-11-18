package com.core.adapter.input.rest.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 🧪 MOCK Controller - Auto Approvals
 * Simula aprovações automáticas para facilitar testes
 * Apenas para desenvolvimento!
 */
@RestController
@RequestMapping("/api/mock")
public class MockApprovalController {
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String offchainApiUrl = "http://offchain-api:3000"; // Sempre usa a URL interna do Docker
    
    /**
     * Auto-aprova um registro com as 3 entidades
     * POST /api/mock/approve-registration/{requestHash}
     */
    @PostMapping("/approve-registration/{requestHash}")
    public ResponseEntity<?> autoApproveRegistration(@PathVariable String requestHash) {
        try {
            System.out.println("🧪 MOCK: Auto-aprovando registro " + requestHash);
            
            String baseUrl = offchainApiUrl + "/api/approvals/v2/registration/" + requestHash;
            
            // 1. Aprovação Financial
            System.out.println("   1/3 - Aprovando como Financial...");
            HttpResponse<String> response1 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/financial"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response1.statusCode() != 200 && response1.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Financial",
                    "details", response1.body()
                ));
            }
            
            Thread.sleep(1000);
            
            // 2. Aprovação Registry Office
            System.out.println("   2/3 - Aprovando como Registry Office...");
            HttpResponse<String> response2 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/registry-office"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response2.statusCode() != 200 && response2.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Registry Office",
                    "details", response2.body()
                ));
            }
            
            Thread.sleep(1000);
            
            // 3. Aprovação Municipality (auto-executa)
            System.out.println("   3/3 - Aprovando como Municipality (AUTO-EXECUTA)...");
            HttpResponse<String> response3 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/municipality"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response3.statusCode() != 200 && response3.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Municipality",
                    "details", response3.body()
                ));
            }
            
            System.out.println("   ✅ Todas as aprovações concluídas!");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ Registro aprovado pelas 3 entidades e executado automaticamente!",
                "requestHash", requestHash,
                "approvals", Map.of(
                    "financial", "✅ Aprovado",
                    "registryOffice", "✅ Aprovado",
                    "municipality", "✅ Aprovado e Executado"
                ),
                "note", "⚡ Propriedade foi registrada no blockchain!"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao auto-aprovar: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Erro ao processar aprovações automáticas",
                "details", e.getMessage()
            ));
        }
    }
    
    /**
     * Auto-aprova uma transferência com as 3 entidades
     * POST /api/mock/approve-transfer/{requestHash}
     */
    @PostMapping("/approve-transfer/{requestHash}")
    public ResponseEntity<?> autoApproveTransfer(@PathVariable String requestHash) {
        try {
            System.out.println("🧪 MOCK: Auto-aprovando transferência " + requestHash);
            
            String baseUrl = offchainApiUrl + "/api/approvals/v2/transfer/" + requestHash;
            
            // 1. Aprovação Financial
            System.out.println("   1/3 - Aprovando como Financial...");
            HttpResponse<String> response1 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/financial"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response1.statusCode() != 200 && response1.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Financial",
                    "details", response1.body()
                ));
            }
            
            Thread.sleep(1000);
            
            // 2. Aprovação Registry Office
            System.out.println("   2/3 - Aprovando como Registry Office...");
            HttpResponse<String> response2 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/registry-office"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response2.statusCode() != 200 && response2.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Registry Office",
                    "details", response2.body()
                ));
            }
            
            Thread.sleep(1000);
            
            // 3. Aprovação Municipality (auto-executa)
            System.out.println("   3/3 - Aprovando como Municipality (AUTO-EXECUTA)...");
            HttpResponse<String> response3 = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/municipality"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response3.statusCode() != 200 && response3.statusCode() != 201) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "Falha na aprovação Municipality",
                    "details", response3.body()
                ));
            }
            
            System.out.println("   ✅ Todas as aprovações concluídas!");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ Transferência aprovada pelas 3 entidades e executada automaticamente!",
                "requestHash", requestHash,
                "approvals", Map.of(
                    "financial", "✅ Aprovado",
                    "registryOffice", "✅ Aprovado",
                    "municipality", "✅ Aprovado e Executado"
                ),
                "note", "⚡ Propriedade foi transferida no blockchain!"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao auto-aprovar transferência: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Erro ao processar aprovações automáticas de transferência",
                "details", e.getMessage()
            ));
        }
    }
    
    /**
     * Healthcheck do mock service
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "mockService", "active",
            "offchainApiUrl", offchainApiUrl,
            "endpoints", Map.of(
                "approveRegistration", "POST /api/mock/approve-registration/{requestHash}",
                "approveTransfer", "POST /api/mock/approve-transfer/{requestHash}"
            )
        ));
    }
}

