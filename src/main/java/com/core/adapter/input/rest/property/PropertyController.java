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
    
    /**
     * Get all properties owned by the authenticated user
     * Requires authentication via JWT token
     */
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getAuthenticatedUserProperties(HttpServletRequest request) {
        try {
            Boolean authenticated = (Boolean) request.getAttribute("authenticated");
            if (authenticated == null || !authenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Find properties by user ID (proprietario is now a FK to users)
            List<PropertyResponse> properties = propertyUseCase.findByProprietario(userId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(properties);
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
}

