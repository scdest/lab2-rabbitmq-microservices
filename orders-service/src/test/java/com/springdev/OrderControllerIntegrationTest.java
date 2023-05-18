package com.springdev;

import com.springdev.entities.Order;
import com.springdev.entities.OrderItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testCreateOrUpdateOrder() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setCreatedAt(LocalDateTime.now());
        order.setOwnerId(UUID.randomUUID());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setQuantity(10);
        orderItem.setProductId(UUID.randomUUID());
        orderItem.setOrderId(order.getId());

        order.setOrderItems(List.of(orderItem));

        HttpEntity<Order> requestEntity = new HttpEntity<>(order, headers);

        ResponseEntity<Order> responseEntity = testRestTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                requestEntity,
                Order.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
