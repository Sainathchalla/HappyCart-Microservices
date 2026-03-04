package com.happycart.inventory_service;

import com.happycart.inventory_service.model.Inventory;
import com.happycart.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return (args) -> {
			inventoryRepository.save(
					Inventory.builder()
							.skuCode("iphone_17")
							.quantity(1)
							.build()
			);
			inventoryRepository.save(
					Inventory.builder()
							.skuCode("samsung_S24")
							.quantity(5)
							.build()
			);
		};
	}
}
