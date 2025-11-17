package com.core.domain.service.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyType;
import com.core.domain.service.blockchain.BlockchainJobPublisher;
import com.core.port.input.property.PropertyUseCase;
import com.core.port.output.property.PropertyRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Property Domain Service
 * Implements business logic for property operations
 */
@Service
public class PropertyService implements PropertyUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    
    private final PropertyRepositoryPort propertyRepositoryPort;
    private final BlockchainJobPublisher blockchainJobPublisher;
    
    public PropertyService(PropertyRepositoryPort propertyRepositoryPort,
                          BlockchainJobPublisher blockchainJobPublisher) {
        this.propertyRepositoryPort = propertyRepositoryPort;
        this.blockchainJobPublisher = blockchainJobPublisher;
    }

    @Override
    public PropertyModel updateBlockchainTxHash(Long propertyId, String txHash) {
        logger.info("Updating blockchain txHash for property {}: {}", propertyId, txHash);
        
        PropertyModel property = propertyRepositoryPort.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + propertyId));
        
        property.setBlockchainTxHash(txHash);
        PropertyModel updatedProperty = propertyRepositoryPort.save(property);
        
        logger.info("✅ Property {} updated with txHash: {}", propertyId, txHash);
        
        return updatedProperty;
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
        
        // Save to database first
        PropertyModel savedProperty = propertyRepositoryPort.save(property);
        
        logger.info("📝 Property registered in database: matriculaId={}, id={}", 
            matriculaId, savedProperty.getId());
        
        // Publish blockchain job asynchronously
        try {
            String jobId = blockchainJobPublisher.publishRegisterPropertyJob(
                savedProperty.getId(), // Include propertyId for webhook callback
                String.valueOf(matriculaId),
                String.valueOf(folha),
                comarca,
                endereco,
                String.valueOf(metragem),
                proprietario,
                String.valueOf(matriculaOrigem != null ? matriculaOrigem : 0),
                tipo.ordinal(), // Convert enum to integer
                isRegular
            );
            
            logger.info("🚀 Blockchain job published: jobId={}, propertyId={}, matriculaId={}", 
                jobId, savedProperty.getId(), matriculaId);
            
        } catch (Exception e) {
            logger.error("⚠️  Failed to publish blockchain job for property {}: {}", 
                matriculaId, e.getMessage());
            // Don't throw - property is already saved in DB
            // The job can be retried later or handled by monitoring
        }
        
        return savedProperty;
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

