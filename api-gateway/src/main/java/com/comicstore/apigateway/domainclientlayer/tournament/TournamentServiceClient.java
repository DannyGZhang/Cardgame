package com.comicstore.apigateway.domainclientlayer.tournament;

import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentClientCarGameResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentRequestModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import com.comicstore.apigateway.utils.exceptions.Tournament.IllegalEntryCostChange;
import com.comicstore.apigateway.utils.exceptions.Tournament.WinnerNotInPlayerListException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class TournamentServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String TOURNAMENT_BASE_URL;

    public TournamentServiceClient(RestTemplate restTemplate,
                                   ObjectMapper objectMapper,
                                   @Value("${app.tournament-service.host}") String tournamentServiceHost,
                                   @Value("${app.tournament-service.port}") String tournamentServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.TOURNAMENT_BASE_URL = "http://" + tournamentServiceHost + ":" + tournamentServicePort + "/api/lab2/v1/tournaments";
    }

    public TournamentResponseModel[] getAllTournaments(){
        TournamentResponseModel tournaments[] = null;
        try {
            String url = TOURNAMENT_BASE_URL;
            tournaments = restTemplate
                    .getForObject(url, TournamentResponseModel[].class);
            log.debug("5. Received in get store clients");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
        return tournaments;
    }


    public TournamentClientCarGameResponseModel getTournamentByID(String tournamentId) {
        TournamentClientCarGameResponseModel tournament = new TournamentClientCarGameResponseModel();
        try {
            String url = TOURNAMENT_BASE_URL +"/"+ tournamentId;
            tournament = restTemplate
                    .getForObject(url, TournamentClientCarGameResponseModel.class);

            log.debug("5. Received in tournament id");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
        return tournament;
    }

    public TournamentClientCarGameResponseModel createNewTournament(TournamentRequestModel tournamentRequestModel){
        TournamentClientCarGameResponseModel tournamentClientCarGameResponseModel = new TournamentClientCarGameResponseModel();
        try {
            String url = TOURNAMENT_BASE_URL;
            tournamentClientCarGameResponseModel =
                    restTemplate.postForObject(url, tournamentRequestModel,
                            TournamentClientCarGameResponseModel.class);

            log.debug("5. Received in create new tournament");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            throw handleHttpClientException(ex);
        }
        return tournamentClientCarGameResponseModel;
    }

    public void deleteTournament(String tournamentId){
        try {
            String url = TOURNAMENT_BASE_URL + "/" + tournamentId;
            restTemplate.delete(url);

            log.debug("5. Received in delete tournament");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public void updateTournament(TournamentRequestModel tournamentRequestModel, String tournamentId){
        try {
            String url = TOURNAMENT_BASE_URL + "/" + tournamentId;
                    restTemplate.put(url, tournamentRequestModel);

            log.debug("5. Received in update tournament");
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {

        if(ex.getStatusCode() == HttpStatus.BAD_REQUEST){
            throw new WinnerNotInPlayerListException(getErrorMessage(ex));
        }
        if(ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY && getErrorMessage(ex).contains("The entry cost must be the same")){
            throw new IllegalEntryCostChange(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            throw new InvalidInputException(getErrorMessage(ex));
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
