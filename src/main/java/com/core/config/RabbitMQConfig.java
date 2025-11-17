package com.core.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Configures queues, message converter, and RabbitTemplate for blockchain job publishing
 */
@Configuration
public class RabbitMQConfig {
    
    public static final String BLOCKCHAIN_JOBS_QUEUE = "blockchain-jobs";
    public static final String BLOCKCHAIN_JOBS_DLQ = "blockchain-jobs-dlq";
    
    /**
     * Main blockchain jobs queue
     * Durable queue for persistent messages
     */
    @Bean
    public Queue blockchainJobsQueue() {
        return QueueBuilder.durable(BLOCKCHAIN_JOBS_QUEUE)
                .build();
    }
    
    /**
     * Dead Letter Queue for failed jobs
     */
    @Bean
    public Queue blockchainJobsDLQ() {
        return QueueBuilder.durable(BLOCKCHAIN_JOBS_DLQ)
                .build();
    }
    
    /**
     * JSON Message Converter
     * Converts Java objects to JSON for RabbitMQ messages
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate with JSON converter
     * Used to send messages to RabbitMQ
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

