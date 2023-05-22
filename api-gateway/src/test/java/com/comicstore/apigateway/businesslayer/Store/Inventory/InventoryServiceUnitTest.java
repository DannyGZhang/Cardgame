package com.comicstore.apigateway.businesslayer.Store.Inventory;

import com.comicstore.apigateway.domainclientlayer.Store.InventoryServiceClient;
import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryServiceUnitTest {

    @Autowired
    InventoryService inventoryService;

    @MockBean
    InventoryServiceClient inventoryServiceClient;

    @MockBean
    StoreServiceClient storeServiceClient;


    @Test
    void createInventory_ShouldReturnCreatedInventory() {
        InventoryResponseModel inventoryResponseModel = new InventoryResponseModel("2be6ab80-6693-404d-beed-66850aa1e7cf","1b5fb4a0-8761-47a6-bacb-ab3c99f8c480", LocalDate.now(),"IN_STORE","OPEN");

        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel.getStoreId())
                .status(inventoryResponseModel.getStatus())
                .type(inventoryResponseModel.getType())
                .build();

        when(storeServiceClient.createNewInventory(inventoryRequestModel,inventoryResponseModel.getStoreId()))
                .thenReturn(inventoryResponseModel);

        InventoryResponseModel returned = inventoryService.createInventory(inventoryResponseModel.getStoreId(),inventoryRequestModel);

        MatcherAssert.assertThat(inventoryResponseModel, samePropertyValuesAs(returned));

    }

    @Test
    void updateInventoryWithInvalidId_ShouldReturnNotFoundException() {
        InventoryResponseModel inventoryResponseModel = new InventoryResponseModel("2be6ab80-6693-404d-beed-66850aa1e7cf","1b5fb4a0-8761-47a6-bacb-ab3c99f8c480", LocalDate.now(),"IN_STORE","OPEN");

        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel.getStoreId())
                .status(inventoryResponseModel.getStatus())
                .type(inventoryResponseModel.getType())
                .build();
        doThrow(new NotFoundException()).when(inventoryServiceClient).updateInventory(inventoryRequestModel, inventoryResponseModel.getInventoryId());


        assertThrows(NotFoundException.class, () -> {
            inventoryService.updateInventory(inventoryResponseModel.getInventoryId(), inventoryRequestModel);
        });

    }
}