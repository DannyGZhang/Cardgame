package com.comicstore.storeservice.businesslayer.Store;

import com.comicstore.storeservice.presentationlayer.Inventory.InventoryResponseModel;
import com.comicstore.storeservice.presentationlayer.Store.StoreRequestModel;
import com.comicstore.storeservice.presentationlayer.Store.StoreResponseModel;

import java.util.List;

public interface StoreService {
    List<StoreResponseModel> getStores();
    StoreResponseModel getStore(String storeId);
    StoreResponseModel createStore(StoreRequestModel storeRequestModel);
    StoreResponseModel updateStore(String storeId,StoreRequestModel storeRequestModel);
    List<InventoryResponseModel> getInventoryByStoreId(String storeId);

    void deleteStore(String storeId);
}
