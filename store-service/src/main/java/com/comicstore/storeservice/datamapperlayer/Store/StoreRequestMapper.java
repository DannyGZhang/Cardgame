package com.comicstore.storeservice.datamapperlayer.Store;


import com.comicstore.storeservice.datalayer.Store.Store;
import com.comicstore.storeservice.presentationlayer.Store.StoreRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring")
public interface StoreRequestMapper {




    @Mappings({
            @Mapping(target = "storeIdentifier", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "contact", ignore = true)
    })
    Store requestModelToEntity(StoreRequestModel storeRequestModel);

}
