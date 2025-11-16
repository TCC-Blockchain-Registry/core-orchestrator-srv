package com.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 *
 * Configures queues, exchanges, bindings, and message converters for blockchain job processing
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    /**
     * Topic Exchange for blockchain operations
     */
    @Bean
    public TopicExchange blockchainExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    /**
     * Queue for blockchain jobs
     */
    @Bean
    public Queue blockchainJobsQueue() {
        return new Queue(queueName, true); // durable = true
    }

    /**
     * Binding between exchange and queue
     */
    @Bean
    public Binding binding(Queue blockchainJobsQueue, TopicExchange blockchainExchange) {
        return BindingBuilder
                .bind(blockchainJobsQueue)
                .to(blockchainExchange)
                .with(routingKey);
    }

    /**
     * JSON Message Converter for serializing/deserializing messages
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate configured with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
