package com.comicstore.apigateway.domainclientlayer.Store;

import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.comicstore.apigateway.utils.exceptions.Clients.DuplicateClientInformationException;
import com.comicstore.apigateway.utils.exceptions.Clients.NoEmailAndPhoneException;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
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
public class InventoryServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String INVENTORY_BASE_URL;

    public InventoryServiceClient(RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               @Value("${app.store-service.host}") String storeServiceHost,
                               @Value("${app.store-service.port}") String storeServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.INVENTORY_BASE_URL = "http://" + storeServiceHost + ":" + storeServicePort + "/api/lab2/v1/inventories";
    }

    public InventoryResponseModel getInventoryById(String inventoryId) {
        InventoryResponseModel inventoryResponseModel = new InventoryResponseModel();
        try {
            String url = INVENTORY_BASE_URL +"/"+ inventoryId;
            inventoryResponseModel = restTemplate
                    .getForObject(url, InventoryResponseModel.class);

            log.debug("5. Received in API-Gateway get inventory id");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            handleHttpClientException(ex);
        }
        return inventoryResponseModel;
    }

    public void deleteInventory(String inventoryId){
        try {
            String url = INVENTORY_BASE_URL + "/" + inventoryId;
            restTemplate.delete(url);

            log.debug("5. Received in delete inventory");
        } catch (HttpClientErrorException ex) {
            log.debug("5.delete");
            handleHttpClientException(ex);
        }
    }

    public void updateInventory(InventoryRequestModel inventoryRequestModel, String inventoryId){
        try {
            String url = INVENTORY_BASE_URL + "/" + inventoryId ;
            restTemplate.put(url, inventoryRequestModel);

            log.debug("5. Received in update inventory");
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
    }

    private void handleHttpClientException(HttpClientErrorException ex) {


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
