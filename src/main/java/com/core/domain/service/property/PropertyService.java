package com.core.domain.service.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyStatus;
import com.core.domain.model.property.PropertyType;
import com.core.port.input.property.PropertyUseCase;
import com.core.port.output.user.UserRepositoryPort;
import com.core.port.output.messaging.BlockchainJobPublisherPort;
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
    private final BlockchainJobPublisherPort jobPublisherPort;
    private final UserRepositoryPort userRepositoryPort;

    public PropertyService(PropertyRepositoryPort propertyRepositoryPort,
                          BlockchainJobPublisherPort jobPublisherPort,
                          UserRepositoryPort userRepositoryPort) {
        this.propertyRepositoryPort = propertyRepositoryPort;
        this.jobPublisherPort = jobPublisherPort;
        this.userRepositoryPort = userRepositoryPort;
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
    
    public PropertyModel updateRequestHash(Long propertyId, String requestHash, String approvalStatus) {
        logger.info("Updating requestHash for property {}: requestHash={}, status={}",
            propertyId, requestHash, approvalStatus);

        PropertyModel property = propertyRepositoryPort.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + propertyId));

        property.setRequestHash(requestHash);
        property.setApprovalStatus(approvalStatus != null ? approvalStatus : "PENDING_APPROVALS");

        // Update status based on approvalStatus
        if ("PENDING_APPROVALS".equals(approvalStatus)) {
            property.setStatus(PropertyStatus.PROCESSANDO_REGISTRO);
        } else if ("EXECUTED".equals(approvalStatus)) {
            property.setStatus(PropertyStatus.OK);
        }

        PropertyModel updatedProperty = propertyRepositoryPort.save(property);

        logger.info("✅ Property {} updated with requestHash for V2 approval system", propertyId);

        return updatedProperty;
    }
    
    @Override
    public PropertyModel registerProperty(Long matriculaId, Long folha, String comarca,
                                         String endereco, Long metragem, Long proprietario,
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
        property.setStatus(PropertyStatus.PENDENTE);  // Status inicial

        // Save to database first
        PropertyModel savedProperty = propertyRepositoryPort.save(property);

        logger.info("📝 Property registered in database: matriculaId={}, id={}",
            matriculaId, savedProperty.getId());

        // Publish blockchain job asynchronously
        try {
            String jobId = jobPublisherPort.publishRegisterPropertyJob(
                savedProperty.getId(), // Include propertyId for webhook callback
                String.valueOf(matriculaId),
                String.valueOf(folha),
                comarca,
                endereco,
                String.valueOf(metragem),
                String.valueOf(proprietario), // Convert Long (userId) to String
                String.valueOf(matriculaOrigem != null ? matriculaOrigem : 0),
                tipo.ordinal(), // Convert enum to integer
                isRegular
            );

            logger.info("🚀 Blockchain job published: jobId={}, propertyId={}, matriculaId={}",
                jobId, savedProperty.getId(), matriculaId);

            // Update status to PROCESSANDO_REGISTRO (being processed on blockchain)
            savedProperty.setStatus(PropertyStatus.PROCESSANDO_REGISTRO);
            savedProperty = propertyRepositoryPort.save(savedProperty);

        } catch (Exception e) {
            logger.error("⚠️  Failed to publish blockchain job for property {}: {}",
                matriculaId, e.getMessage());
            // Keep as PENDENTE if job publishing fails - can be retried later
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
    public List<PropertyModel> findByProprietario(Long proprietario) {
        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietario cannot be null");
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

    /**
     * Updates the owner of a property and sets status to OK
     * Called when a property transfer is completed on blockchain
     *
     * @param matriculaId The property matricula ID
     * @param newOwnerAddress The blockchain wallet address of the new owner
     * @return Updated property model
     */
    public PropertyModel updateOwner(Long matriculaId, String newOwnerAddress) {
        logger.info("Updating owner for property {}: newOwner={}", matriculaId, newOwnerAddress);

        PropertyModel property = propertyRepositoryPort.findByMatriculaId(matriculaId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + matriculaId));

        // Find user by wallet address
        var user = userRepositoryPort.findByWalletAddress(newOwnerAddress)
                .orElseThrow(() -> new RuntimeException("User not found with wallet address: " + newOwnerAddress));

        // Update owner using business method
        property.updateProprietario(user.getId());

        // Set status to OK (transfer completed)
        property.setStatus(PropertyStatus.OK);

        PropertyModel updatedProperty = propertyRepositoryPort.save(property);

        logger.info("✅ Property {} owner updated to user {} (wallet: {}), status: OK",
            matriculaId, user.getId(), newOwnerAddress);

        return updatedProperty;
    }

    /**
     * Initiates a property transfer, setting status to EM_TRANSFERENCIA
     *
     * @param matriculaId The property matricula ID
     * @return Updated property model
     */
    public PropertyModel initiateTransfer(Long matriculaId) {
        logger.info("Initiating transfer for property {}", matriculaId);

        PropertyModel property = propertyRepositoryPort.findByMatriculaId(matriculaId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + matriculaId));

        // Only allow transfer if property is in OK status
        if (property.getStatus() != PropertyStatus.OK) {
            throw new IllegalStateException("Property must be in OK status to initiate transfer. Current status: "
                + property.getStatus());
        }

        property.setStatus(PropertyStatus.EM_TRANSFERENCIA);
        PropertyModel updatedProperty = propertyRepositoryPort.save(property);

        logger.info("✅ Property {} status updated to EM_TRANSFERENCIA", matriculaId);

        return updatedProperty;
    }

    /**
     * Updates the status of a property
     *
     * @param matriculaId The property matricula ID
     * @param newStatus The new status
     * @return Updated property model
     */
    public PropertyModel updateStatus(Long matriculaId, PropertyStatus newStatus) {
        logger.info("Updating status for property {}: newStatus={}", matriculaId, newStatus);

        PropertyModel property = propertyRepositoryPort.findByMatriculaId(matriculaId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + matriculaId));

        property.setStatus(newStatus);
        PropertyModel updatedProperty = propertyRepositoryPort.save(property);

        logger.info("✅ Property {} status updated to {}", matriculaId, newStatus);

        return updatedProperty;
    }

    private void validatePropertyInput(Long matriculaId, Long folha, String comarca,
                                       String endereco, Long metragem, Long proprietario,
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
        
        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietario (userId) cannot be null");
        }
        
        if (tipo == null) {
            throw new IllegalArgumentException("Property type cannot be null");
        }
    }
}

