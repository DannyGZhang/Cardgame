package com.comicstore.tournamentservice.datamapperlayer;

import com.comicstore.tournamentservice.datalayer.CardIdentifier;
import com.comicstore.tournamentservice.datalayer.ClientIdentifier;
import com.comicstore.tournamentservice.datalayer.Tournament;
import com.comicstore.tournamentservice.presentationlayer.TournamentRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TournamentRequestMapper {


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "tournamentIdentifier", ignore = true),
            @Mapping(target = "players", ignore = true),
            @Mapping(target = "location", ignore = true),
            @Mapping(expression = "java(cardIdentifier)",target = "cardGame"),
            @Mapping(expression = "java(clientIdentifier)",target = "winner")
    })
    Tournament requestModelToEntity(TournamentRequestModel tournamentRequestModel, CardIdentifier cardIdentifier, ClientIdentifier clientIdentifier);


}
