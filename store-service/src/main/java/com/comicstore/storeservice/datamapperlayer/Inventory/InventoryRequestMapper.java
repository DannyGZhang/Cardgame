package com.comicstore.storeservice.datamapperlayer.Inventory;

import com.comicstore.storeservice.datalayer.Inventory.Inventory;
import com.comicstore.storeservice.datalayer.Store.StoreIdentifier;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InventoryRequestMapper {
    @Mappings({
            @Mapping(target = "inventoryIdentifier", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "lastUpdated", ignore = true),
            @Mapping(expression = "java(storeIdentifier)",target = "storeIdentifier")

    })
    Inventory requestModelToEntity(InventoryRequestModel inventoryRequestModel, StoreIdentifier storeIdentifier);
}
