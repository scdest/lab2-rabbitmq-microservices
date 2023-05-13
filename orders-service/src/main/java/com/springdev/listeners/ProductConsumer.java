package com.springdev.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdev.entities.Product;
import com.springdev.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProductConsumer {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    @Autowired
    public ProductConsumer(ObjectMapper objectMapper, ProductRepository productRepository) {
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
    }

    @RabbitListener(queues = "new-product")
    public void receiveMessage(String message) {
        try {
            log.info("Message received {}", message);
            Product product = objectMapper.readValue(message, Product.class);
            productRepository.save(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

