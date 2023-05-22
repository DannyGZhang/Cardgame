package com.comicstore.tournamentservice.presentationlayer;

import com.comicstore.tournamentservice.datalayer.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TournamentResponseModel {
    private String tournamentId;

    private String location;
    private String cardGame;

    private double entryCost;
    private String winner;
    private List<Result> results;
}
