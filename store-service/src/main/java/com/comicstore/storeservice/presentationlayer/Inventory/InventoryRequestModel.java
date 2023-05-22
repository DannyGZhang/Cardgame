package com.comicstore.storeservice.presentationlayer.Inventory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryRequestModel {
    private String storeId;
    private String type;
    private String status;
}
