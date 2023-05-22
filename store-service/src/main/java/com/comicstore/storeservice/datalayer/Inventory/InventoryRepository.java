package com.comicstore.storeservice.datalayer.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
Inventory getInventoryByInventoryIdentifier_InventoryId(String inventoryId);

List<Inventory> getInventoriesByStoreIdentifier_StoreId(String storeId);

Boolean existsByInventoryIdentifier_InventoryId(String inventoryId);


}
