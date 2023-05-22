package com.comicstore.apigateway.businesslayer.CardGame;

import com.comicstore.apigateway.businesslayer.Client.ClientsService;
import com.comicstore.apigateway.domainclientlayer.CardGame.CardGameServiceClient;
import com.comicstore.apigateway.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetResponseModel;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CardGameServiceUnitTest {
    @Autowired
    CardGameService cardGameService;
    @MockBean
    private CardGameServiceClient cardGameServiceClient;

    @Test
    public void whenCreateCardGame_ThenReturnCreatedCardGame(){
        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel("6dfc9e76-1aa7-4786-8398-f4ae25737324", "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel.getCardGameName())
                .company(cardGameResponseModel.getCompany())
                .isActive(cardGameResponseModel.getIsActive())
                .releaseDate(cardGameResponseModel.getReleaseDate().toString())
                .build();


        when(cardGameServiceClient.createNewCardGame(cardGameRequestModel))
                .thenReturn(cardGameResponseModel);

        CardGameResponseModel returned = cardGameServiceClient.createNewCardGame(cardGameRequestModel);

        MatcherAssert.assertThat(cardGameResponseModel, samePropertyValuesAs(returned));


    }


    @Test
    public void whenUpdateCardGameWithInvalidId_ThenThrowNotFoundException(){
        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel("Invalid", "Magic The Gathering", "Wizards of the Coast", LocalDate.parse("2000-08-01"), true);
        CardGameRequestModel cardGameRequestModel =  CardGameRequestModel.builder()
                .cardGameName(cardGameResponseModel.getCardGameName())
                .company(cardGameResponseModel.getCompany())
                .isActive(cardGameResponseModel.getIsActive())
                .releaseDate(cardGameResponseModel.getReleaseDate().toString())
                .build();


        doThrow(new NotFoundException()).when(cardGameServiceClient).updateCardGame(cardGameRequestModel, cardGameResponseModel.getCardId());


        assertThrows(NotFoundException.class, () -> {
            cardGameService.updateCardGame(cardGameResponseModel.getCardId(),cardGameRequestModel);
        });


    }


    @Test
    public void createNewSetWithInvalidBody_ShouldReturnNewState(){
       SetResponseModel setResponseModel = new SetResponseModel("acfdca5c-fcd1-41d7-b5e9-2df69f1763b2","6dfc9e76-1aa7-4786-8398-f4ae25737324","I hate doing these testss",LocalDate.parse("2018-09-10"),190);

        SetRequestModel setRequestModel =  SetRequestModel.builder()
                .cardId(setResponseModel.getCardGame())
                .numberOfCards(setResponseModel.getNumberOfCards())
                .name(setResponseModel.getName())
                .releaseDate(setResponseModel.getReleaseDate().toString())
                .build();
        when(cardGameServiceClient.createNewCardGameSet(setRequestModel,setResponseModel.getCardGame()))
                .thenReturn(setResponseModel);

        SetResponseModel returned = cardGameService.addCardGameSet(setResponseModel.getCardGame(),setRequestModel);

        MatcherAssert.assertThat(setResponseModel, samePropertyValuesAs(returned));


    }



}