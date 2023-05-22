package com.comicstore.tournamentservice.datalayer;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TournamentRepository extends MongoRepository<Tournament, Integer> {
    Tournament getTournamentByTournamentIdentifier_TournamentId(String tournamentId);

    List<Tournament> getTournamentsByPlayers(ClientIdentifier playerId);

    List<Tournament> getTournamentsByWinner(ClientIdentifier playerId);
List<Tournament> getTournamentsByCardGame_CardIdAndPlayers(String cardGameId, ClientIdentifier playerId);
    List<Tournament> getTournamentsByCardGame_CardId(String cardGameId);
}
