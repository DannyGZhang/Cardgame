package com.comicstore.tournamentservice.presentationlayer;


import com.comicstore.tournamentservice.datalayer.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TournamentRequestModel {
    @NotBlank
    private String cardGame;
    @NotBlank
    private String location;
    @NotNull
    private double entryCost;
    @NotBlank
    private String winner;

    @NotNull
    private List<Result> results;

}
