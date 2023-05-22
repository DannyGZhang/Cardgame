package com.comicstore.cardgameservice.datamapperlayer;

import com.comicstore.cardgameservice.datalayer.CardIdentifier;
import com.comicstore.cardgameservice.datalayer.Set;
import com.comicstore.cardgameservice.presentationlayer.SetRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SetRequestMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "setIdentifier", ignore = true),
            @Mapping(expression = "java(cardIdentifier)",target = "cardIdentifier")
    })
    Set entityToResponseModel(SetRequestModel set, CardIdentifier cardIdentifier);

}
