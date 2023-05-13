package com.springdev.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdev.entities.Order;
import com.springdev.repositories.OrderRepository;
import com.springdev.senders.OrderSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String ROUTING_KEY_CATALOG = "new-order-catalog";

    private static final String ROUTING_KEY_USER = "new-order-user";

    private final OrderRepository orderRepository;
    private final OrderSender orderSender;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Order> createOrUpdateOrder(@RequestBody Order order) throws JsonProcessingException {
        Order savedOrder = orderRepository.save(order);
        orderSender.sendMessage(ROUTING_KEY_CATALOG, objectMapper.writeValueAsString(savedOrder));
        orderSender.sendMessage(ROUTING_KEY_USER, objectMapper.writeValueAsString(savedOrder));
        return ResponseEntity.ok(savedOrder);
    }
}
