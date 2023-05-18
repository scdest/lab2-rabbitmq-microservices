package com.springdev;

import com.springdev.entities.Product;
import com.springdev.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void createOrUpdateProduct_ShouldReturnOk() {

        Product product = new Product();
        product.setQuantity(4);
        product.setCategory("kovbasa");
        product.setName("Oleks");
        product.setId(UUID.randomUUID());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Product> requestEntity = new HttpEntity<>(product, headers);

        ResponseEntity<Product> response = testRestTemplate.exchange("/products", HttpMethod.POST, requestEntity, Product.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    }


    @Test
    public void findByCategory_ShouldReturnProductsByCategory() {
        Product product1 = new Product();
        product1.setQuantity(4);
        product1.setCategory("kovbasa");
        product1.setName("Oleks1");
        product1.setId(UUID.randomUUID());

        Product product2 = new Product();
        product2.setQuantity(4);
        product2.setCategory("Electronics");
        product2.setName("Oleks2");
        product2.setId(UUID.randomUUID());

        productRepository.saveAll(List.of(product1, product2));

        String url = UriComponentsBuilder.fromPath("/products")
                .queryParam("category", "Electronics")
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<List<Product>> response = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Product> products = response.getBody();
        Assertions.assertNotNull(products);
        Assertions.assertEquals(1, products.size());

        Assertions.assertEquals(product2.getName(), products.get(0).getName());
    }
}
