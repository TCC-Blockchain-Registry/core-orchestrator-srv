package com.core.adapter.output.messaging;

import com.core.config.RabbitMQConfig;
import com.core.domain.model.blockchain.BlockchainJobDTO;
import com.core.domain.model.blockchain.JobType;
import com.core.port.output.messaging.BlockchainJobPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ Adapter for publishing blockchain jobs
 *
 * This adapter implements the BlockchainJobPublisherPort interface using RabbitMQ
 * as the messaging infrastructure. It handles serialization, queue routing, and
 * error handling specific to RabbitMQ.
 *
 * Following hexagonal architecture, this adapter is in the infrastructure layer
 * and can be easily swapped for other messaging implementations (Kafka, SQS, etc.)
 * without affecting the domain layer.
 */
@Component
public class RabbitMQJobPublisherAdapter implements BlockchainJobPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQJobPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQJobPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
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

    @Override
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

    @Override
    public String publishConfigureTransferJob(
            Long transferId,
            String from,
            String to,
            String matriculaId,
            List<String> approvers) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", transferId.toString());
        payload.put("from", from);
        payload.put("to", to);
        // Adicionar seller/buyer para compatibilidade com Queue Worker
        payload.put("seller", from);
        payload.put("buyer", to);
        payload.put("matriculaId", matriculaId);
        payload.put("approvers", approvers);

        return publishJob(JobType.CONFIGURE_TRANSFER, payload);
    }

    @Override
    public String publishApproveTransferJob(
            String transferId,
            String from,
            String to,
            String matriculaId,
            String approverAddress) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", transferId);
        payload.put("from", from);
        payload.put("to", to);
        payload.put("matriculaId", matriculaId);
        payload.put("approverAddress", approverAddress);

        return publishJob(JobType.APPROVE_TRANSFER, payload);
    }

    @Override
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
