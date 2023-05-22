package com.comicstore.cardgameservice.datamapperlayer;

import com.comicstore.cardgameservice.datalayer.CardGame;
import com.comicstore.cardgameservice.presentationlayer.CardGameRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CardGameRequestMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "cardIdentifier", ignore = true)
    })
    CardGame requestModelToEntity(CardGameRequestModel cardGameRequestModel);
}
