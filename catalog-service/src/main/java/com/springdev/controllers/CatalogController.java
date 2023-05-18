package com.springdev.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdev.entities.Product;
import com.springdev.repositories.ProductRepository;
import com.springdev.sender.RabbitMQSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogController {

    private static final String ROUTING_KEY = "new-product";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitMQSender sender;

    @Operation(summary = "Create or update a product", description = "Creates or updates a product in the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created or updated successfully.",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)) })
    })
    @PostMapping
    public ResponseEntity<Product> createOrUpdateProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product object to be created or updated.", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))) @RequestBody Product product) throws JsonProcessingException {
        Product savedProduct = productRepository.save(product);
        sender.sendMessage(ROUTING_KEY, objectMapper.writeValueAsString(savedProduct));
        return ResponseEntity.ok(savedProduct);
    }

    @Operation(summary = "Find products by category", description = "Returns a list of products matching the specified category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products matching the category.",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class))) })
    })
    @GetMapping
    public ResponseEntity<List<Product>> findByCategory(
            @Parameter(description = "Category of the products to be retrieved.", required = true, example = "Electronics") @RequestParam String category) {
        return ResponseEntity.ok(productRepository.findByCategory(category));
    }
}
