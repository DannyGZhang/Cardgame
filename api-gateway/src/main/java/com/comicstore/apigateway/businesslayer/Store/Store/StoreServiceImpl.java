package com.comicstore.apigateway.businesslayer.Store.Store;

import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService{

    StoreServiceClient storeServiceClient;

    public StoreServiceImpl(StoreServiceClient storeServiceClient) {
        this.storeServiceClient = storeServiceClient;
    }

    @Override
    public StoreResponseModel[] getStores() {
        return storeServiceClient.getStores();
    }

    @Override
    public StoreResponseModel getStore(String storeId) {
        return storeServiceClient.getStoreById(storeId);
    }

    @Override
    public StoreResponseModel createStore(StoreRequestModel storeRequestModel) {
        return storeServiceClient.createNewStore(storeRequestModel);
    }

    @Override
    public void updateStore(String storeId, StoreRequestModel storeRequestModel) {
        storeServiceClient.updateStore(storeRequestModel,storeId);
    }

    @Override
    public InventoryResponseModel[] getInventoryByStoreId(String storeId) {
        return storeServiceClient.getStoreInventories(storeId);
    }

    @Override
    public void deleteStore(String storeId) {
        storeServiceClient.deleteStore(storeId);
    }
}
