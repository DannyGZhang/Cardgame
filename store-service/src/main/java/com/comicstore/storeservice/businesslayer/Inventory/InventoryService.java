package com.comicstore.storeservice.businesslayer.Inventory;


import com.comicstore.storeservice.presentationlayer.Inventory.InventoryRequestModel;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryResponseModel;

import java.util.List;

public interface InventoryService {
     InventoryResponseModel createInventory(String storeId,InventoryRequestModel inventoryRequestModel);
     InventoryResponseModel updateInventory(String inventoryId,InventoryRequestModel inventoryRequestModel);
     List<InventoryResponseModel> getInventories();
     InventoryResponseModel getInventoryById(String inventoryId);

     void deleteInventory(String inventoryId);


}
