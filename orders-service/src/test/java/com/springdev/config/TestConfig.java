package com.springdev.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RabbitMQContainer rabbitMQContainer() {
        RabbitMQContainer container = new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"));
        container.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));
        container.start();
        return container;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer postgreSQLContainer() {
        PostgreSQLContainer container = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));
        container.start();
        return container;
    }

    @Bean
    public DataSource dataSource(PostgreSQLContainer postgreSQLContainer) {
        return DataSourceBuilder.create()
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory(RabbitMQContainer rabbitMQContainer) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQContainer.getHost());
        connectionFactory.setPort(rabbitMQContainer.getAmqpPort());
        connectionFactory.setUsername(rabbitMQContainer.getAdminUsername());
        connectionFactory.setPassword(rabbitMQContainer.getAdminPassword());
        return connectionFactory;
    }
}
