package com.comicstore.apigateway.businesslayer.Store.Inventory;

import com.comicstore.apigateway.domainclientlayer.Store.InventoryServiceClient;
import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService{

    InventoryServiceClient inventoryServiceClient;
    StoreServiceClient storeServiceClient;

    public InventoryServiceImpl(InventoryServiceClient inventoryServiceClient, StoreServiceClient storeServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.storeServiceClient = storeServiceClient;
    }

    @Override
    public InventoryResponseModel createInventory(String storeId, InventoryRequestModel inventoryRequestModel) {
        return storeServiceClient.createNewInventory(inventoryRequestModel,storeId);
    }

    @Override
    public void updateInventory(String inventoryId, InventoryRequestModel inventoryRequestModel) {
        inventoryServiceClient.updateInventory(inventoryRequestModel,inventoryId);
    }


    @Override
    public InventoryResponseModel getInventoryById(String inventoryId) {
        return inventoryServiceClient.getInventoryById(inventoryId);
    }

    @Override
    public void deleteInventory(String inventoryId) {
        inventoryServiceClient.deleteInventory(inventoryId);
    }
}
