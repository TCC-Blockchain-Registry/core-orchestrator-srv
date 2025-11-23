package com.core.port.output.messaging;

import com.core.domain.model.blockchain.JobType;

import java.util.List;
import java.util.Map;

/**
 * Output Port for publishing blockchain jobs to message queue
 *
 * This interface abstracts the messaging infrastructure (RabbitMQ, Kafka, etc.)
 * from the domain layer, following hexagonal architecture principles.
 *
 * The implementation will handle serialization, queue configuration, and
 * error handling specific to the chosen messaging technology.
 */
public interface BlockchainJobPublisherPort {

    /**
     * Publish a blockchain job to the queue
     *
     * @param jobType Type of blockchain operation
     * @param payload Job payload data
     * @return Job ID for tracking
     */
    String publishJob(JobType jobType, Map<String, Object> payload);

    /**
     * Publish a REGISTER_PROPERTY job
     *
     * @param propertyId Database ID for webhook callback
     * @param matriculaId Property registration number
     * @param folha Sheet number
     * @param comarca District
     * @param endereco Address
     * @param metragem Area in square meters
     * @param proprietario Owner's Ethereum address
     * @param matriculaOrigem Original matricula (0 if new)
     * @param tipo Property type (0=URBANO, 1=RURAL, 2=LITORAL)
     * @param isRegular Whether property is regular
     * @return Job ID
     */
    String publishRegisterPropertyJob(
            Long propertyId,
            String matriculaId,
            String folha,
            String comarca,
            String endereco,
            String metragem,
            String proprietario,
            String matriculaOrigem,
            Integer tipo,
            Boolean isRegular);

    /**
     * Publish a CONFIGURE_TRANSFER job
     *
     * @param transferId Transfer ID from database
     * @param from Seller's Ethereum address
     * @param to Buyer's Ethereum address
     * @param matriculaId Property registration number
     * @param approvers List of approver addresses
     * @return Job ID
     */
    String publishConfigureTransferJob(
            Long transferId,
            String from,
            String to,
            String matriculaId,
            List<String> approvers);

    /**
     * Publish an APPROVE_TRANSFER job
     *
     * @param transferId Transfer ID
     * @param from Seller's Ethereum address
     * @param to Buyer's Ethereum address
     * @param matriculaId Property registration number
     * @param approverAddress Approver's Ethereum address
     * @return Job ID
     */
    String publishApproveTransferJob(
            String transferId,
            String from,
            String to,
            String matriculaId,
            String approverAddress);

    /**
     * Publish an EXECUTE_TRANSFER job
     *
     * @param transferId Transfer ID
     * @param matriculaId Property registration number
     * @param seller Seller's Ethereum address
     * @param buyer Buyer's Ethereum address
     * @return Job ID
     */
    String publishExecuteTransferJob(
            String transferId,
            String matriculaId,
            String seller,
            String buyer);
}
