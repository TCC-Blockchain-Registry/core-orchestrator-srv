package com.core.domain.service.transfer;

import com.core.domain.model.property.PropertyModel;
import com.core.domain.model.property.PropertyStatus;
import com.core.domain.model.transfer.TransferModel;
import com.core.domain.model.transfer.TransferStatus;
import com.core.domain.model.blockchain.JobType;
import com.core.domain.model.user.UserModel;
import com.core.port.input.transfer.TransferUseCase;
import com.core.port.output.messaging.BlockchainJobPublisherPort;
import com.core.port.output.property.PropertyRepositoryPort;
import com.core.port.output.transfer.TransferRepositoryPort;
import com.core.port.output.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Transfer Domain Service
 * Implements business logic for property transfer operations
 */
@Service
public class TransferService implements TransferUseCase {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final TransferRepositoryPort transferRepositoryPort;
    private final PropertyRepositoryPort propertyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final BlockchainJobPublisherPort jobPublisherPort;

    public TransferService(TransferRepositoryPort transferRepositoryPort,
                          PropertyRepositoryPort propertyRepositoryPort,
                          UserRepositoryPort userRepositoryPort,
                          BlockchainJobPublisherPort jobPublisherPort) {
        this.transferRepositoryPort = transferRepositoryPort;
        this.propertyRepositoryPort = propertyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.jobPublisherPort = jobPublisherPort;
    }

    /**
     * Initiates a property transfer
     *
     * @param matriculaId Property to transfer
     * @param buyerId Buyer user ID
     * @return Created transfer model
     */
    @Override
    public TransferModel initiateTransfer(Long matriculaId, Long buyerId) {
        logger.info("Initiating transfer for property {} to buyer {}", matriculaId, buyerId);

        // 1. Validate property exists and is in OK status
        PropertyModel property = propertyRepositoryPort.findByMatriculaId(matriculaId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + matriculaId));

        if (property.getStatus() != PropertyStatus.OK) {
            throw new IllegalStateException("Property must be in OK status to transfer. Current: " + property.getStatus());
        }

        // 2. Check if there's already an active transfer for this property
        if (transferRepositoryPort.hasActiveTransfer(matriculaId)) {
            throw new IllegalStateException("Property already has an active transfer in progress");
        }

        // 3. Validate buyer exists and is different from seller
        UserModel buyer = userRepositoryPort.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + buyerId));

        if (property.getProprietario().equals(buyerId)) {
            throw new IllegalArgumentException("Buyer cannot be the same as current owner");
        }

        // 4. Get seller information
        UserModel seller = userRepositoryPort.findById(property.getProprietario())
                .orElseThrow(() -> new IllegalArgumentException("Current owner not found: " + property.getProprietario()));

        // 5. Create transfer model
        TransferModel transfer = new TransferModel(matriculaId, seller.getId(), buyer.getId());
        transfer.setStatus(TransferStatus.PENDENTE);

        // 6. Save to database
        TransferModel savedTransfer = transferRepositoryPort.save(transfer);

        logger.info("Transfer created in database: id={}, matriculaId={}, seller={}, buyer={}",
            savedTransfer.getId(), matriculaId, seller.getId(), buyer.getId());

        // 7. Get approvers (hardcoded for now - can be made dynamic later)
        List<String> approvers = getApproversForProperty(property);

        // 8. Publish CONFIGURE_TRANSFER job
        try {
            String jobId = jobPublisherPort.publishConfigureTransferJob(
                savedTransfer.getId(),
                seller.getWalletAddress(),
                buyer.getWalletAddress(),
                String.valueOf(matriculaId),
                approvers
            );

            logger.info("CONFIGURE_TRANSFER job published: jobId={}, transferId={}", jobId, savedTransfer.getId());

            // 9. Update status to CONFIGURANDO
            savedTransfer.updateStatus(TransferStatus.CONFIGURANDO);
            savedTransfer = transferRepositoryPort.save(savedTransfer);

        } catch (Exception e) {
            logger.error("Failed to publish CONFIGURE_TRANSFER job for transfer {}: {}",
                savedTransfer.getId(), e.getMessage());
            // Keep as PENDENTE - can be retried later
        }

        return savedTransfer;
    }

    /**
     * Records an approver's approval for a transfer
     *
     * @param transferId Transfer ID
     * @param approverId Approver user ID
     * @return Updated transfer model
     */
    @Override
    public TransferModel approveTransfer(Long transferId, Long approverId) {
        logger.info("Processing approval for transfer {} by approver {}", transferId, approverId);

        // 1. Validate transfer exists and can be approved
        TransferModel transfer = transferRepositoryPort.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + transferId));

        if (!transfer.canBeApproved()) {
            throw new IllegalStateException("Transfer must be in AGUARDANDO_APROVACOES status. Current: " + transfer.getStatus());
        }

        // 2. Get approver wallet address
        UserModel approver = userRepositoryPort.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found: " + approverId));

        // 3. Get seller and buyer wallet addresses
        UserModel seller = userRepositoryPort.findById(transfer.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        UserModel buyer = userRepositoryPort.findById(transfer.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        // 4. Publish APPROVE_TRANSFER job
        try {
            String jobId = jobPublisherPort.publishApproveTransferJob(
                String.valueOf(transferId),
                seller.getWalletAddress(),
                buyer.getWalletAddress(),
                String.valueOf(transfer.getMatriculaId()),
                approver.getWalletAddress()
            );

            logger.info("APPROVE_TRANSFER job published: jobId={}, transferId={}, approver={}",
                jobId, transferId, approver.getWalletAddress());

        } catch (Exception e) {
            logger.error("Failed to publish APPROVE_TRANSFER job: {}", e.getMessage());
            throw new RuntimeException("Failed to publish approval job", e);
        }

        return transfer;
    }

    /**
     * Records buyer's acceptance of a transfer
     *
     * @param transferId Transfer ID
     * @param buyerId Buyer user ID (for validation)
     * @return Updated transfer model
     */
    @Override
    public TransferModel acceptTransfer(Long transferId, Long buyerId) {
        logger.info("Processing buyer acceptance for transfer {} by buyer {}", transferId, buyerId);

        // 1. Validate transfer exists
        TransferModel transfer = transferRepositoryPort.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + transferId));

        if (!transfer.canBeApproved()) {
            throw new IllegalStateException("Transfer must be in AGUARDANDO_APROVACOES status. Current: " + transfer.getStatus());
        }

        // 2. Validate buyer matches
        if (!transfer.getBuyerId().equals(buyerId)) {
            throw new IllegalArgumentException("Only the buyer can accept this transfer");
        }

        // 3. Get buyer wallet address
        UserModel buyer = userRepositoryPort.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        // 4. Publish ACCEPT_TRANSFER job
        try {
            String jobId = jobPublisherPort.publishJob(
                JobType.ACCEPT_TRANSFER,
                Map.of(
                    "transferId", String.valueOf(transferId),
                    "matriculaId", String.valueOf(transfer.getMatriculaId()),
                    "buyerAddress", buyer.getWalletAddress()
                )
            );

            logger.info("ACCEPT_TRANSFER job published: jobId={}, transferId={}", jobId, transferId);

        } catch (Exception e) {
            logger.error("Failed to publish ACCEPT_TRANSFER job: {}", e.getMessage());
            throw new RuntimeException("Failed to publish acceptance job", e);
        }

        return transfer;
    }

    /**
     * Updates transfer with blockchain transaction hash
     * Called by webhook after job completion
     *
     * @param transferId Transfer ID
     * @param txHash Transaction hash
     * @return Updated transfer
     */
    @Override
    public TransferModel updateBlockchainTxHash(Long transferId, String txHash) {
        logger.info("Updating transfer {} with txHash {}", transferId, txHash);

        TransferModel transfer = transferRepositoryPort.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferId));

        transfer.setTransactionHash(txHash);

        // If we're in CONFIGURANDO, move to AGUARDANDO_APROVACOES
        if (transfer.getStatus() == TransferStatus.CONFIGURANDO) {
            transfer.updateStatus(TransferStatus.AGUARDANDO_APROVACOES);
        }

        TransferModel updatedTransfer = transferRepositoryPort.save(transfer);

        logger.info("Transfer {} updated with txHash and status {}", transferId, updatedTransfer.getStatus());

        return updatedTransfer;
    }

    /**
     * Marks transfer as completed
     * Called by webhook when PropertyTransferred event is received
     *
     * @param transferId Transfer ID
     * @return Updated transfer
     */
    @Override
    public TransferModel completeTransfer(Long transferId) {
        logger.info("Completing transfer {}", transferId);

        TransferModel transfer = transferRepositoryPort.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferId));

        transfer.complete();
        TransferModel updatedTransfer = transferRepositoryPort.save(transfer);

        logger.info("Transfer {} marked as CONCLUIDA", transferId);

        return updatedTransfer;
    }

    // Query methods

    @Override
    public TransferModel findById(Long id) {
        return transferRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found with id: " + id));
    }

    @Override
    public List<TransferModel> findByMatriculaId(Long matriculaId) {
        return transferRepositoryPort.findByMatriculaId(matriculaId);
    }

    @Override
    public List<TransferModel> findBySellerId(Long sellerId) {
        return transferRepositoryPort.findBySellerId(sellerId);
    }

    @Override
    public List<TransferModel> findByBuyerId(Long buyerId) {
        return transferRepositoryPort.findByBuyerId(buyerId);
    }

    @Override
    public List<TransferModel> findAll() {
        return transferRepositoryPort.findAll();
    }

    // Helper methods

    /**
     * Gets the list of approvers for a property
     * TODO: Make this configurable based on property type/location
     */
    private List<String> getApproversForProperty(PropertyModel property) {
        // For now, return hardcoded approvers (can be retrieved from environment or database)
        // These should match the addresses registered in ApproversRegistry contract
        return Arrays.asList(
            System.getenv("FINANCIAL_APPROVER_ADDRESS"),
            System.getenv("REGISTRY_OFFICE_APPROVER_ADDRESS"),
            System.getenv("MUNICIPALITY_APPROVER_ADDRESS")
        );
    }
}
