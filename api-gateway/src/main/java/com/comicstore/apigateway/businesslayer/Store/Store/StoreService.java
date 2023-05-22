package com.comicstore.apigateway.businesslayer.Store.Store;

import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;

import java.util.List;

public interface StoreService {
    StoreResponseModel[] getStores();
    StoreResponseModel getStore(String storeId);
    StoreResponseModel createStore(StoreRequestModel storeRequestModel);
    void updateStore(String storeId,StoreRequestModel storeRequestModel);
    InventoryResponseModel[] getInventoryByStoreId(String storeId);

    void deleteStore(String storeId);
}
