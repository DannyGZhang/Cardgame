package com.comicstore.tournamentservice.domainclientlayer.CardGame;

import com.comicstore.tournamentservice.utils.HttpErrorInfo;
import com.comicstore.tournamentservice.utils.exceptions.InvalidInputException;
import com.comicstore.tournamentservice.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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


    public CardGameResponseModel getCardGameById(String cardGameId) {
        CardGameResponseModel cardGameResponseModel = null;
        try {
            String url = CARDGAME_BASE_URL +"/"+ cardGameId;
            cardGameResponseModel = restTemplate
                    .getForObject(url, CardGameResponseModel.class);

            log.debug("5. api get card game by id");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            handleHttpClientException(ex,true);
        }
        return cardGameResponseModel;
    }






    private void handleHttpClientException(HttpClientErrorException ex, boolean isCardGame) {



        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NotFoundException(getErrorMessage(ex));
        }



        throw ex;
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
