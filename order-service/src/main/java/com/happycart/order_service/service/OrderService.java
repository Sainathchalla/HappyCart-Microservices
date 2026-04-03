package com.happycart.order_service.service;

import com.happycart.order_service.dto.InventoryResponse;
import com.happycart.order_service.dto.OrderLineItemsRequest;
import com.happycart.order_service.dto.OrderRequest;
import com.happycart.order_service.model.Order;
import com.happycart.order_service.model.OrderLineItems;
import com.happycart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
// RequiredArgsConstructor only create the constructor for final and @NonNull fields
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemsList(
                        orderRequest.getOrderLineItemsRequestList().stream()
                                .map(this::mapToOrderLineItems)
                                .toList()
                )
                .build();

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Order is placed only when stock is present, So go to inventory service and check for stock.
        // How to communicate with other service ?
        // A) RestTemplate, WebClient and FeignClients
        // Here I am using WebClient.

        // This is a synchronous request, since we are using block at end, where the request will be waiting for the response.
        // This will block upcoming requests.
        for(String skuCode: skuCodes) {
            System.out.println("Order includes: " +skuCode);
        }

//      Eureka resolves the url and also sends requests to multiple instances of it(Load Balancing from here (Client Side Load Balancing)) : http://inventory-service/api/inventory ->
//      http://localhost:62818/api/inventory, http://localhost:54231/api/inventory etc...


        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        System.out.println(Arrays.toString(inventoryResponseArray));

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.getInStock());


        if (allProductsInStock) {
            orderRepository.save(order);
            log.info("Order saved with id: {}", order.getId());
            return "Order Place Successfully..!!";
        } else {
            throw new IllegalArgumentException("Product is not in stock, Please try again later...");
        }
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsRequest orderLineItemsRequest) {
        return OrderLineItems.builder()
                .skuCode(orderLineItemsRequest.getSkuCode())
                .price(orderLineItemsRequest.getPrice())
                .quantity(orderLineItemsRequest.getQuantity())
                .build();
    }
}