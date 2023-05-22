package com.comicstore.apigateway.presentationlayer.tournament;

import java.util.UUID;

public class StoreIdentifier {
    private String storeId;


    public StoreIdentifier() {
        this.storeId = UUID.randomUUID().toString();
    }


    public StoreIdentifier(String storeId) {
        this.storeId = storeId;
    }



    public String getStoreId() {
        return this.storeId;
    }

}
