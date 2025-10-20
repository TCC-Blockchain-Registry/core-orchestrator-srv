package com.core.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hibernate Configuration
 * Pure Hibernate without Spring Data JPA
 * Supports both PostgreSQL (production) and H2 (tests)
 */
@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = "com.core")
public class HibernateConfig {
    
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    
    @Value("${spring.jpa.database-platform:org.hibernate.dialect.PostgreSQLDialect}")
    private String dialect;
    
    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;
    
    @Bean
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration();
        
        // Database connection properties (dynamic based on profile)
        configuration.setProperty("hibernate.connection.driver_class", driverClassName);
        configuration.setProperty("hibernate.connection.url", url);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
        
        // Hibernate properties (dynamic based on profile)
        configuration.setProperty("hibernate.dialect", dialect);
        configuration.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        
        // Add annotated entity classes
        configuration.addAnnotatedClass(com.core.adapter.output.persistence.user.entity.UserEntity.class);
        configuration.addAnnotatedClass(com.core.adapter.output.persistence.property.entity.PropertyEntity.class);
        configuration.addAnnotatedClass(com.core.adapter.output.persistence.property.entity.PropertyTransferEntity.class);
        configuration.addAnnotatedClass(com.core.adapter.output.persistence.property.entity.PropertyTransferApprovalEntity.class);
        
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        
        return configuration.buildSessionFactory(builder.build());
    }
}
