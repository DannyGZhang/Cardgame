package com.comicstore.clientservice.datalayer;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
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
