package com.happycart.order_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class InventoryResponse {
    private String skuCode;
    private Boolean inStock;
}
