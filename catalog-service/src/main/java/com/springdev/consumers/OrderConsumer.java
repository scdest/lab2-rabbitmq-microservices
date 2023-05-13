package com.springdev.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdev.dto.Order;
import com.springdev.entities.Product;
import com.springdev.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;

    @RabbitListener(queues = "new-order-catalog")
    public void receiveMessage(String message) {
        try {
            log.info("Message received {}", message);
            Order newOrder = objectMapper.readValue(message, Order.class);
            newOrder.getOrderItems().forEach(orderItem -> {
                Product product = productRepository.findById(orderItem.getProductId()).orElseThrow();
                product.setQuantity(product.getQuantity() - orderItem.getQuantity());
                productRepository.save(product);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
