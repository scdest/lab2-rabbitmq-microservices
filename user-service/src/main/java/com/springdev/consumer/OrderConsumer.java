package com.springdev.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdev.entities.Order;
import com.springdev.repositories.OrderRepository;
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

    private final OrderRepository orderRepository;

    @RabbitListener(queues = "new-order-user")
    public void receiveMessage(String message) {
        try {
            log.info("Message received {}", message);
            Order newOrder = objectMapper.readValue(message, Order.class);
            orderRepository.save(newOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
