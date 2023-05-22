package com.comicstore.tournamentservice.domainclientlayer.Store;

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

@Slf4j
@Component
public class StoreServiceClient {


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String STORE_BASE_URL;

    public StoreServiceClient(RestTemplate restTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${app.store-service.host}") String storeServiceHost,
                                  @Value("${app.store-service.port}") String storeServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.STORE_BASE_URL = "http://" + storeServiceHost + ":" + storeServicePort + "/api/lab2/v1/stores";
    }


    public StoreResponseModel getStoreById(String storeId) {
        StoreResponseModel storeResponseModel = new StoreResponseModel();
        try {
            String url = STORE_BASE_URL +"/"+ storeId;
            storeResponseModel = restTemplate
                    .getForObject(url, StoreResponseModel.class);

            log.debug("5. Received in API-Gateway  get store id");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            handleHttpClientException(ex);
        }
        return storeResponseModel;
    }




    private void handleHttpClientException(HttpClientErrorException ex) {


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
