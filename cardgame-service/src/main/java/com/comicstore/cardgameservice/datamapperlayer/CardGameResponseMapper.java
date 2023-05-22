package com.comicstore.cardgameservice.datamapperlayer;

import com.comicstore.cardgameservice.datalayer.CardGame;
import com.comicstore.cardgameservice.datalayer.Set;
import com.comicstore.cardgameservice.presentationlayer.CardGameController;
import com.comicstore.cardgameservice.presentationlayer.CardGameResponseModel;
import com.comicstore.cardgameservice.presentationlayer.SetResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface CardGameResponseMapper {
    @Mapping(expression="java(cardGame.getCardIdentifier().getCardId())", target = "cardId")
    CardGameResponseModel entityToResponseModel(CardGame cardGame);

    List<CardGameResponseModel> entitiesToResponseModel(List<CardGame> cardGames);


}
