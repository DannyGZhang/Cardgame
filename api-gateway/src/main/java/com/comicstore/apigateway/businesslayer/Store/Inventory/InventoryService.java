package com.comicstore.apigateway.businesslayer.Store.Inventory;


import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;

import java.util.List;

public interface InventoryService {
     InventoryResponseModel createInventory(String storeId, InventoryRequestModel inventoryRequestModel);
     void updateInventory(String inventoryId,InventoryRequestModel inventoryRequestModel);
     InventoryResponseModel getInventoryById(String inventoryId);

     void deleteInventory(String inventoryId);


}
