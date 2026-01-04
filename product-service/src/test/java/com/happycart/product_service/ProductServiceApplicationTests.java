package com.happycart.product_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happycart.product_service.dto.ProductRequest;
import com.happycart.product_service.dto.ProductResponse;
import com.happycart.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        log.info("URL for MongoDB : {}", mongoDBContainer.getReplicaSetUrl());
    }

    @BeforeEach
    void emptyDatabase() {
        log.info("Clearing DB Before Test Case");
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = fanRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetProducts() throws Exception {
		ProductRequest productRequest1 = fanRequest();
		String productRequestString1 = objectMapper.writeValueAsString(productRequest1);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString1))
				.andExpect(status().isCreated());

        ProductRequest productRequest2 = tvRequest();
        String productRequestString2 = objectMapper.writeValueAsString(productRequest2);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString2))
                .andExpect(status().isCreated());


        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

		String resultResponse = resultActions.andReturn().getResponse().getContentAsString();
		List<ProductResponse> products = objectMapper.readValue(resultResponse, new TypeReference<>() {
        });
		log.info("Products : {}", products);
        assertEquals(2, products.size());

        // should use set for best comparison
        List<String> list = products.stream().map(ProductResponse::getName).toList();
        assertEquals(List.of("Fan", "TV"), list);
    }

    private ProductRequest fanRequest() {
        return ProductRequest.builder()
                .name("Fan")
                .description("Table Fan")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

    private ProductRequest tvRequest() {
        return ProductRequest.builder()
                .name("TV")
                .description("Samsung TV")
                .price(BigDecimal.valueOf(3200))
                .build();
    }
}