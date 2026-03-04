package com.happycart.inventory_service.controller;

import com.happycart.inventory_service.dto.InventoryResponse;
import com.happycart.inventory_service.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@AllArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;


    // example request: http://localhost/api/inventory/HP Laptop
    // Limitation: Only one item is checked for existence.

    // List of sku-codes passed as a list
    // http://localhost:8082/api/inventory?skuCode=HP Laptop&skuCode=iPhone13

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}

