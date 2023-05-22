package com.comicstore.clientservice.datamapperlayer;

import com.comicstore.clientservice.datalayer.Client;
import com.comicstore.clientservice.datalayer.StoreIdentifier;
import com.comicstore.clientservice.presentationlayer.ClientRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientRequestMapper {

//    @Mappings({
//            @Mapping(target = "clientIdentifier", ignore = true),
//            @Mapping(target = "id", ignore = true),
//            @Mapping(target = "contact", ignore = true),
//            @Mapping(target = "rebate", ignore = true),
//            @Mapping(expression = "java(storeIdentifier)",target = "storeIdentifier")
//    })
//    Client requestModelToEntity(ClientRequestModel clientRequestModel, StoreIdentifier storeIdentifier);
}
