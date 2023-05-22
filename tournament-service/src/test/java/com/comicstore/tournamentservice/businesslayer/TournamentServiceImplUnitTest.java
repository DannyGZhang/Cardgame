package com.comicstore.tournamentservice.businesslayer;

import com.comicstore.tournamentservice.datalayer.*;
import com.comicstore.tournamentservice.datamapperlayer.TournamentClientCardGameResponseMapper;
import com.comicstore.tournamentservice.datamapperlayer.TournamentRequestMapper;
import com.comicstore.tournamentservice.datamapperlayer.TournamentResponseMapper;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameServiceClient;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.tournamentservice.presentationlayer.TournamentClientCarGameResponseModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentRequestModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentResponseModel;
import com.comicstore.tournamentservice.utils.exceptions.InvalidInputException;
import com.comicstore.tournamentservice.utils.exceptions.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@Slf4j
@SpringBootTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
class TournamentServiceImplUnitTest {
    @Autowired
    TournamentService tournamentService;

    @MockBean
    ClientServiceClient clientServiceClient;

    @MockBean
    StoreServiceClient storeServiceClient;

    @MockBean
    CardGameServiceClient cardGameServiceClient;

    @MockBean
    TournamentRepository tournamentRepository;

    @SpyBean
    TournamentResponseMapper tournamentResponseMapper;
    TournamentRequestMapper tournamentRequestMapper;
    @SpyBean
    TournamentClientCardGameResponseMapper tournamentClientCardGameResponseMapper;



    @Test
    public void createNewTournament_ShouldSucceed(){
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = 5.5;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));
        results.add(new Result(clientIdentifier3, 2, 1, 1));

        TournamentRequestModel tournamentRequestModel = TournamentRequestModel.builder()
                .cardGame(cardIdentifier.getCardId())
                .entryCost(entryCost)
                .location(storeIdentifier.getStoreId())
                .winner(winner.getClientId())
                .results(results)
                .build();


        ClientResponseModel player1Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier1.getClientId(),
                "Pauline",
                "Grishmanov",
                20,
                3416.77, "pgrishmanov1r@tinyurl.com",
                "979-767-5807");


        ClientResponseModel player2Response = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                clientIdentifier2.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        ClientResponseModel player3Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier3.getClientId(),
                "Leodora",
                "Quodling", 20,
                2543.3, "lquodling4t@latimes.com",
                "229-316-8771");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);


        Tournament tournament = buildTournament1();
        Tournament saved = buildTournament1();
        saved.setId("0001");

        when(clientServiceClient.getClientAggregateById(clientIdentifier1.getClientId())).thenReturn(player1Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier2.getClientId())).thenReturn(player2Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier3.getClientId())).thenReturn(player3Response);
        when(storeServiceClient.getStoreById(storeIdentifier.getStoreId())).thenReturn(locationResponse);
        when(clientServiceClient.getClientAggregateById(winner.getClientId())).thenReturn(winnerResponse);
        when(cardGameServiceClient.getCardGameById(cardIdentifier.getCardId())).thenReturn(cardGameResponseModel);
        when(tournamentRepository.save(tournament)).thenReturn(saved);

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(saved);

        //act
        TournamentClientCarGameResponseModel tournamentClientCarGameResponseModel = tournamentService.createTournament(tournamentRequestModel);


        assertNotNull(tournamentClientCarGameResponseModel);
        MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(tournamentClientCarGameResponseModel.getCardGame()));
        MatcherAssert.assertThat(locationResponse, samePropertyValuesAs(tournamentClientCarGameResponseModel.getLocation()));
        MatcherAssert.assertThat(winnerResponse, samePropertyValuesAs(tournamentClientCarGameResponseModel.getWinner()));

    }
    @Test
    public void createNewTournamentWithInvalidEntryCost_ShouldThrownInvalidInputException(){
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = -5.5;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));
        results.add(new Result(clientIdentifier3, 2, 1, 1));

        TournamentRequestModel tournamentRequestModel = TournamentRequestModel.builder()
                .cardGame(cardIdentifier.getCardId())
                .entryCost(entryCost)
                .location(storeIdentifier.getStoreId())
                .winner(winner.getClientId())
                .results(results)
                .build();

        try {
            tournamentService.createTournament(tournamentRequestModel);
        }catch (InvalidInputException ex) {
            assertNotNull(ex);
            assertEquals("Entry cost is negative !",ex.getMessage());
        }



    }


    @Test
    public void createNewTournamentWithInvalidCardGameId_ShouldNotFoundException(){
        CardIdentifier cardIdentifier = new CardIdentifier("1");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = 5.5;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));
        results.add(new Result(clientIdentifier3, 2, 1, 1));

        TournamentRequestModel tournamentRequestModel = TournamentRequestModel.builder()
                .cardGame(cardIdentifier.getCardId())
                .entryCost(entryCost)
                .location(storeIdentifier.getStoreId())
                .winner(winner.getClientId())
                .results(results)
                .build();

        ClientResponseModel player1Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier1.getClientId(),
                "Pauline",
                "Grishmanov",
                20,
                3416.77, "pgrishmanov1r@tinyurl.com",
                "979-767-5807");


        ClientResponseModel player2Response = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                clientIdentifier2.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        ClientResponseModel player3Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier3.getClientId(),
                "Leodora",
                "Quodling", 20,
                2543.3, "lquodling4t@latimes.com",
                "229-316-8771");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");





        when(clientServiceClient.getClientAggregateById(clientIdentifier1.getClientId())).thenReturn(player1Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier2.getClientId())).thenReturn(player2Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier3.getClientId())).thenReturn(player3Response);
        when(storeServiceClient.getStoreById(storeIdentifier.getStoreId())).thenReturn(locationResponse);
        when(clientServiceClient.getClientAggregateById(winner.getClientId())).thenReturn(winnerResponse);
        when(cardGameServiceClient.getCardGameById(cardIdentifier.getCardId())).thenThrow(new NotFoundException("Card game does not exist with ID : 1"));



        try {
            tournamentService.createTournament(tournamentRequestModel);
        }catch (NotFoundException ex) {
            assertNotNull(ex);
            assertEquals("Card game does not exist with ID : " + tournamentRequestModel.getCardGame(),ex.getMessage());
        }



    }





    private Tournament buildTournament1() {
        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = 5.5;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));
        results.add(new Result(clientIdentifier3, 2, 1, 1));
        List<ClientIdentifier> players = new ArrayList<>();

        players.add(clientIdentifier1);
        players.add(clientIdentifier2);
        players.add(clientIdentifier3);


        var tournament = Tournament.builder()
                .tournamentIdentifier(tournamentIdentifier)
                .players(players)
                .location(storeIdentifier)
                .winner(winner)
                .entryCost(entryCost)
                .cardGame(cardIdentifier)
                .results(results)
                .build();
        return tournament;

    }

    @Test
    public void updateTournament_ShouldSucceed(){
        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = 5.5;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 2, 2, 0));
        results.add(new Result(clientIdentifier3, 2, 1, 1));


        List<Result> resultsUpdate = new ArrayList<>();

        resultsUpdate.add(new Result(clientIdentifier1, 1, 3, 0));
        resultsUpdate.add(new Result(clientIdentifier2, 2, 5, 0));
        resultsUpdate.add(new Result(clientIdentifier3, 2, 1, 1));

        TournamentRequestModel tournamentRequestModel = TournamentRequestModel.builder()
                .cardGame(cardIdentifier.getCardId())
                .entryCost(entryCost)
                .location(storeIdentifier.getStoreId())
                .winner(winner.getClientId())
                .results(resultsUpdate)
                .build();


        ClientResponseModel player1Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier1.getClientId(),
                "Pauline",
                "Grishmanov",
                20,
                3416.77, "pgrishmanov1r@tinyurl.com",
                "979-767-5807");


        ClientResponseModel player2Response = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                clientIdentifier2.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        ClientResponseModel player3Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier3.getClientId(),
                "Leodora",
                "Quodling", 20,
                2543.3, "lquodling4t@latimes.com",
                "229-316-8771");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);


        Tournament tournament = buildTournament1();
        tournament.setResults(resultsUpdate);
        Tournament saved = buildTournament1();
        saved.setId("0001");


        Tournament updated = saved;
        updated.setResults(resultsUpdate);


        when(clientServiceClient.getClientAggregateById(clientIdentifier1.getClientId())).thenReturn(player1Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier2.getClientId())).thenReturn(player2Response);
        when(clientServiceClient.getClientAggregateById(clientIdentifier3.getClientId())).thenReturn(player3Response);
        when(storeServiceClient.getStoreById(storeIdentifier.getStoreId())).thenReturn(locationResponse);
        when(clientServiceClient.getClientAggregateById(winner.getClientId())).thenReturn(winnerResponse);
        when(cardGameServiceClient.getCardGameById(cardIdentifier.getCardId())).thenReturn(cardGameResponseModel);
        when(tournamentRepository.getTournamentByTournamentIdentifier_TournamentId(saved.getTournamentIdentifier().getTournamentId())).thenReturn(saved);

        when(tournamentRepository.save(tournament)).thenReturn(updated);

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updated);

        //act
        TournamentClientCarGameResponseModel tournamentClientCarGameResponseModel = tournamentService.updateTournament(tournamentIdentifier.getTournamentId(),tournamentRequestModel);


        assertNotNull(tournamentClientCarGameResponseModel);
        MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(tournamentClientCarGameResponseModel.getCardGame()));
        MatcherAssert.assertThat(locationResponse, samePropertyValuesAs(tournamentClientCarGameResponseModel.getLocation()));
        MatcherAssert.assertThat(winnerResponse, samePropertyValuesAs(tournamentClientCarGameResponseModel.getWinner()));
        MatcherAssert.assertThat(resultsUpdate, samePropertyValuesAs(tournamentClientCarGameResponseModel.getResults()));
        assertEquals(resultsUpdate.get(1).getDefeats(),tournamentClientCarGameResponseModel.getResults().get(1).getDefeats());

    }
}