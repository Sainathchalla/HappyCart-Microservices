package com.happycart.inventory_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class InventoryResponse {
    private String skuCode;
    private Boolean inStock;
}
