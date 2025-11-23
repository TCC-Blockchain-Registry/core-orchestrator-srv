package com.core.port.input.transfer;

import com.core.domain.model.transfer.TransferModel;

import java.util.List;

/**
 * Transfer Use Case Input Port
 * Defines operations for property transfer management
 */
public interface TransferUseCase {

    /**
     * Initiates a property transfer
     *
     * @param matriculaId Property to transfer
     * @param buyerId Buyer user ID
     * @return Created transfer model
     */
    TransferModel initiateTransfer(Long matriculaId, Long buyerId);

    /**
     * Records an approver's approval for a transfer
     *
     * @param transferId Transfer ID
     * @param approverId Approver user ID
     * @return Updated transfer model
     */
    TransferModel approveTransfer(Long transferId, Long approverId);

    /**
     * Records buyer's acceptance of a transfer
     *
     * @param transferId Transfer ID
     * @param buyerId Buyer user ID (for validation)
     * @return Updated transfer model
     */
    TransferModel acceptTransfer(Long transferId, Long buyerId);

    /**
     * Find transfer by ID
     */
    TransferModel findById(Long id);

    /**
     * Find transfers by property matricula ID
     */
    List<TransferModel> findByMatriculaId(Long matriculaId);

    /**
     * Find transfers by seller ID
     */
    List<TransferModel> findBySellerId(Long sellerId);

    /**
     * Find transfers by buyer ID
     */
    List<TransferModel> findByBuyerId(Long buyerId);

    /**
     * Find all transfers
     */
    List<TransferModel> findAll();

    /**
     * Updates transfer with blockchain transaction hash
     * Called by webhook after job completion
     *
     * @param transferId Transfer ID
     * @param txHash Transaction hash
     * @return Updated transfer
     */
    TransferModel updateBlockchainTxHash(Long transferId, String txHash);

    /**
     * Marks transfer as completed
     * Called by webhook when PropertyTransferred event is received
     *
     * @param transferId Transfer ID
     * @return Updated transfer
     */
    TransferModel completeTransfer(Long transferId);
}
