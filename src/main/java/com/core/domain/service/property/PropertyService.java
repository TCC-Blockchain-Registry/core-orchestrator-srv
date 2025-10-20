package com.core.domain.service.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyType;
import com.core.port.input.property.PropertyUseCase;
import com.core.port.output.property.PropertyRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Property Domain Service
 * Implements business logic for property operations
 */
@Service
public class PropertyService implements PropertyUseCase {
    
    private final PropertyRepositoryPort propertyRepositoryPort;
    
    public PropertyService(PropertyRepositoryPort propertyRepositoryPort) {
        this.propertyRepositoryPort = propertyRepositoryPort;
    }
    
    @Override
    public PropertyModel registerProperty(Long matriculaId, Long folha, String comarca,
                                         String endereco, Long metragem, String proprietario,
                                         Long matriculaOrigem, PropertyType tipo, Boolean isRegular) {
        // Validate input
        validatePropertyInput(matriculaId, folha, comarca, endereco, metragem, proprietario, tipo);
        
        // Check if matricula already exists
        if (propertyRepositoryPort.findByMatriculaId(matriculaId).isPresent()) {
            throw new IllegalArgumentException("Property with matricula " + matriculaId + " already exists");
        }
        
        // Create property model
        PropertyModel property = new PropertyModel();
        property.setMatriculaId(matriculaId);
        property.setFolha(folha);
        property.setComarca(comarca);
        property.setEndereco(endereco);
        property.setMetragem(metragem);
        property.setProprietario(proprietario);
        property.setMatriculaOrigem(matriculaOrigem);
        property.setTipo(tipo);
        property.setIsRegular(isRegular != null ? isRegular : true);
        
        return propertyRepositoryPort.save(property);
    }
    
    @Override
    public PropertyModel findById(Long id) {
        return propertyRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + id));
    }
    
    @Override
    public PropertyModel findByMatriculaId(Long matriculaId) {
        return propertyRepositoryPort.findByMatriculaId(matriculaId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with matricula: " + matriculaId));
    }
    
    @Override
    public List<PropertyModel> findAll() {
        return propertyRepositoryPort.findAll();
    }
    
    @Override
    public List<PropertyModel> findByProprietario(String proprietario) {
        if (proprietario == null || proprietario.trim().isEmpty()) {
            throw new IllegalArgumentException("Proprietario cannot be empty");
        }
        return propertyRepositoryPort.findByProprietario(proprietario);
    }
    
    @Override
    public List<PropertyModel> findByComarca(String comarca) {
        if (comarca == null || comarca.trim().isEmpty()) {
            throw new IllegalArgumentException("Comarca cannot be empty");
        }
        return propertyRepositoryPort.findByComarca(comarca);
    }
    
    @Override
    public PropertyModel updateBlockchainTxHash(Long id, String txHash) {
        PropertyModel property = findById(id);
        property.setBlockchainTxHash(txHash);
        return propertyRepositoryPort.save(property);
    }
    
    private void validatePropertyInput(Long matriculaId, Long folha, String comarca,
                                       String endereco, Long metragem, String proprietario,
                                       PropertyType tipo) {
        if (matriculaId == null) {
            throw new IllegalArgumentException("Matricula ID cannot be null");
        }
        
        if (folha == null) {
            throw new IllegalArgumentException("Folha cannot be null");
        }
        
        if (comarca == null || comarca.trim().isEmpty()) {
            throw new IllegalArgumentException("Comarca cannot be empty");
        }
        
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereco cannot be empty");
        }
        
        if (metragem == null || metragem <= 0) {
            throw new IllegalArgumentException("Metragem must be greater than 0");
        }
        
        if (proprietario == null || proprietario.trim().isEmpty()) {
            throw new IllegalArgumentException("Proprietario cannot be empty");
        }
        
        // Validate Ethereum address format (0x + 40 hex chars)
        if (!proprietario.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum address format for proprietario");
        }
        
        if (tipo == null) {
            throw new IllegalArgumentException("Property type cannot be null");
        }
    }
}

