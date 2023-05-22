package com.comicstore.apigateway.domainclientlayer.Client;

import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
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

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class ClientServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String CLIENT_SERVICE_BASE_URL;

    public ClientServiceClient(RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               @Value("${app.clients-service.host}") String clientServiceHost,
                               @Value("${app.clients-service.port}") String clientServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.CLIENT_SERVICE_BASE_URL = "http://" + clientServiceHost + ":" + clientServicePort + "/api/lab2/v1/stores";
    }

    public ClientResponseModel getClientAggregateById(String clientId) {
        ClientResponseModel clientResponseModel = new ClientResponseModel();
        try {
            String url = CLIENT_SERVICE_BASE_URL +"/clients/"+ clientId;
            clientResponseModel = restTemplate
                    .getForObject(url, ClientResponseModel.class);

            log.debug("5. Received in API-Gateway Client Service Client getClientAggregate with clientResponseModel : " + clientResponseModel.getClientId());
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
        return clientResponseModel;
    }

    public ClientResponseModel createNewClient(ClientRequestModel clientRequestModel){
        ClientResponseModel clientResponseModel = new ClientResponseModel();
        try {
            String url = CLIENT_SERVICE_BASE_URL + "/clients";
            clientResponseModel =
                    restTemplate.postForObject(url, clientRequestModel,
                            ClientResponseModel.class);

            log.debug("5. Received in createNewClient");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            handleHttpClientException(ex);
        }
        return clientResponseModel;
    }
    public void updateClient(ClientRequestModel clientRequestModel, String clientId){
        try {
            String url = CLIENT_SERVICE_BASE_URL + "/clients/" + clientId ;
            restTemplate.put(url, clientRequestModel);

            log.debug("5. Received in updateClient");
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
    }

    public void deleteClient(String clientId){
        try {
            String url = CLIENT_SERVICE_BASE_URL + "/clients/" + clientId;
            restTemplate.delete(url);

            log.debug("5. Received in delete client");
        } catch (HttpClientErrorException ex) {
            log.debug("5.delete");
            handleHttpClientException(ex);
        }
    }

    public ClientResponseModel[] getAllClientFromStoreId(String storeId){
        ClientResponseModel clientResponseModels[] = null;
        try {
            String url = CLIENT_SERVICE_BASE_URL +"/"+ storeId + "/clients";
            clientResponseModels = restTemplate
                    .getForObject(url, ClientResponseModel[].class);
            log.debug("5. Received in get store clients");
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
        return clientResponseModels;
    }


    public ClientResponseModel[] getAllClient(){
        ClientResponseModel clientResponseModels[] = null;
        try {
            String url = CLIENT_SERVICE_BASE_URL + "/clients";
            clientResponseModels = restTemplate
                    .getForObject(url, ClientResponseModel[].class);
            log.debug("5. Received in get store clients");
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
        return clientResponseModels;
    }


    private void handleHttpClientException(HttpClientErrorException ex) {

        if(ex.getStatusCode() == HttpStatus.BAD_REQUEST){
            throw new NoEmailAndPhoneException(getErrorMessage(ex));
        }
        if(ex.getStatusCode() == CONFLICT){
            throw new DuplicateClientInformationException(getErrorMessage(ex));
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
