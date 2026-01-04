package com.happycart.order_service.service;

import com.happycart.order_service.dto.OrderLineItemsRequest;
import com.happycart.order_service.dto.OrderRequest;
import com.happycart.order_service.model.Order;
import com.happycart.order_service.model.OrderLineItems;
import com.happycart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
// RequiredArgsConstructor only create the constructor for final and @NonNull fields
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public void createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemsList(
                        orderRequest.getOrderLineItemsRequestList().stream()
                                .map(this::mapToOrderLineItems)
                                .toList()
                )
                .build();

        orderRepository.save(order);
        log.info("Order saved with id: {}", order.getId());
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsRequest orderLineItemsRequest) {
        return OrderLineItems.builder()
                .skuCode(orderLineItemsRequest.getSkuCode())
                .price(orderLineItemsRequest.getPrice())
                .quantity(orderLineItemsRequest.getQuantity())
                .build();
    }
}