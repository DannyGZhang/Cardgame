package com.comicstore.tournamentservice.datamapperlayer;

import com.comicstore.tournamentservice.datalayer.Tournament;
import com.comicstore.tournamentservice.presentationlayer.TournamentResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TournamentResponseMapper {

    @Mapping(expression="java(tournament.getTournamentIdentifier().getTournamentId())", target = "tournamentId")
    @Mapping(expression="java(tournament.getWinner().getClientId())", target = "winner")
    @Mapping(expression="java(tournament.getCardGame().getCardId())", target = "cardGame")
    @Mapping(expression="java(tournament.getLocation().getStoreId())", target = "location")
    TournamentResponseModel entityToResponseModel(Tournament tournament);

    @Mapping(expression="java(results)", target = "results")
    List<TournamentResponseModel> entitiesToResponseModel(List<Tournament> tournaments);


}



/*
  @Mapping(expression = "java(client.getId())",  target = "winner.id")
    @Mapping(expression = "java(client.getClientIdentifier().getClientId())",  target = "clientId")
    @Mapping(expression = "java(client.getStoreIdentifier())",  target = "storeIdentifier")
    @Mapping(expression = "java(client.getFirstName())",  target = "firstName")
    @Mapping(expression = "java(client.getLastName())",  target = "lastName")
    @Mapping(expression = "java(client.getRebate())",  target = "rebate")
    @Mapping(expression = "java(client.getTotalBought)",  target = "totalBought")
    @Mapping(expression = "java(client.getContact())", target = "contact" )
 */