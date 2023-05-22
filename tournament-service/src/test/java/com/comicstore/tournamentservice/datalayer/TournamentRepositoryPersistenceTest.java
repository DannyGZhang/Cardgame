package com.comicstore.tournamentservice.datalayer;

import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
@DataMongoTest
class TournamentRepositoryPersistenceTest {
    @Autowired
    private TournamentRepository tournamentRepository;

    private Tournament presavedTournament;

    private final String VALID_PLAYER_WINNER_ID = "c80690a8-746d-4d23-817c-235579e516b3";
    private final String VALID_PLAYER_ID = "b5521e15-ee99-4862-af8d-75e761a964f2";
    private final String VALID_CARD_ID = "6dfc9e76-1aa7-4786-8398-f4ae25737324";

    @BeforeEach
    public void setup(){
        tournamentRepository.deleteAll();
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("e42a6c05-e304-4402-ac1a-e118d2ff278e");
        ClientIdentifier clientIdentifier4 = new ClientIdentifier("9d11ccd6-f518-47e0-aad5-4d75530dd53b");
        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();
        List<ClientIdentifier> players = new ArrayList<>();
        double entryCost = 6.00;
        players.add(clientIdentifier1);
        players.add(clientIdentifier2);
        players.add(clientIdentifier3);
        players.add(clientIdentifier4);
        results.add(new Result(clientIdentifier1,1,3,0));
        results.add(new Result(clientIdentifier2,3,1,0));
        results.add(new Result(clientIdentifier3,1,2,1));
        results.add(new Result(clientIdentifier4,0,4,0));

        presavedTournament = tournamentRepository.save(
                new Tournament(cardIdentifier,storeIdentifier,players,entryCost,winner,results));

    }
    @Test
    void getTournamentByTournamentIdentifier_ShouldReturnOneTournament() {
        Tournament found = tournamentRepository.getTournamentByTournamentIdentifier_TournamentId(presavedTournament.getTournamentIdentifier().getTournamentId());
        assertEquals(presavedTournament.getTournamentIdentifier().getTournamentId(),found.tournamentIdentifier.getTournamentId());
        assertEquals(presavedTournament.getLocation().getStoreId(),found.getLocation().getStoreId());
        assertEquals(presavedTournament.getCardGame().getCardId(),found.getCardGame().getCardId());
        assertEquals(presavedTournament.getEntryCost(),found.getEntryCost());
        assertThat(presavedTournament.getResults(), samePropertyValuesAs(found.getResults()));
        assertEquals(presavedTournament.getPlayers().get(0).getClientId(),found.getPlayers().get(0).getClientId());
        assertEquals(presavedTournament.getPlayers().get(1).getClientId(),found.getPlayers().get(1).getClientId());
        assertEquals(presavedTournament.getPlayers().get(2).getClientId(),found.getPlayers().get(2).getClientId());
        assertEquals(presavedTournament.getPlayers().get(3).getClientId(),found.getPlayers().get(3).getClientId());


    }

    @Test
    void getTournamentsByPlayers_ShouldReturnAllTournamentsWherePlayerIdIsFound() {
        Integer expectedNum = 1;
        List<Tournament> found = tournamentRepository.getTournamentsByPlayers(new ClientIdentifier(VALID_PLAYER_ID));
        assertEquals(expectedNum,found.size());
    }

    @Test
    void getTournamentsByWinner_ShouldReturnAllTournamentsWhereIdIsWinner() {
        Integer expectedNum = 1;
        List<Tournament> found = tournamentRepository.getTournamentsByWinner(new ClientIdentifier(VALID_PLAYER_ID));
        assertEquals(0,found.size());

        found = tournamentRepository.getTournamentsByWinner(new ClientIdentifier(VALID_PLAYER_WINNER_ID));
        assertEquals(expectedNum,found.size());

    }

    @Test
    void getTournamentsByCardGame_CardIdAndPlayers_ShouldReturnAllTournamentsWhereCardGameIdAndPlayerIdIsFound() {
        Integer expectedNum = 1;
        List<Tournament> found = tournamentRepository.getTournamentsByCardGame_CardIdAndPlayers(VALID_CARD_ID,new ClientIdentifier(VALID_PLAYER_ID));
        assertEquals(expectedNum,found.size());
    }

    @Test
    void getTournamentsByCardGame_CardId_ShouldReturnAllTournamentsWhereCardGameIdIsFound() {
        Integer expectedNum = 1;
        List<Tournament> found = tournamentRepository.getTournamentsByCardGame_CardId(VALID_CARD_ID);
        assertEquals(expectedNum,found.size());
    }
}