package com.comicstore.tournamentservice.datamapperlayer;

import com.comicstore.tournamentservice.datalayer.Result;
import com.comicstore.tournamentservice.datalayer.Tournament;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreResponseModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentClientCarGameResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TournamentClientCardGameResponseMapper {

    @Mapping(expression="java(tournament.getTournamentIdentifier().getTournamentId())", target = "tournamentId")
    @Mapping(expression="java(players)", target = "players")
    @Mapping(expression="java(winner)", target = "winner")
    @Mapping(expression="java(cardGame)", target = "cardGame")
    @Mapping(expression="java(results)", target = "results")
    @Mapping(expression="java(store)", target = "location")
    TournamentClientCarGameResponseModel entityToResponseModel(Tournament tournament, List<ClientResponseModel> players, CardGameResponseModel cardGame, ClientResponseModel winner, List<Result> results, StoreResponseModel store);


}
