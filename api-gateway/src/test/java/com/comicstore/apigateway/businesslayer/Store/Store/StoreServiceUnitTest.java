package com.comicstore.apigateway.businesslayer.Store.Store;

import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class StoreServiceUnitTest {

    @Autowired
    StoreService storeService;

    @MockBean
    StoreServiceClient storeServiceClient;

    @Test
    void createStore_ShouldReturnCreatedStore() {
        StoreResponseModel storeResponseModel = new StoreResponseModel("6428645f-d56d-4ee5-841e-f7cee01e4cc3",
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel.getCity())
                .dateOpened(storeResponseModel.getDateOpened().toString())
                .email(storeResponseModel.getEmail())
                .phoneNumber(storeResponseModel.getPhoneNumber())
                .postalCode(storeResponseModel.getPostalCode())
                .province(storeResponseModel.getProvince())
                .streetAddress(storeResponseModel.getStreetAddress())
                .status(storeResponseModel.getStatus())
                .build();

        when(storeServiceClient.createNewStore(storeRequestModel))
                .thenReturn(storeResponseModel);

        StoreResponseModel returned = storeService.createStore(storeRequestModel);

        MatcherAssert.assertThat(storeResponseModel, samePropertyValuesAs(returned));


    }

    @Test
    void updateStoreWithInvalidId_ShouldThrowNotFoundException() {
        StoreResponseModel storeResponseModel = new StoreResponseModel("invalid",
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel.getCity())
                .dateOpened(storeResponseModel.getDateOpened().toString())
                .email(storeResponseModel.getEmail())
                .phoneNumber(storeResponseModel.getPhoneNumber())
                .postalCode(storeResponseModel.getPostalCode())
                .province(storeResponseModel.getProvince())
                .streetAddress(storeResponseModel.getStreetAddress())
                .status(storeResponseModel.getStatus())
                .build();

        doThrow(new NotFoundException()).when(storeServiceClient).updateStore(storeRequestModel, storeResponseModel.getStoreId());


        assertThrows(NotFoundException.class, () -> {
            storeService.updateStore(storeResponseModel.getStoreId(),storeRequestModel);
        });

    }
}