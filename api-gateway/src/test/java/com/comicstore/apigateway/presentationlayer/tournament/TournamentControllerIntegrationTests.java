package com.comicstore.apigateway.presentationlayer.tournament;

import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TournamentControllerIntegrationTests {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    public void setUp() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mapper.registerModule(new JavaTimeModule());
    }
    @Test
    void createTournaments() throws URISyntaxException, JsonProcessingException {
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



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(tournamentClientCarGameResponseModel))
                );



        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TournamentClientCarGameResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(tournamentClientCarGameResponseModel, samePropertyValuesAs(dto,"results"));
                    MatcherAssert.assertThat(tournamentClientCarGameResponseModel.getResults(), samePropertyValuesAs(dto.getResults()));
                    assertEquals(tournamentClientCarGameResponseModel.getCardGame().getCardId(), tournamentRequestModel.getCardGame());
                    assertEquals(tournamentClientCarGameResponseModel.getLocation().getStoreId(), tournamentRequestModel.getLocation());
                    assertEquals(tournamentClientCarGameResponseModel.getWinner().getClientId(), tournamentRequestModel.getWinner());

                });
    }


    @Test
    void createTournamentsWithWinnerNotInList_ShouldReturnWinnerNotInPlayerListException() throws URISyntaxException, JsonProcessingException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("09d36844-15fc-4c71-ae22-28494c87a093");
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




        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(
                                new HttpErrorInfo(HttpStatus.BAD_REQUEST,"uri=/api/lab2/v1/tournaments","The winner was not part of the result list")
                        ))
                );



        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.BAD_REQUEST,"uri=/api/lab2/v1/tournaments","The winner was not part of the result list"), samePropertyValuesAs(dto,"timestamp"));

                });
    }



    @Test
    void createTournamentsWithNegativeCost_ShouldReturnInvalidInputException() throws URISyntaxException, JsonProcessingException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("09d36844-15fc-4c71-ae22-28494c87a093");
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




        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(
                                new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/tournaments","Entry cost is negative !")
                        ))
                );



        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/tournaments","Entry cost is negative !"), samePropertyValuesAs(dto,"timestamp"));

                });
    }
    @Test
    void getTournaments() throws URISyntaxException, JsonProcessingException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");


        double entryCost = 6.00;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();

        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));


        TournamentResponseModel tournamentResponseModel = new TournamentResponseModel("350497d4-438d-41ff-9568-f77665aba774",storeIdentifier.getStoreId(),cardIdentifier.getCardId(),entryCost,winner.getClientId(),results);
Integer expectedNum = 1;


        TournamentResponseModel[] tournamentResponseModels = {tournamentResponseModel};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(tournamentResponseModels)));

        String url = "api/lab2/v1/tournaments";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(TournamentResponseModel[].class)
                .value((dto) -> {
                    assertEquals(expectedNum, dto.length);
                    Arrays.stream(dto).toList().forEach(tournement -> assertNotNull(tournement.getLinks()));
                });

    }

    @Test
    void getTournamentById() throws URISyntaxException, JsonProcessingException {
        String tournamentId = "350497d4-438d-41ff-9568-f77665aba774";

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



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments/"+tournamentId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(tournamentClientCarGameResponseModel)));

        String url = "api/lab2/v1/tournaments/"+tournamentId;

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(TournamentClientCarGameResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(tournamentClientCarGameResponseModel, samePropertyValuesAs(dto,"results"));
                    MatcherAssert.assertThat(tournamentClientCarGameResponseModel.getResults(), samePropertyValuesAs(dto.getResults()));
                    assertEquals(tournamentClientCarGameResponseModel.getCardGame().getCardId(), tournamentRequestModel.getCardGame());
                    assertEquals(tournamentClientCarGameResponseModel.getLocation().getStoreId(), tournamentRequestModel.getLocation());
                    assertEquals(tournamentClientCarGameResponseModel.getWinner().getClientId(), tournamentRequestModel.getWinner());
                });

    }

    @Test
    void deleteTournament() throws URISyntaxException {
        String tournamentId = "350497d4-438d-41ff-9568-f77665aba774";

        String url = "api/lab2/v1/tournaments/" + tournamentId;
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments/" + tournamentId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT)
                );
        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent()
        ;

    }

    @Test
    void deleteTournamentWithInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        String tournamentId = "350497d4-438d-41ff-9568-f77665aba774";

        String url = "api/lab2/v1/tournaments/" + tournamentId;
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments/" + tournamentId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/tournaments/" + tournamentId,"Not found"))));
        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/tournaments/" + tournamentId,"Not found"), samePropertyValuesAs(dto,"timestamp"));
                  });

    }

    @Test
    void updateTournament() throws JsonProcessingException, URISyntaxException {
        String tournamentId = "350497d4-438d-41ff-9568-f77665aba774";

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


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments/" + tournamentId)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                );

        String url = "api/lab2/v1/tournaments/"+tournamentId;
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void updateTournamentWithPriceChange_ShouldThrowIllegalPriceChangeException() throws JsonProcessingException, URISyntaxException {
        String tournamentId = "350497d4-438d-41ff-9568-f77665aba774";

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


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7004/api/lab2/v1/tournaments/" + tournamentId)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(
                                new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/tournaments/" + tournamentId,"The entry cost must be the same as the one defined at the time of creation \nEntry cost on creation : " + 5.00 + "\nThe one entered in update : " + tournamentRequestModel.getEntryCost())
                        )));

        String url = "api/lab2/v1/tournaments/"+tournamentId;
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/tournaments/" + tournamentId,"The entry cost must be the same as the one defined at the time of creation \nEntry cost on creation : " + 5.00 + "\nThe one entered in update : " + tournamentRequestModel.getEntryCost())
                            ,samePropertyValuesAs(dto,"timestamp"));

                });;
    }
}