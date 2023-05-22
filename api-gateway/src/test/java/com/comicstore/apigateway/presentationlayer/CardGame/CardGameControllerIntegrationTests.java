package com.comicstore.apigateway.presentationlayer.CardGame;

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

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CardGameControllerIntegrationTests {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    private CardGameResponseModel cardGameResponseModel;
    private CardGameResponseModel cardGameResponseModel2;

    private SetResponseModel setResponseModel;
    @BeforeEach
    public void setUp() {

        cardGameResponseModel = new CardGameResponseModel("6dfc9e76-1aa7-4786-8398-f4ae25737324", "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);
        cardGameResponseModel2 = new CardGameResponseModel("de173589-8526-4994-bfa8-e0e1340b97e0", "Dragon Ball", "Bonzai", LocalDate.parse("2002-08-01"), true);
        setResponseModel = new SetResponseModel("acfdca5c-fcd1-41d7-b5e9-2df69f1763b2",cardGameResponseModel.getCardId(),"I hate doing these testss",LocalDate.parse("2018-09-10"),190);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getCardGames_ShouldReturnAllCardGames() throws URISyntaxException, JsonProcessingException {

        CardGameResponseModel[] cardGameResponseModels = {cardGameResponseModel,cardGameResponseModel2};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModels)
                ));
        String url = "api/lab2/v1/cardgames";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(CardGameResponseModel[].class)
                .value((dto) -> {
                    MatcherAssert.assertThat(cardGameResponseModels, samePropertyValuesAs(dto));
                });
    }

    @Test
    void getCardGame_ShouldReturnCardGameWithId() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +cardGameResponseModel.getCardId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel)
                        ));
        String url = "api/lab2/v1/cardgames/"+cardGameResponseModel.getCardId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(CardGameResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(dto));
                });
    }

    @Test
    void addSetToCardGame_ShouldCreateASetToCardGameId() throws URISyntaxException, JsonProcessingException {
        SetRequestModel setRequestModel =  SetRequestModel.builder()
                .cardId(setResponseModel.getCardGame())
                .numberOfCards(setResponseModel.getNumberOfCards())
                .name(setResponseModel.getName())
                .releaseDate(setResponseModel.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +setResponseModel.getCardGame()+"/sets")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(setResponseModel))
                );


        String url = "api/lab2/v1/cardgames/"+setResponseModel.getCardGame()+"/sets";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SetResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(setResponseModel, samePropertyValuesAs(dto));
                    assertEquals(setResponseModel.getCardGame(),dto.getCardGame());
                    assertEquals(setRequestModel.getName(),dto.getName());
                });
    }



        @Test
        void addSetToCardGameWithDuplicateSetName_ShouldThrowDuplicateSetNameException() throws URISyntaxException, JsonProcessingException {
            SetRequestModel setRequestModel =  SetRequestModel.builder()
                    .cardId(setResponseModel.getCardGame())
                    .numberOfCards(setResponseModel.getNumberOfCards())
                    .name(setResponseModel.getName())
                    .releaseDate(setResponseModel.getReleaseDate().toString())
                    .build();
            mockRestServiceServer.expect(ExpectedCount.once(),
                            requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +setResponseModel.getCardGame()+"/sets")))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/cardgames/" +setResponseModel.getCardGame()+"/sets","Name provided is a duplicate : " + setRequestModel.getName())))
                    );


            String url = "api/lab2/v1/cardgames/"+setResponseModel.getCardGame()+"/sets";
            webTestClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(setRequestModel)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(HttpErrorInfo.class)
                    .value((dto) -> {
                        MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/cardgames/" +setResponseModel.getCardGame()+"/sets","Name provided is a duplicate : " + setRequestModel.getName()), samePropertyValuesAs(dto,"timestamp"));
                    });
        }

    @Test
    void getCardGameSets_ShouldReturnAllSetsWithCardGameId() throws JsonProcessingException, URISyntaxException {
        SetResponseModel[] setResponseModels = {setResponseModel};
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +setResponseModel.getCardGame()+"/sets")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(setResponseModels))
                );
        String url = "api/lab2/v1/cardgames/"+setResponseModel.getCardGame()+"/sets";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(SetResponseModel[].class)
                .value((dto) -> {
                    assertEquals(1,dto.length);
                    assertEquals(setResponseModel.getSetId(),dto[0].getSetId());
                });

    }

    @Test
    void deleteCardGame_ShouldReturnNoContent() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +cardGameResponseModel.getCardId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        String url = "api/lab2/v1/cardgames/"+cardGameResponseModel.getCardId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    void deleteCardGameWithInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +cardGameResponseModel.getCardId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/cardgames/" +cardGameResponseModel.getCardId(),"Not found"))));
        String url = "api/lab2/v1/cardgames/"+cardGameResponseModel.getCardId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value(dto->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/cardgames/" +cardGameResponseModel.getCardId(),"Not found"), samePropertyValuesAs(dto,"timestamp"));

                });


    }

    @Test
    void addCardGame_ShouldReturnCreatedCardGame() throws URISyntaxException, JsonProcessingException {
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel.getCardGameName())
                .company(cardGameResponseModel.getCompany())
                .isActive(cardGameResponseModel.getIsActive())
                .releaseDate(cardGameResponseModel.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(cardGameResponseModel))
                );


        String url = "api/lab2/v1/cardgames";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CardGameResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(dto));
                    assertNotNull(dto.getLinks());
                });
    }


    @Test
    void addCardGameWithDuplicateName_ShouldThrowDuplicateCardGameException() throws URISyntaxException, JsonProcessingException {
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel.getCardGameName())
                .company(cardGameResponseModel.getCompany())
                .isActive(cardGameResponseModel.getIsActive())
                .releaseDate(cardGameResponseModel.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/cardgames","Name provided is a duplicate : " + cardGameRequestModel.getCardGameName())))
                );


        String url = "api/lab2/v1/cardgames";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/cardgames","Name provided is a duplicate : " + cardGameRequestModel.getCardGameName()), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void deleteCardGameSet_ShouldReturnNoContent() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/sets/" +setResponseModel.getSetId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        String url = "api/lab2/v1/cardgames/sets/"+setResponseModel.getSetId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }


    @Test
    void deleteCardGameSetWithInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/sets/" +setResponseModel.getSetId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/cardgames/sets/" +setResponseModel.getSetId(),"Not found")))
                );



        String url = "api/lab2/v1/cardgames/sets/"+setResponseModel.getSetId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/cardgames/sets/" +setResponseModel.getSetId(),"Not found"), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void updateSet_ShouldReturnOK() throws URISyntaxException, JsonProcessingException {
        SetRequestModel setRequestModel =  SetRequestModel.builder()
                .cardId(setResponseModel.getCardGame())
                .numberOfCards(setResponseModel.getNumberOfCards())
                .name(setResponseModel.getName())
                .releaseDate(setResponseModel.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/sets/" +setResponseModel.getSetId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                );


        String url = "api/lab2/v1/cardgames/sets/"+setResponseModel.getSetId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }



    @Test
    void updateCardGame_ShouldReturnOK() throws URISyntaxException {
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel2.getCardGameName())
                .company(cardGameResponseModel2.getCompany())
                .isActive(cardGameResponseModel2.getIsActive())
                .releaseDate(cardGameResponseModel2.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +cardGameResponseModel2.getCardId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                );


        String url = "api/lab2/v1/cardgames/"+cardGameResponseModel2.getCardId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateCardGameWithUnknownError_ShouldThrowInvalidInputException() throws URISyntaxException, JsonProcessingException {
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel2.getCardGameName())
                .company(cardGameResponseModel2.getCompany())
                .isActive(cardGameResponseModel2.getIsActive())
                .releaseDate(cardGameResponseModel2.getReleaseDate().toString())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7002/api/lab2/v1/cardgames/" +cardGameResponseModel2.getCardId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/cardgames/"  +cardGameResponseModel2.getCardId(),"Unknown Error has occurred !")))
                );


        String url = "api/lab2/v1/cardgames/"+cardGameResponseModel2.getCardId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/cardgames/"  +cardGameResponseModel2.getCardId(),"Unknown Error has occurred !"), samePropertyValuesAs(dto,"timestamp"));

                });
    }
}

