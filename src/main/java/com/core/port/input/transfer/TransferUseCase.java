package com.core.port.input.transfer;

import com.core.domain.model.transfer.TransferModel;

import java.util.List;

/**
 * Transfer Use Case Input Port
 * Defines operations for property transfer management
 */
public interface TransferUseCase {

    /**
     * Initiates a property transfer (legacy method - accepts buyerId)
     *
     * @param matriculaId Property to transfer
     * @param buyerId Buyer user ID
     * @return Created transfer model
     * @deprecated Use initiateTransferByCpf or initiateTransferByWallet instead
     */
    @Deprecated
    TransferModel initiateTransfer(Long matriculaId, Long buyerId);

    /**
     * Initiates a property transfer using buyer's CPF
     * Validates that the authenticated user is the property owner
     *
     * @param authenticatedUserId ID of the authenticated user (from JWT)
     * @param matriculaId Property to transfer
     * @param buyerCpf Buyer's CPF (11 digits)
     * @return Created transfer model
     * @throws IllegalArgumentException if buyer not found or authenticated user is not the owner
     */
    TransferModel initiateTransferByCpf(Long authenticatedUserId, Long matriculaId, String buyerCpf);

    /**
     * Initiates a property transfer using buyer's wallet address
     * Validates that the authenticated user is the property owner
     *
     * @param authenticatedUserId ID of the authenticated user (from JWT)
     * @param matriculaId Property to transfer
     * @param buyerWalletAddress Buyer's wallet address (0x...)
     * @return Created transfer model
     * @throws IllegalArgumentException if buyer not found or authenticated user is not the owner
     */
    TransferModel initiateTransferByWallet(Long authenticatedUserId, Long matriculaId, String buyerWalletAddress);

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
