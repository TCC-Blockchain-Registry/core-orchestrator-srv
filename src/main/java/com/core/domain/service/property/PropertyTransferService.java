package com.core.domain.service.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyTransferModel;
import com.core.domain.model.property.TransferStatus;
import com.core.domain.service.blockchain.BlockchainJobPublisher;
import com.core.port.input.property.PropertyTransferUseCase;
import com.core.port.output.property.PropertyRepositoryPort;
import com.core.port.output.property.PropertyTransferRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Property Transfer Domain Service
 * Implements business logic for property transfer operations
 */
@Service
public class PropertyTransferService implements PropertyTransferUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyTransferService.class);
    
    private final PropertyTransferRepositoryPort transferRepositoryPort;
    private final PropertyRepositoryPort propertyRepositoryPort;
    private final BlockchainJobPublisher blockchainJobPublisher;
    
    public PropertyTransferService(PropertyTransferRepositoryPort transferRepositoryPort,
                                  PropertyRepositoryPort propertyRepositoryPort,
                                  BlockchainJobPublisher blockchainJobPublisher) {
        this.transferRepositoryPort = transferRepositoryPort;
        this.propertyRepositoryPort = propertyRepositoryPort;
        this.blockchainJobPublisher = blockchainJobPublisher;
    }
    
    @Override
    public PropertyTransferModel initiateTransfer(Long propertyId, String fromProprietario, 
                                                 String toProprietario) {
        // Validate input
        validateTransferInput(propertyId, fromProprietario, toProprietario);
        
        // Check if property exists
        PropertyModel property = propertyRepositoryPort.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));
        
        // Validate fromProprietario matches current owner
        if (!property.getProprietario().equals(fromProprietario)) {
            throw new IllegalArgumentException("From proprietario does not match current property owner");
        }
        
        // BUSINESS RULE: Check if property has active transfer
        if (hasActiveTransfer(propertyId)) {
            throw new IllegalStateException("Property already has an active transfer in progress");
        }
        
        // Create transfer
        PropertyTransferModel transfer = new PropertyTransferModel();
        transfer.setPropertyId(propertyId);
        transfer.setFromProprietario(fromProprietario);
        transfer.setToProprietario(toProprietario);
        // Status is already set to PENDING in constructor
        
        // Save to database first
        PropertyTransferModel savedTransfer = transferRepositoryPort.save(transfer);
        
        logger.info("📝 Transfer initiated in database: transferId={}, propertyId={}, from={}, to={}", 
            savedTransfer.getId(), propertyId, fromProprietario, toProprietario);
        
        // Publish blockchain job asynchronously
        try {
            // TODO: Get real approver addresses from database or configuration
            // For now, using placeholder approvers
            List<String> approvers = Arrays.asList(
                "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "0xbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
            );
            
            String jobId = blockchainJobPublisher.publishConfigureTransferJob(
                fromProprietario,
                toProprietario,
                String.valueOf(property.getMatriculaId()),
                approvers
            );
            
            logger.info("🚀 Blockchain transfer configuration job published: jobId={}, transferId={}", 
                jobId, savedTransfer.getId());
            
        } catch (Exception e) {
            logger.error("⚠️  Failed to publish blockchain job for transfer {}: {}", 
                savedTransfer.getId(), e.getMessage());
            // Don't throw - transfer is already saved in DB
            // The job can be retried later or handled by monitoring
        }
        
        return savedTransfer;
    }
    
    @Override
    public PropertyTransferModel completeTransfer(Long transferId, String blockchainTxHash) {
        PropertyTransferModel transfer = findById(transferId);
        
        // Validate transfer is approved
        if (transfer.getStatus() != TransferStatus.APPROVED) {
            throw new IllegalStateException("Transfer must be approved before completing");
        }
        
        // Update transfer
        transfer.completeWithBlockchain(blockchainTxHash);
        PropertyTransferModel savedTransfer = transferRepositoryPort.save(transfer);
        
        // Update property owner
        PropertyModel property = propertyRepositoryPort.findById(transfer.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        
        property.updateProprietario(transfer.getToProprietario());
        propertyRepositoryPort.save(property);
        
        return savedTransfer;
    }
    
    @Override
    public PropertyTransferModel approveTransfer(Long transferId) {
        PropertyTransferModel transfer = findById(transferId);
        
        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only pending transfers can be approved");
        }
        
        transfer.approve();
        return transferRepositoryPort.save(transfer);
    }
    
    @Override
    public PropertyTransferModel rejectTransfer(Long transferId) {
        PropertyTransferModel transfer = findById(transferId);
        
        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Cannot reject completed transfer");
        }
        
        transfer.reject();
        return transferRepositoryPort.save(transfer);
    }
    
    @Override
    public PropertyTransferModel cancelTransfer(Long transferId) {
        PropertyTransferModel transfer = findById(transferId);
        
        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed transfer");
        }
        
        transfer.cancel();
        return transferRepositoryPort.save(transfer);
    }
    
    @Override
    public PropertyTransferModel findById(Long id) {
        return transferRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found with id: " + id));
    }
    
    @Override
    public List<PropertyTransferModel> findByPropertyId(Long propertyId) {
        return transferRepositoryPort.findByPropertyId(propertyId);
    }
    
    @Override
    public List<PropertyTransferModel> findAll() {
        return transferRepositoryPort.findAll();
    }
    
    @Override
    public boolean hasActiveTransfer(Long propertyId) {
        List<PropertyTransferModel> transfers = transferRepositoryPort.findByPropertyId(propertyId);
        
        return transfers.stream()
                .anyMatch(t -> t.getStatus() == TransferStatus.PENDING || 
                              t.getStatus() == TransferStatus.APPROVED);
    }
    
    private void validateTransferInput(Long propertyId, String fromProprietario, String toProprietario) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }
        
        if (fromProprietario == null || fromProprietario.trim().isEmpty()) {
            throw new IllegalArgumentException("From proprietario cannot be empty");
        }
        
        if (toProprietario == null || toProprietario.trim().isEmpty()) {
            throw new IllegalArgumentException("To proprietario cannot be empty");
        }
        
        // Validate Ethereum address format
        if (!fromProprietario.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum address format for from proprietario");
        }
        
        if (!toProprietario.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid Ethereum address format for to proprietario");
        }
        
        if (fromProprietario.equals(toProprietario)) {
            throw new IllegalArgumentException("Cannot transfer to the same proprietario");
        }
    }
}

