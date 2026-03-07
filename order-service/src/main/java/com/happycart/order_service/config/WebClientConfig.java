package com.happycart.order_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Use of LoadBalanced Annotation -> Client side load balancing, and It will intercept the requests from order service and finds the endpoint of inventory service.
    // If not used, then inventory-service(used in url to hit), will be intercepted by DNS, and we won't find it.
    @Bean
    @LoadBalanced
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }
}
