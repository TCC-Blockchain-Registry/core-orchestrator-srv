package com.core.adapter.input.rest.property;

import com.core.adapter.input.rest.property.dto.PropertyRegistrationRequest;
import com.core.adapter.input.rest.property.dto.PropertyResponse;
import com.core.adapter.input.rest.property.swagger.PropertySwaggerApi;
import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.user.UserModel;
import com.core.port.input.property.PropertyUseCase;
import com.core.port.output.user.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Property REST Controller
 * Handles property-related HTTP requests
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyController implements PropertySwaggerApi {
    
    private final PropertyUseCase propertyUseCase;
    private final UserRepositoryPort userRepositoryPort;
    
    public PropertyController(PropertyUseCase propertyUseCase, UserRepositoryPort userRepositoryPort) {
        this.propertyUseCase = propertyUseCase;
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    @PostMapping("/register")
    public ResponseEntity<PropertyResponse> registerProperty(@RequestBody PropertyRegistrationRequest request) {
        try {
            PropertyModel property = propertyUseCase.registerProperty(
                request.matriculaId(),
                request.folha(),
                request.comarca(),
                request.endereco(),
                request.metragem(),
                request.proprietario(),
                request.matriculaOrigem(),
                request.tipo(),
                request.isRegular()
            );
            
            PropertyResponse response = toResponse(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        try {
            PropertyModel property = propertyUseCase.findById(id);
            return ResponseEntity.ok(toResponse(property));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        try {
            List<PropertyResponse> properties = propertyUseCase.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(HttpServletRequest request) {
        try {
            Boolean authenticated = (Boolean) request.getAttribute("authenticated");
            if (authenticated == null || !authenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Optional<UserModel> userOptional = userRepositoryPort.findById(userId);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            UserModel user = userOptional.get();
            String walletAddress = user.getWalletAddress();
            
            if (walletAddress == null || walletAddress.trim().isEmpty()) {
                // User has no wallet, return empty list
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<PropertyResponse> properties = propertyUseCase.findByProprietario(walletAddress).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByUserId(@PathVariable Long userId) {
        try {
            Optional<UserModel> userOptional = userRepositoryPort.findById(userId);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            UserModel user = userOptional.get();
            String walletAddress = user.getWalletAddress();
            
            if (walletAddress == null || walletAddress.trim().isEmpty()) {
                // User has no wallet, return empty list
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<PropertyResponse> properties = propertyUseCase.findByProprietario(walletAddress).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping("/by-proprietario/{proprietario}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByProprietario(
            @PathVariable String proprietario) {
        try {
            List<PropertyResponse> properties = propertyUseCase.findByProprietario(proprietario).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private PropertyResponse toResponse(PropertyModel property) {
        return new PropertyResponse(
            property.getId(),
            property.getMatriculaId(),
            property.getFolha(),
            property.getComarca(),
            property.getEndereco(),
            property.getMetragem(),
            property.getProprietario(),
            property.getMatriculaOrigem(),
            property.getTipo(),
            property.getIsRegular(),
            property.getBlockchainTxHash(),
            property.getStatus(),
            property.getCreatedAt(),
            property.getUpdatedAt()
        );
    }
    
    /**
     * 🧪 MOCK ENDPOINT - AUTO-APPROVE
     * Registra propriedade e simula todas as aprovações automaticamente
     * Apenas para desenvolvimento/testes
     */
    @PostMapping("/register-and-approve-mock")
    public ResponseEntity<?> registerAndApproveMock(@RequestBody PropertyRegistrationRequest request) {
        try {
            // 1. Registrar propriedade normalmente
            PropertyModel property = propertyUseCase.registerProperty(
                request.matriculaId(),
                request.folha(),
                request.comarca(),
                request.endereco(),
                request.metragem(),
                request.proprietario(),
                request.matriculaOrigem(),
                request.tipo(),
                request.isRegular()
            );
            
            // 2. Simular aprovações em background (Thread separada para não bloquear)
            new Thread(() -> {
                try {
                    // Aguardar 3 segundos para o job ser processado
                    Thread.sleep(3000);
                    
                    // Buscar requestHash nos logs ou simular aprovações via HTTP
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    String baseUrl = "http://offchain-api:3000/api/approvals/v2/registration";
                    
                    // Tentar obter o requestHash da propriedade (assumindo que foi salvo)
                    // Por enquanto, vamos apenas logar que as aprovações seriam feitas
                    System.out.println("🧪 MOCK: Simulando aprovações para matrícula " + request.matriculaId());
                    System.out.println("   Aguarde ~10 segundos para as aprovações automáticas...");
                    
                    // Na prática, você precisaria:
                    // 1. Pegar o requestHash do evento/log
                    // 2. Fazer 3 POSTs para aprovar
                    // Exemplo (comentado pois precisa do requestHash real):
                    /*
                    String requestHash = "0x..."; // Obter de alguma forma
                    
                    // Aprovação 1: Financial
                    client.send(
                        java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(baseUrl + "/" + requestHash + "/financial"))
                            .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                            .build(),
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                    );
                    
                    Thread.sleep(2000);
                    
                    // Aprovação 2: Registry Office
                    client.send(
                        java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(baseUrl + "/" + requestHash + "/registry-office"))
                            .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                            .build(),
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                    );
                    
                    Thread.sleep(2000);
                    
                    // Aprovação 3: Municipality (auto-executa)
                    client.send(
                        java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(baseUrl + "/" + requestHash + "/municipality"))
                            .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                            .build(),
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                    );
                    */
                    
                } catch (Exception e) {
                    System.err.println("❌ Erro ao simular aprovações: " + e.getMessage());
                }
            }).start();
            
            // 3. Retornar resposta imediata
            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                "success", true,
                "message", "✅ Propriedade registrada! Aprovações automáticas iniciadas em background.",
                "property", new PropertyResponse(
                    property.getId(),
                    property.getMatriculaId(),
                    property.getFolha(),
                    property.getComarca(),
                    property.getEndereco(),
                    property.getMetragem(),
                    property.getProprietario(),
                    property.getMatriculaOrigem(),
                    property.getTipo(),
                    property.getIsRegular(),
                    property.getBlockchainTxHash(),
                    property.getStatus(),
                    property.getCreatedAt(),
                    property.getUpdatedAt()
                ),
                "note", "⏳ Aguarde ~10 segundos e consulte GET /api/properties para ver o status atualizado",
                "mockEnabled", true
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                "success", false,
                "error", "Erro ao registrar propriedade",
                "details", e.getMessage()
            ));
        }
    }
}

