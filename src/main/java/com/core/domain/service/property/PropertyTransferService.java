package com.core.domain.service.property;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyTransferModel;
import com.core.domain.model.property.TransferStatus;
import com.core.port.input.property.PropertyTransferUseCase;
import com.core.port.output.property.PropertyRepositoryPort;
import com.core.port.output.property.PropertyTransferRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Property Transfer Domain Service
 * Implements business logic for property transfer operations
 */
@Service
public class PropertyTransferService implements PropertyTransferUseCase {
    
    private final PropertyTransferRepositoryPort transferRepositoryPort;
    private final PropertyRepositoryPort propertyRepositoryPort;
    
    public PropertyTransferService(PropertyTransferRepositoryPort transferRepositoryPort,
                                  PropertyRepositoryPort propertyRepositoryPort) {
        this.transferRepositoryPort = transferRepositoryPort;
        this.propertyRepositoryPort = propertyRepositoryPort;
    }
    
    @Override
    public PropertyTransferModel initiateTransfer(Long propertyId, String fromProprietario,
                                                 String toProprietario) {
        validateTransferInput(propertyId, fromProprietario, toProprietario);

        PropertyModel property = propertyRepositoryPort.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        if (!property.getProprietario().equals(fromProprietario)) {
            throw new IllegalArgumentException("From proprietario does not match current property owner");
        }
        
        // BUSINESS RULE: Check if property has active transfer
        if (hasActiveTransfer(propertyId)) {
            throw new IllegalStateException("Property already has an active transfer in progress");
        }

        PropertyTransferModel transfer = new PropertyTransferModel();
        transfer.setPropertyId(propertyId);
        transfer.setFromProprietario(fromProprietario);
        transfer.setToProprietario(toProprietario);

        return transferRepositoryPort.save(transfer);
    }
    
    @Override
    public PropertyTransferModel completeTransfer(Long transferId, String blockchainTxHash) {
        PropertyTransferModel transfer = findById(transferId);

        if (transfer.getStatus() != TransferStatus.APPROVED) {
            throw new IllegalStateException("Transfer must be approved before completing");
        }

        transfer.completeWithBlockchain(blockchainTxHash);
        PropertyTransferModel savedTransfer = transferRepositoryPort.save(transfer);

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

