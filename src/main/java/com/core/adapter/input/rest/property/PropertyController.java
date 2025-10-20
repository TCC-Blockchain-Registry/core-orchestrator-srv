package com.core.adapter.input.rest.property;

import com.core.adapter.input.rest.property.dto.PropertyRegistrationRequest;
import com.core.adapter.input.rest.property.dto.PropertyResponse;
import com.core.adapter.input.rest.property.swagger.PropertySwaggerApi;
import com.core.domain.model.property.PropertyModel;
import com.core.port.input.property.PropertyUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Property REST Controller
 * Handles property-related HTTP requests
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyController implements PropertySwaggerApi {
    
    private final PropertyUseCase propertyUseCase;
    
    public PropertyController(PropertyUseCase propertyUseCase) {
        this.propertyUseCase = propertyUseCase;
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
            property.getCreatedAt(),
            property.getUpdatedAt()
        );
    }
}

