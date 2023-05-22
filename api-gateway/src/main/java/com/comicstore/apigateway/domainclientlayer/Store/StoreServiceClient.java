package com.comicstore.apigateway.domainclientlayer.Store;

import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import com.comicstore.apigateway.utils.exceptions.StoreInventory.DuplicateStoreLocationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Store;
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


    public StoreResponseModel[] getStores() {
        StoreResponseModel storeResponseModels[] = null;
        try {
            String url = STORE_BASE_URL;
            storeResponseModels = restTemplate
                    .getForObject(url, StoreResponseModel[].class);

            log.debug("5. Received in API-Gateway  get stores");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            handleHttpClientException(ex);
        }
        return storeResponseModels;
    }

    public InventoryResponseModel[] getStoreInventories(String storeId) {
        InventoryResponseModel inventoryResponseModels[] = null;
        try {
            String url = STORE_BASE_URL + "/" + storeId + "/inventories";
            inventoryResponseModels = restTemplate
                    .getForObject(url, InventoryResponseModel[].class);

            log.debug("5. Received in API-Gateway  get store inventories");
        } catch (HttpClientErrorException ex) {
            log.debug("5.Error");
            handleHttpClientException(ex);
        }
        return inventoryResponseModels;
    }


    public void deleteStore(String storeId){
        try {
            String url = STORE_BASE_URL + "/" + storeId;
            restTemplate.delete(url);

            log.debug("5. Received in delete store");
        } catch (HttpClientErrorException ex) {
            log.debug("5.delete");
            handleHttpClientException(ex);
        }
    }


    public StoreResponseModel createNewStore(StoreRequestModel storeRequestModel){
        StoreResponseModel storeResponseModel = new StoreResponseModel();
        try {
            String url = STORE_BASE_URL;
            storeResponseModel =
                    restTemplate.postForObject(url, storeRequestModel,
                            StoreResponseModel.class);

            log.debug("5. Received in createNewClient");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            handleHttpClientException(ex);
        }
        return storeResponseModel;
    }

    public InventoryResponseModel createNewInventory(InventoryRequestModel inventoryRequestModel, String storeId){
        InventoryResponseModel inventoryResponseModel = new InventoryResponseModel();
        try {
            String url = STORE_BASE_URL + "/" + storeId + "/inventories";
            inventoryResponseModel =
                    restTemplate.postForObject(url, inventoryRequestModel,
                            InventoryResponseModel.class);

            log.debug("5. Received in create new inventory");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            handleHttpClientException(ex);
        }
        return inventoryResponseModel;
    }



    public void updateStore(StoreRequestModel storeRequestModel, String storeId){
        try {
            String url = STORE_BASE_URL + "/" + storeId ;
            restTemplate.put(url, storeRequestModel);

            log.debug("5. Received in update store");
        } catch (HttpClientErrorException ex) {
            log.debug("5.");
            handleHttpClientException(ex);
        }
    }
    private void handleHttpClientException(HttpClientErrorException ex) {

        if(ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY && getErrorMessage(ex).contains("occupied by another store")){
            throw new DuplicateStoreLocationException(getErrorMessage(ex));
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
