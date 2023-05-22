package com.comicstore.apigateway.businesslayer.tournament;

import com.comicstore.apigateway.domainclientlayer.tournament.TournamentServiceClient;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.*;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class TournamentServiceUnitTest {
    @Autowired
    TournamentService tournamentService;

    @MockBean
    TournamentServiceClient tournamentServiceClient;


    @Test
    public void createNewTournament_ShouldReturnNewTournament(){

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");


        double entryCost = 6.00;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));


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

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");

        List<ClientResponseModel> players = new ArrayList<>();

        players.add(player1Response);
        players.add(player2Response);



        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);


        TournamentClientCarGameResponseModel tournamentClientCarGameResponseModel =
                new TournamentClientCarGameResponseModel("350497d4-438d-41ff-9568-f77665aba774",players,cardGameResponseModel,locationResponse,entryCost,winnerResponse,results);




        when(tournamentServiceClient.createNewTournament(tournamentRequestModel))
                .thenReturn(tournamentClientCarGameResponseModel);


        TournamentClientCarGameResponseModel returned = tournamentService.createTournament(tournamentRequestModel);

        MatcherAssert.assertThat(tournamentClientCarGameResponseModel, samePropertyValuesAs(returned));


    }


    @Test
    public void updateTournamentWithInvalidId_ShouldThowNotFoundException(){
        String tournamentId = "Invalid";

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");


        double entryCost = 6.00;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));


        TournamentRequestModel tournamentRequestModel = TournamentRequestModel.builder()
                .cardGame(cardIdentifier.getCardId())
                .entryCost(entryCost)
                .location(storeIdentifier.getStoreId())
                .winner(winner.getClientId())
                .results(results)
                .build();



        doThrow(new NotFoundException()).when(tournamentServiceClient).updateTournament(tournamentRequestModel, tournamentId);


        assertThrows(NotFoundException.class, () -> {
            tournamentService.updateTournament(tournamentId,tournamentRequestModel);
        });


    }
}