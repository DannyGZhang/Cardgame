package com.comicstore.tournamentservice.datalayer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

@Document(collection = "tournaments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    private String id;

    CardIdentifier cardGame;
    TournamentIdentifier tournamentIdentifier;

    StoreIdentifier location;
    private List<ClientIdentifier> players;
    private double entryCost;
    private ClientIdentifier winner;
    private List<Result> results;

    public Tournament(CardIdentifier cardGame, StoreIdentifier location, List<ClientIdentifier> players, double entryCost, ClientIdentifier winner, List<Result> results) {
        this.cardGame = cardGame;
        this.tournamentIdentifier = new TournamentIdentifier();
        this.location = location;
        this.players = players;
        this.entryCost = entryCost;
        this.winner = winner;
        this.results = results;
    }
}
