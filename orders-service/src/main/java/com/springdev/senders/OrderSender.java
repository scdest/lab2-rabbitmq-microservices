package com.springdev.senders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String routingKey, Object message) {
        log.info("sending message {}", message);
        rabbitTemplate.convertAndSend(routingKey, message);
    }
}
