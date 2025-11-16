package com.core.adapter.output.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ Publisher Service
 * Publishes blockchain jobs to RabbitMQ queue
 */
@Service
public class RabbitMQPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String publishJob(String jobType, Object payload) {
        BlockchainJob job = new BlockchainJob(jobType, payload);

        logger.info("Publishing blockchain job: {} with ID: {}", jobType, job.getId());

        try {
            // Publish to exchange with routing key
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    "blockchain." + jobType.toLowerCase(),
                    job
            );

            logger.info("Successfully published job: {}", job.getId());
            return job.getId();
        } catch (Exception e) {
            logger.error("Failed to publish job: {} - Error: {}", job.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish blockchain job", e);
        }
    }

    public String publishJob(String jobType, Object payload, Integer maxAttempts) {
        BlockchainJob job = new BlockchainJob(jobType, payload, maxAttempts);

        logger.info("Publishing blockchain job: {} with ID: {} (maxAttempts: {})",
                jobType, job.getId(), maxAttempts);

        try {
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    "blockchain." + jobType.toLowerCase(),
                    job
            );

            logger.info("Successfully published job: {}", job.getId());
            return job.getId();
        } catch (Exception e) {
            logger.error("Failed to publish job: {} - Error: {}", job.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish blockchain job", e);
        }
    }
}
