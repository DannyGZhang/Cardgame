package com.comicstore.tournamentservice.presentationlayer;

import com.comicstore.tournamentservice.datalayer.Result;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreResponseModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TournamentClientCarGameResponseModel {

    private String tournamentId;
    private List<ClientResponseModel> players;
    private CardGameResponseModel cardGame;
    private StoreResponseModel location;
    private double entryCost;
    private ClientResponseModel winner;
    private List<Result> results;
}
