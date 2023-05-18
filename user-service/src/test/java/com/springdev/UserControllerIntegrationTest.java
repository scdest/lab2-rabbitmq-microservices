package com.springdev;

import com.springdev.entities.Order;
import com.springdev.entities.OrderItem;
import com.springdev.entities.User;
import com.springdev.repositories.OrderRepository;
import com.springdev.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testCreateUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        User user = new User();
        user.setEmail("mail");
        user.setName("Oleks");
        user.setId(UUID.randomUUID());

        ResponseEntity<User> responseEntity = testRestTemplate.postForEntity(
                "/users",
                new HttpEntity<>(user, headers),
                User.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testGetOrdersByUserId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        User user = new User();
        user.setEmail("mail");
        user.setName("Oleks");
        user.setId(UUID.randomUUID());

        user = userRepository.saveAndFlush(user);

        Order order1 = createOrderWithItems();
        order1.setOwnerId(user.getId());
        Order order2 = createOrderWithItems();

        orderRepository.saveAll(List.of(order2, order1));

        ResponseEntity<List<Order>> response = testRestTemplate.exchange(
                "/users/" + user.getId() + "/orders",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private Order createOrderWithItems() {
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
        return order;
    }
}
