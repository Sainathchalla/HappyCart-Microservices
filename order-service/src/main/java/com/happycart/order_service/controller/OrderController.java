package com.happycart.order_service.controller;

import com.happycart.order_service.dto.OrderRequest;
import com.happycart.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

//    Retry(CircuitBreaker(TimeLimiter))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory", fallbackMethod = "fallBackMethod")
    public CompletableFuture<String> createOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.createOrder(orderRequest));
    }

    public CompletableFuture<String> fallBackMethod(OrderRequest orderRequest, Throwable throwable) {
        return CompletableFuture.supplyAsync(() -> "Oops! Order Cannot be placed, please try after some time!");
    }
}
