package com.core.domain.service.blockchain;

import com.core.config.RabbitMQConfig;
import com.core.domain.model.blockchain.BlockchainJobDTO;
import com.core.domain.model.blockchain.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Blockchain Job Publisher Service
 * Publishes blockchain jobs to RabbitMQ queue for asynchronous processing
 */
@Service
public class BlockchainJobPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(BlockchainJobPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    public BlockchainJobPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    /**
     * Publish a blockchain job to the queue
     * 
     * @param jobType Type of blockchain operation
     * @param payload Job payload data
     * @return Job ID for tracking
     */
    public String publishJob(JobType jobType, Map<String, Object> payload) {
        try {
            BlockchainJobDTO job = new BlockchainJobDTO(jobType, payload);
            
            logger.info("📤 Publishing blockchain job to queue: type={}, id={}", jobType, job.getId());
            logger.debug("Job payload: {}", payload);
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.BLOCKCHAIN_JOBS_QUEUE,
                job
            );
            
            logger.info("✅ Job published successfully: jobId={}", job.getId());
            
            return job.getId();
            
        } catch (Exception e) {
            logger.error("❌ Failed to publish blockchain job: type={}, error={}", 
                jobType, e.getMessage(), e);
            throw new RuntimeException("Failed to publish blockchain job: " + e.getMessage(), e);
        }
    }
    
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
    public String publishRegisterPropertyJob(
            Long propertyId,
            String matriculaId,
            String folha,
            String comarca,
            String endereco,
            String metragem,
            String proprietario,
            String matriculaOrigem,
            Integer tipo,
            Boolean isRegular) {
        
        Map<String, Object> payload = Map.of(
            "propertyId", propertyId,
            "matriculaId", matriculaId,
            "folha", folha,
            "comarca", comarca,
            "endereco", endereco,
            "metragem", metragem,
            "proprietario", proprietario,
            "matriculaOrigem", matriculaOrigem,
            "tipo", tipo,
            "isRegular", isRegular
        );
        
        return publishJob(JobType.REGISTER_PROPERTY, payload);
    }
    
    /**
     * Publish a CONFIGURE_TRANSFER job
     * 
     * @param from Seller's Ethereum address
     * @param to Buyer's Ethereum address
     * @param matriculaId Property registration number
     * @param approvers List of approver addresses
     * @return Job ID
     */
    public String publishConfigureTransferJob(
            String from,
            String to,
            String matriculaId,
            java.util.List<String> approvers) {
        
        Map<String, Object> payload = Map.of(
            "from", from,
            "to", to,
            "matriculaId", matriculaId,
            "approvers", approvers
        );
        
        return publishJob(JobType.CONFIGURE_TRANSFER, payload);
    }
    
    /**
     * Publish an APPROVE_TRANSFER job
     * 
     * @param transferId Transfer ID
     * @param matriculaId Property registration number
     * @param approverAddress Approver's Ethereum address
     * @return Job ID
     */
    public String publishApproveTransferJob(
            String transferId,
            String matriculaId,
            String approverAddress) {
        
        Map<String, Object> payload = Map.of(
            "transferId", transferId,
            "matriculaId", matriculaId,
            "approverAddress", approverAddress
        );
        
        return publishJob(JobType.APPROVE_TRANSFER, payload);
    }
    
    /**
     * Publish an EXECUTE_TRANSFER job
     * 
     * @param transferId Transfer ID
     * @param matriculaId Property registration number
     * @param seller Seller's Ethereum address
     * @param buyer Buyer's Ethereum address
     * @return Job ID
     */
    public String publishExecuteTransferJob(
            String transferId,
            String matriculaId,
            String seller,
            String buyer) {
        
        Map<String, Object> payload = Map.of(
            "transferId", transferId,
            "matriculaId", matriculaId,
            "seller", seller,
            "buyer", buyer
        );
        
        return publishJob(JobType.EXECUTE_TRANSFER, payload);
    }
}

