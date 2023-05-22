package com.comicstore.tournamentservice.datalayer;


import java.util.UUID;

public class TournamentIdentifier {
    private String tournamentId;

    public TournamentIdentifier() {
        this.tournamentId = UUID.randomUUID().toString();
    }

    public TournamentIdentifier(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentId() {
        return this.tournamentId;
    }
}
