package com.comicstore.cardgameservice.presentationlayer;

import com.comicstore.cardgameservice.datalayer.CardGameRepository;
import com.comicstore.cardgameservice.datalayer.CardIdentifier;
import com.comicstore.cardgameservice.datalayer.SetIdentifier;
import com.comicstore.cardgameservice.datalayer.SetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-mysql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CardGameControllerIntegrationTest {
    private final String BASE_URI_CARDGAMES = "/api/lab2/v1/cardgames";
    private final String VALID_CARDGAME_ID = "6dfc9e76-1aa7-4786-8398-f4ae25737324";
    private final String VALID_CARDGAME_NAME = "Magic The Gathering";
    private final String VALID_CARDGAME_COMPANY = "Wizards of the Coast";
    private final String VALID_CARDGAME_RELEASEDATE = "2000-08-01";
    private final Boolean VALID_CARDGAME_ISACTIVE = true;



    private final String VALID_SET_ID = "7a167ae1-38aa-4584-8c08-ef1ac1a32236";
    private final String VALID_SET_CARDGAME_ID = "579a2484-43f4-413c-a97a-d0f1d9c980e4";
    private final String VALID_SET_NAME = "Warriors Return";
    private final String VALID_SET_RELEASEDATE = "2008-08-18";
    private final int VALID_SET_NUMOFCARDS = 113;






    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CardGameRepository cardGameRepository;

    @Autowired
    SetRepository setRepository;
    @Test
    void WhenCardGamesExist_thenReturnAllCardGames() {
        int expectedNumCardGames = 4;
        webTestClient.get()
                .uri(BASE_URI_CARDGAMES)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumCardGames);
    }

    @Test
    void WhenCardGameExists_thenReturnCardGame() {


        webTestClient.get()
                .uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CardGameResponseModel.class)
                .value((dto) -> {
                    assertEquals(dto.getCardId(), VALID_CARDGAME_ID);
                    assertEquals(dto.getCompany(), VALID_CARDGAME_COMPANY);
                    assertEquals(dto.getCardGameName(), VALID_CARDGAME_NAME);
                    assertEquals(dto.getIsActive(), VALID_CARDGAME_ISACTIVE);
                    assertEquals(dto.getReleaseDate(), LocalDate.parse(VALID_CARDGAME_RELEASEDATE));
                });
    }


    @Test
    void WhenCreateCardGameWithValidValues_ReturnCardGame() {
        String expectedName = "Magic the Gathering";
        String expectedCompany = "Wizard";
        String expectedRelease = "2020-02-02";
        Boolean expectedActive = false;

        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,expectedCompany,expectedRelease,expectedActive);
        webTestClient.post().uri(BASE_URI_CARDGAMES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CardGameResponseModel.class)
                .value(val -> {
                    assertNotNull(val.getCardId());
                    assertEquals(val.getCardGameName(), expectedName);
                    assertEquals(val.getCompany(), expectedCompany);
                    assertEquals(val.getReleaseDate(), LocalDate.parse(expectedRelease));
                    assertEquals(val.getIsActive(),expectedActive);
                });

    }


    @Test
    void WhenCreateCardGameWithInvalidDate_ReturnCardGame() {
        String expectedName = "Magic the Gathering";
        String expectedCompany = "Wizard";
        String expectedRelease = "2020/02;02";
        Boolean expectedActive = false;

        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,expectedCompany,expectedRelease,expectedActive);
        webTestClient.post().uri(BASE_URI_CARDGAMES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES)
                .jsonPath("$.message")
                .isEqualTo("Release date entered is not in format YYYY-MM-DD : " + cardGameRequestModel.getReleaseDate());
        }



    @Test
    void WhenCreateCardGameWithInvalidName_ReturnCardGame() {
        String expectedName = "Pokemon TCG";
        String expectedCompany = "Wizard";
        String expectedRelease = "2020-02-02";
        Boolean expectedActive = false;

        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,expectedCompany,expectedRelease,expectedActive);
        webTestClient.post().uri(BASE_URI_CARDGAMES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES)
                .jsonPath("$.message")
                .isEqualTo("Name provided is a duplicate : " + cardGameRequestModel.getCardGameName());
    }
    @Test
    void WhenCardGameExists_ReturnSets(){
        int expectedNumSets =  1;

        webTestClient.get()
                .uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID + "/sets")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumSets);

    }
    @Test
    void WhenSetsExists_ReturnSets(){
        int expectedNumSets =  3;

        webTestClient.get()
                .uri(BASE_URI_CARDGAMES + "/sets")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumSets);

    }

    @Test
    void deleteCardGameWithValidId_ReturnNoContent(){
        webTestClient.delete().uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();





        assertNull(cardGameRepository.getCardGameByCardIdentifier_CardId(VALID_CARDGAME_ID));
        assertEquals(0,setRepository.getSetByCardIdentifier_CardId(VALID_CARDGAME_ID).size());

    }




    @Test
    void deleteSetWithValidId_ReturnNoContent(){
        webTestClient.delete().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();

        assertNull(setRepository.getSetBySetIdentifier_SetId(VALID_SET_ID));

    }

    @Test
    void deleteSetWithInvalidId_ThenThrowNotFoundException(){
        webTestClient.delete().uri(BASE_URI_CARDGAMES + "/sets/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/sets/1")
                .jsonPath("$.message")
                .isEqualTo("No set with id : " + 1 + " was found !");;


    }
    @Test
    public void WhenUpdateSetWithValidValues_thenReturnUpdatedSet() {
        //arrange
        String expectedName = "testName";

        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,VALID_SET_RELEASEDATE,VALID_SET_NUMOFCARDS);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .jsonPath("$.setId").isEqualTo(VALID_SET_ID)
                .jsonPath("$.name").isEqualTo(expectedName);
    }


    @Test
    public void WhenUpdateSetWithInvalidName_thenReturnUpdatedSet() {
        //arrange
        String expectedName = "Limited Edition Alpha";

        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,VALID_SET_RELEASEDATE,VALID_SET_NUMOFCARDS);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .jsonPath("$.message").isNotEmpty();
    }
    @Test
    public void WhenUpdateSetWithInvalidNumOfCards_thenThrowInvalidInputException() {
        //arrange
        int invalidNumOfCard = -100;

        SetRequestModel setRequestModel = createNewSetRequestModel(VALID_SET_NAME,VALID_SET_RELEASEDATE,invalidNumOfCard);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .jsonPath("$.message")
                .isEqualTo("Number of cards can't be negative : " + setRequestModel.getNumberOfCards());
    }


    @Test
    public void WhenUpdateSetWithInvalidDateComparedToGame_thenThrowInvalidInputException() {
        //arrange
        String expectedDate = "1800-02-02";

        SetRequestModel setRequestModel = createNewSetRequestModel(VALID_SET_NAME,expectedDate,VALID_SET_NUMOFCARDS);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" +BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .jsonPath("$.message")
                .isEqualTo("A set can't be release be for the game : \n" +
                        "Game was released : 2000-09-23 \n" +
                        "SimpleDateFormat entered for set : 1800-02-02");
    }
    @Test
    public void WhenUpdateSetWithInvalidDate_thenThrowInvalidInputException() {
        //arrange
        String expectedName = "testName";
        String invalidDate = "2020/2/0";

        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,invalidDate,VALID_SET_NUMOFCARDS);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/sets/" + VALID_SET_ID)
                .jsonPath("$.message")
                .isEqualTo("Release date entered is not in format YYYY-MM-DD : " + setRequestModel.getReleaseDate());

    }


    @Test
    public void WhenUpdateSetWithInvalidCardGameId_thenThrowNotFoundException() {
        //arrange
        String expectedName = "testName";
        String invalidDate = "2020-12-10";

        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,invalidDate,VALID_SET_NUMOFCARDS);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/sets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/sets/1")
                .jsonPath("$.message")
                .isEqualTo("No set with id : " + 1 + " was found !");

    }


    @Test
    public void WhenUpdateCardGameWithValidValues_thenReturnUpdatedCardGame() {
        //arrange
        String expectedName = "testName";

        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,VALID_CARDGAME_COMPANY,VALID_CARDGAME_RELEASEDATE,VALID_CARDGAME_ISACTIVE);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .jsonPath("$.cardId").isEqualTo(VALID_CARDGAME_ID)
                .jsonPath("$.cardGameName").isEqualTo(expectedName);
    }

    @Test
    public void WhenUpdateCardGameWithInvalidDate_thenThrowInvalidInputException() {
        //arrange
        String expectedName = "testName";
        String invalidDate = "2020.22/2";
        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,VALID_CARDGAME_COMPANY,invalidDate,VALID_CARDGAME_ISACTIVE);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .jsonPath("$.message")
                .isEqualTo("Release date entered is not in format YYYY-MM-DD : " + cardGameRequestModel.getReleaseDate());

    }


    @Test
    public void WhenUpdateCardGameWithInvalidUUID_thenThrowNotFoundException() {
        //arrange
        String expectedName = "testName";
        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,VALID_CARDGAME_COMPANY,VALID_CARDGAME_RELEASEDATE,VALID_CARDGAME_ISACTIVE);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/1")
                .jsonPath("$.message")
                .isEqualTo("Card game with id : " + 1 + " was not found !");

    }

    @Test
    public void WhenUpdateCardGameWithInvalidName_thenThrowDuplicateNameException() {
        //arrange
        String expectedName = "Pokemon TCG";
        CardGameRequestModel cardGameRequestModel = createNewCardGameRequestModel(expectedName,VALID_CARDGAME_COMPANY,VALID_CARDGAME_RELEASEDATE,VALID_CARDGAME_ISACTIVE);

        //act and assert
        webTestClient.put().uri(BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardGameRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/" + VALID_CARDGAME_ID)
                .jsonPath("$.message").isNotEmpty();
    }


    @Test
    public void WhenCreateSetWithValidValues_thenReturnNewSet() {
        //arrange
        String expectedDate = "2002-02-02";
        String expectedName = "M20";
        int expectedNumOfCards = 190;
        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,expectedDate,expectedNumOfCards);

        //act and assert
        webTestClient.post().uri(BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(SetResponseModel.class)
                .value(dto ->{
                    assertNotNull(dto.getSetId());
                    assertEquals(dto.getCardGame(),VALID_CARDGAME_ID);
                    assertEquals(dto.getName(),expectedName);
                    assertEquals(dto.getReleaseDate(),LocalDate.parse(expectedDate));
                    assertEquals(dto.getNumberOfCards(),expectedNumOfCards);
        });
    }


    @Test
    public void WhenCreateSetWithInvalidDate_thenThrowInvalidInputException() {
        //arrange
        String expectedDate = "1800/02/02";
        String expectedName = "M20";
        int expectedNumOfCards = 190;
        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,expectedDate,expectedNumOfCards);

        //act and assert
        webTestClient.post().uri(BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .jsonPath("$.message")
                .isEqualTo("Release date entered is not in format YYYY-MM-DD : " + setRequestModel.getReleaseDate());
    }

    @Test
    public void WhenCreateSetWithInvalidName_thenThrowInvalidInputException() {
        //arrange
        String expectedDate = "2020-02-02";
        String expectedName = "The Awakening";
        int expectedNumOfCards = 190;
        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,expectedDate,expectedNumOfCards);

        //act and assert
        webTestClient.post().uri(BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody().jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .jsonPath("$.message")
                .isEqualTo("Name provided is a duplicate : " + setRequestModel.getName());


    }


    @Test
    public void WhenCreateSetWithCardGameId_thenThrowNotFoundException() {
        //arrange
        String expectedDate = "2020-02-02";
        String expectedName = "The Awakening";
        int expectedNumOfCards = 190;
        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,expectedDate,expectedNumOfCards);

        //act and assert
        webTestClient.post().uri(BASE_URI_CARDGAMES + "/1/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody().jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/1/sets")
                .jsonPath("$.message")
                .isEqualTo("No card game with id : 1 was found !");


    }


    @Test
    public void WhenCreateSetWithInvalidDateComparedToGame_thenThrowInvalidInputException() {
        //arrange
        String expectedDate = "1800-02-02";
        String expectedName = "M20";
        int expectedNumOfCards = 190;
        SetRequestModel setRequestModel = createNewSetRequestModel(expectedName,expectedDate,expectedNumOfCards);

        //act and assert
        webTestClient.post().uri(BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(setRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_CARDGAMES + "/"+VALID_CARDGAME_ID+"/sets")
                .jsonPath("$.message")
                .isEqualTo("A set can't be release be for the game : \n" +
                        "Game was released : 2000-08-01 \n" +
                        "SimpleDateFormat entered for set : 1800-02-02");
    }
    private CardGameRequestModel createNewCardGameRequestModel(String name, String company, String date, Boolean active){
        return CardGameRequestModel.builder()
                .cardGameName(name).company(company).releaseDate(date).isActive(active).build();
    }
    private SetRequestModel createNewSetRequestModel(String name, String release, int numOfCards){
        return SetRequestModel.builder()
                .name(name).releaseDate(release).numberOfCards(numOfCards).build();
    }
}