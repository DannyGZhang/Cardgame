package com.comicstore.tournamentservice.presentationlayer;

import com.comicstore.tournamentservice.datalayer.*;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreResponseModel;
import com.comicstore.tournamentservice.utils.HttpErrorInfo;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TournamentControllerIntegrationTest {
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
    public void getValidTournamentById_ShouldReturnTournament() throws JsonProcessingException, URISyntaxException {
        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("e42a6c05-e304-4402-ac1a-e118d2ff278e");
        ClientIdentifier clientIdentifier4 = new ClientIdentifier("9d11ccd6-f518-47e0-aad5-4d75530dd53b");
        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");


        List<Result> results = new ArrayList<>();
        results.add(new Result(clientIdentifier1, 1, 3, 0));
        results.add(new Result(clientIdentifier2, 3, 1, 0));
        results.add(new Result(clientIdentifier3, 1, 2, 1));
        results.add(new Result(clientIdentifier4, 0, 4, 0));


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


        ClientResponseModel player3Response = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                clientIdentifier3.getClientId(),
                "Frederick",
                "Girardini", 0,
                377.37, "fgirardini2g@ox.ac.uk",
                "285-342-7049");

        ClientResponseModel player4Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier4.getClientId(),
                "Hunfredo",
                "Colgan", 20,
                2418.39, "hcolgan2c@blogspot.com",
                "583-709-0854");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");

        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier3.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player3Response))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier4.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player4Response))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" + cardIdentifier.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );


        String url = "api/lab2/v1/tournaments/" + tournamentIdentifier.getTournamentId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(TournamentClientCarGameResponseModel.class)
                .value((dto) -> {
                            assertNotNull(dto);
                            MatcherAssert.assertThat(winnerResponse, samePropertyValuesAs(dto.getWinner()));
                            MatcherAssert.assertThat(results, samePropertyValuesAs(dto.getResults()));
                            MatcherAssert.assertThat(player1Response, samePropertyValuesAs(dto.getPlayers().get(0)));
                            MatcherAssert.assertThat(player2Response, samePropertyValuesAs(dto.getPlayers().get(1)));
                            MatcherAssert.assertThat(player3Response, samePropertyValuesAs(dto.getPlayers().get(2)));
                            MatcherAssert.assertThat(player4Response, samePropertyValuesAs(dto.getPlayers().get(3)));
                            MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(dto.getCardGame()));
                            MatcherAssert.assertThat(locationResponse, samePropertyValuesAs(dto.getLocation()));
                        }
                );


    }

    @Test
    public void whenAllValuesAreValidOnCreateTournament_ShouldReturnNewTournament() throws JsonProcessingException, URISyntaxException {
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


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" + cardIdentifier.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
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
                    assertNotNull(dto);
                    MatcherAssert.assertThat(winnerResponse, samePropertyValuesAs(dto.getWinner(), "totalBought", "rebate"));
                    MatcherAssert.assertThat(player1Response, samePropertyValuesAs(dto.getPlayers().get(0), "totalBought", "rebate"));
                    MatcherAssert.assertThat(player2Response, samePropertyValuesAs(dto.getPlayers().get(1), "totalBought", "rebate"));
                    MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(dto.getCardGame()));
                    MatcherAssert.assertThat(locationResponse, samePropertyValuesAs(dto.getLocation()));
                    assertEquals(tournamentRequestModel.getEntryCost(), dto.getEntryCost());
                    MatcherAssert.assertThat(tournamentRequestModel.getResults(), samePropertyValuesAs(dto.getResults()));
                    assertEquals(dto.getPlayers().get(0).getTotalBought(), player1Response.getTotalBought() + tournamentRequestModel.getEntryCost());
                    assertEquals(dto.getPlayers().get(1).getTotalBought(), player2Response.getTotalBought() + tournamentRequestModel.getEntryCost());
                });

    }



    @Test
    public void whenWinnerIsNotInPlayerListCreateTournament_ShouldThrowWinnerNotInPlayerListException() throws JsonProcessingException, URISyntaxException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("01f4e1d5-194a-4b1c-ab79-d8c008071ff5");
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


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" + cardIdentifier.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );


        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.BAD_REQUEST, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }

    @Test
    public void whenStoreDoesNotExistCreateTournament_ShouldThrowNotFoundException() throws JsonProcessingException, URISyntaxException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("01f4e1d5-194a-4b1c-ab79-d8c008071ff5");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");


        double entryCost = 6.00;


        StoreIdentifier storeIdentifier = new StoreIdentifier("1");
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


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );



        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }
    @Test
    public void whenWinnerDoesNotExistCreateTournament_ShouldThrowNotFoundException() throws JsonProcessingException, URISyntaxException {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("1");
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



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );



        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }


    @Test
    public void whenCardGameDoesNotExistInCreateTournament_ShouldThrowNotFoundException() throws JsonProcessingException, URISyntaxException {
        CardIdentifier cardIdentifier = new CardIdentifier("1");
        ClientIdentifier winner = new ClientIdentifier("01f4e1d5-194a-4b1c-ab79-d8c008071ff5");
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


        StoreResponseModel locationResponse = new StoreResponseModel(storeIdentifier.getStoreId(),
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" + cardIdentifier.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );


        String url = "api/lab2/v1/tournaments";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }


    @Test
    public void whenAllValuesAreValidOnUpdateTournament_ShouldUpdatedTournament() throws JsonProcessingException, URISyntaxException {

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

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier2.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player2Response))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier3.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player3Response))
                );
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier3.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeIdentifier.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" + cardIdentifier.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );


        String url = "api/lab2/v1/tournaments/" + tournamentIdentifier.getTournamentId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TournamentClientCarGameResponseModel.class)
                .value((dto) -> {
                    assertNotNull(dto);
                    MatcherAssert.assertThat(winnerResponse, samePropertyValuesAs(dto.getWinner(), "totalBought", "rebate"));
                    MatcherAssert.assertThat(player1Response, samePropertyValuesAs(dto.getPlayers().get(0), "totalBought", "rebate"));
                    MatcherAssert.assertThat(player2Response, samePropertyValuesAs(dto.getPlayers().get(1), "totalBought", "rebate"));
                    MatcherAssert.assertThat(player3Response, samePropertyValuesAs(dto.getPlayers().get(2), "totalBought", "rebate"));
                    MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(dto.getCardGame()));
                    MatcherAssert.assertThat(locationResponse, samePropertyValuesAs(dto.getLocation()));
                    assertEquals(tournamentRequestModel.getEntryCost(), dto.getEntryCost());
                    MatcherAssert.assertThat(tournamentRequestModel.getResults(), samePropertyValuesAs(dto.getResults()));
                    assertEquals(dto.getPlayers().get(0).getTotalBought(), player1Response.getTotalBought());
                    assertEquals(dto.getPlayers().get(1).getTotalBought(), player2Response.getTotalBought());
                    assertEquals(dto.getPlayers().get(2).getTotalBought(), player3Response.getTotalBought() + tournamentRequestModel.getEntryCost());

                });

    }



    @Test
    public void whenPlayerDoesNotExistOnUpdateTournament_ThenThrowNotFoundException() throws JsonProcessingException, URISyntaxException {

        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("1");
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






        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                );




        String url = "api/lab2/v1/tournaments/" + tournamentIdentifier.getTournamentId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }
    @Test
    public void whenInvalidEntryCostOnUpdateTournament_ThenIllegalEntryCostChangeException() throws JsonProcessingException, URISyntaxException {

        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("1");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");


        double entryCost = 6.5;


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








        String url = "api/lab2/v1/tournaments/" + tournamentIdentifier.getTournamentId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tournamentRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertNotNull(dto.getMessage());
                    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, dto.getHttpStatus());
                    assertEquals("uri=/"+url,dto.getPath());
                });

    }


    @Test
    public void getAllTournaments_ShouldReturnAllTournamentInSmallerDTO() {
        Integer expectedNum = 2;


        String url = "api/lab2/v1/tournaments";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNum);

    }


    @Test
    public void getAllTournamentsByCardGameId_ShouldReturnAllTournamentInSmallerDTOWithTheCardGameId() throws URISyntaxException, JsonProcessingException {
        Integer expectedNum = 1;

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");


        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/6dfc9e76-1aa7-4786-8398-f4ae25737324")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );
        String url = "api/lab2/v1/tournaments?cardGameId=" + cardIdentifier.getCardId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNum);

    }


    @Test
    public void getAllTournamentsByCardGameIdAndPlayerid_ShouldReturnAllTournamentInSmallerDTOWithTheCardGameIdAndPlayerId() throws URISyntaxException, JsonProcessingException {
        Integer expectedNum = 1;
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");

        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");

        ClientResponseModel player1Response = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier1.getClientId(),
                "Pauline",
                "Grishmanov",
                20,
                3416.77, "pgrishmanov1r@tinyurl.com",
                "979-767-5807");

        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel(cardIdentifier.getCardId(), "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + clientIdentifier1.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(player1Response))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/6dfc9e76-1aa7-4786-8398-f4ae25737324")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );
        String url = "api/lab2/v1/tournaments?cardGameId=" + cardIdentifier.getCardId()+"&playerId="+clientIdentifier1.getClientId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNum);

    }


    @Test
    public void getAllTournamentsByWinnerId_ShouldReturnAllTournamentInSmallerDTOWithThatWinnerId() throws URISyntaxException, JsonProcessingException {
        Integer expectedNum = 1;

        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                winner.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + winner.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        String url = "api/lab2/v1/tournaments?winnerId=" + winner.getClientId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNum);

    }


    @Test
    public void getAllTournamentsByPlayerId_ShouldReturnAllTournamentInSmallerDTOWhereThePlayerPlayedIn() throws URISyntaxException, JsonProcessingException {
        Integer expectedNum = 2;

        ClientIdentifier player = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");

        ClientResponseModel winnerResponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                player.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/stores/clients/" + player.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(winnerResponse))
                );
        String url = "api/lab2/v1/tournaments?playerId=" + player.getClientId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNum);

    }

    @Test
    public void WhenDeleteExistingTournament_ThenDeleteTournament() {
        TournamentIdentifier tournamentIdentifier = new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3");

        String url = "api/lab2/v1/tournaments/" + tournamentIdentifier.getTournamentId();


        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();


        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class).value((dto) -> {
                            assertNotNull(dto.getMessage());
                            assertEquals(dto.getPath(), "uri=/" + url);
                        }
                );

    }


    @Test
    public void WhenDeleteInvalidTournamentId_ThenThrowNotFoundException() {

        String url = "api/lab2/v1/tournaments/1";


        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound().expectBody(HttpErrorInfo.class)
                .value((dto) ->{
                    assertEquals(HttpStatus.NOT_FOUND,dto.getHttpStatus());
                    assertNotNull(dto.getMessage());
                    assertEquals("uri=/"+url,dto.getPath());
                });
    }


}