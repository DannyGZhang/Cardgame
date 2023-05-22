package com.comicstore.apigateway.presentationlayer.tournament;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class Result {

    private ClientIdentifier clientId;

    private int victories;

    private int defeats;

    private int draws;

    public Result() {
    }

    public @NotNull String getClientId() {
        return clientId.getClientId();
    }

    public @NotNull int getVictories() {
        return victories;
    }

    public @NotNull int getDefeats() {
        return defeats;
    }

    public @NotNull int getDraws() {
        return draws;
    }

    public Result(@NotNull ClientIdentifier clientIdentifier, @NotNull int victories, @NotNull int defeats, @NotNull int draws) {
        Objects.requireNonNull(this.clientId = clientIdentifier);
        Objects.requireNonNull(this.victories = victories);
        Objects.requireNonNull(this.defeats = defeats);
        Objects.requireNonNull(this.draws = draws);


    }
}
