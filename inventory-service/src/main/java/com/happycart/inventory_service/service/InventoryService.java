package com.happycart.inventory_service.service;

import com.happycart.inventory_service.dto.InventoryResponse;
import com.happycart.inventory_service.model.Inventory;
import com.happycart.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {

        // skuCodes -> A, B, C
        // Inventory -> A
        System.out.println("Order List Came, Will see the stock..");
        List<Inventory> list = inventoryRepository.findBySkuCodeIn(skuCodes);

        Map<String, Boolean> lookUpMap = list.stream().collect(
                Collectors.toMap(
                        Inventory::getSkuCode,
                        inventory -> inventory.getQuantity() > 0
                )
        );

        return skuCodes.stream().map(
                skuCode -> InventoryResponse.builder()
                        .skuCode(skuCode)
                        .inStock(lookUpMap.getOrDefault(skuCode, false))
                        .build()).toList();
    }
}
