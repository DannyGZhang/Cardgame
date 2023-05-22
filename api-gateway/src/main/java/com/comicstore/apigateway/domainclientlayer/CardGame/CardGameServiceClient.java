package com.comicstore.apigateway.domainclientlayer.CardGame;

import com.comicstore.apigateway.presentationlayer.CardGame.CardGameRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetResponseModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.comicstore.apigateway.utils.exceptions.CardGame.DuplicateCardGameNameException;
import com.comicstore.apigateway.utils.exceptions.CardGame.DuplicateSetNameException;
import com.comicstore.apigateway.utils.exceptions.Clients.DuplicateClientInformationException;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import com.comicstore.apigateway.utils.exceptions.Clients.NoEmailAndPhoneException;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Set;

import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@Component
public class CardGameServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String CARDGAME_BASE_URL;



    public CardGameServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper,
                                 @Value("${app.cardgame-service.host}") String cardgameServiceHost,
                                 @Value("${app.cardgame-service.port}") String cardgameServicePort){
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        CARDGAME_BASE_URL = "http://" + cardgameServiceHost + ":" + cardgameServicePort + "/api/lab2/v1/cardgames";
    }


    public void deleteCardGame(String cardgameId){
        try {
            String url = CARDGAME_BASE_URL  +"/"+ cardgameId;
            restTemplate.delete(url);

            log.debug("5. Received in delete cardgame");
        } catch (HttpClientErrorException ex) {
            log.debug("5.delete");
            throw handleHttpClientException(ex,true);
        }
    }

    public void deleteSet(String setId){
        try {
            String url = CARDGAME_BASE_URL  +"/sets/"+ setId ;
            restTemplate.delete(url);

            log.debug("5. Received in delete set");
        } catch (HttpClientErrorException ex) {
            log.debug("5.delete");
            throw handleHttpClientException(ex,false);
        }
    }


    public CardGameResponseModel getCardGameById(String cardGameId) {
        CardGameResponseModel cardGameResponseModel = null;
        try {
            String url = CARDGAME_BASE_URL +"/"+ cardGameId;
            cardGameResponseModel = restTemplate
                    .getForObject(url, CardGameResponseModel.class);

            log.debug("5. api get card game by id");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            throw handleHttpClientException(ex,true);
        }
        return cardGameResponseModel;
    }


    public CardGameResponseModel[] getAllCardGames(){
        CardGameResponseModel cardGameResponseModels[] = null;
        try {
            String url = CARDGAME_BASE_URL;
            cardGameResponseModels = restTemplate
                    .getForObject(url, CardGameResponseModel[].class);
            log.debug("5. Received in get store clients");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex, true);
        }
        return cardGameResponseModels;
    }

    public SetResponseModel[] getAllCardGameSets(String cardgameId){
        SetResponseModel setResponseModels[] = null;
        try {
            String url = CARDGAME_BASE_URL + "/" + cardgameId + "/sets";
            setResponseModels = restTemplate
                    .getForObject(url, SetResponseModel[].class);
            log.debug("5. Received in get store clients");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex,false);
        }
        return setResponseModels;
    }


    public CardGameResponseModel createNewCardGame(CardGameRequestModel cardGameRequestModel){
        CardGameResponseModel cardGameResponseModel = new CardGameResponseModel();
        try {
            String url = CARDGAME_BASE_URL;
            cardGameResponseModel =
                    restTemplate.postForObject(url, cardGameRequestModel,
                            CardGameResponseModel.class);

            log.debug("5. Received in create new card");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            throw handleHttpClientException(ex, true);
        }
        return cardGameResponseModel;
    }

    public void updateCardGame(CardGameRequestModel cardGameRequestModel, String cardGameId){
        try {
            String url = CARDGAME_BASE_URL + "/" + cardGameId ;
            restTemplate.put(url, cardGameRequestModel);

            log.debug("5. Received in update card game");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            throw handleHttpClientException(ex, true);
        }
    }


    public SetResponseModel createNewCardGameSet(SetRequestModel setRequestModel, String cardId){
        SetResponseModel setResponseModel = new SetResponseModel();
        try {
            String url = CARDGAME_BASE_URL + "/"+ cardId+"/sets";
            setResponseModel =
                    restTemplate.postForObject(url, setRequestModel,
                            SetResponseModel.class);

            log.debug("5. Received in create new card");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            throw handleHttpClientException(ex, false);
        }
        return setResponseModel;
    }

    public void updateCardGameSet(SetRequestModel setRequestModel, String setId){
        try {
            String url = CARDGAME_BASE_URL + "/sets/" + setId ;
            restTemplate.put(url, setRequestModel);

            log.debug("5. Received in update card game");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex, false);
        }
    }
    private RuntimeException handleHttpClientException(HttpClientErrorException ex, boolean isCardGame) {


        if(ex.getStatusCode() == CONFLICT && isCardGame){
            return new DuplicateCardGameNameException(getErrorMessage(ex));
        }
        else if(ex.getStatusCode() == CONFLICT && !isCardGame){
            return new DuplicateSetNameException(getErrorMessage(ex));
        }


        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }


        return ex;
    }
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch (IOException ioex) {
            return ioex.getMessage();
        }
    }
}
