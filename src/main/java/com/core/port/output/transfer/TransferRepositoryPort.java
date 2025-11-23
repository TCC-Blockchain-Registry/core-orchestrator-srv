package com.core.port.output.transfer;

import com.core.domain.model.transfer.TransferModel;
import java.util.List;
import java.util.Optional;

/**
 * Transfer Repository Output Port
 * Defines the contract for transfer persistence operations
 */
public interface TransferRepositoryPort {

    /**
     * Save a transfer to the repository
     *
     * @param transfer The transfer model to save
     * @return The saved transfer model with generated ID
     */
    TransferModel save(TransferModel transfer);

    /**
     * Find a transfer by ID
     *
     * @param id The transfer ID to search for
     * @return Optional containing the transfer if found, empty otherwise
     */
    Optional<TransferModel> findById(Long id);

    /**
     * Find all transfers for a specific property
     *
     * @param matriculaId The property matricula ID
     * @return List of transfers for the property
     */
    List<TransferModel> findByMatriculaId(Long matriculaId);

    /**
     * Find all transfers where user is the seller
     *
     * @param sellerId The seller user ID
     * @return List of transfers where user is selling
     */
    List<TransferModel> findBySellerId(Long sellerId);

    /**
     * Find all transfers where user is the buyer
     *
     * @param buyerId The buyer user ID
     * @return List of transfers where user is buying
     */
    List<TransferModel> findByBuyerId(Long buyerId);

    /**
     * Find all transfers in the system
     *
     * @return List of all transfers
     */
    List<TransferModel> findAll();

    /**
     * Check if there's an active (non-completed) transfer for a property
     *
     * @param matriculaId The property matricula ID
     * @return true if there's an active transfer, false otherwise
     */
    boolean hasActiveTransfer(Long matriculaId);
}
