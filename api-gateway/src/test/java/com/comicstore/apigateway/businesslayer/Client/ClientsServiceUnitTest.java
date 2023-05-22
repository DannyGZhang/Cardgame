package com.comicstore.apigateway.businesslayer.Client;

import com.comicstore.apigateway.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.ClientIdentifier;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClientsServiceUnitTest {

    @Autowired
    ClientsService clientsService;
    @MockBean
    private ClientServiceClient clientServiceClient;

    @MockBean
    private StoreServiceClient storeServiceClient;

    @Test
    public void createClientWithInvalidStoreId_ShouldReturnNotfoundException(){

        String storeId = "1";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";


        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();

        when(storeServiceClient.getStoreById(storeId))
                .thenThrow(new NotFoundException("Store was not found !"));


        assertThrows(NotFoundException.class, () -> {
            clientsService.createNewClient(clientRequestModel);
        });
    }


    @Test
    public void createClientWithValidStoreId_ShouldReturnCreatedClient(){

        String storeId = "6428645f-d56d-4ee5-841e-f7cee01e4cc3";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";
        ClientResponseModel clientResponseModel = new ClientResponseModel(storeId,"d20fe81c-7a78-493b-bcd8-4f53dc99f285",firstName,lastName,20,total,email,phone);
       StoreResponseModel storeResponseModel = new StoreResponseModel("6428645f-d56d-4ee5-841e-f7cee01e4cc3",
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();

        when(storeServiceClient.getStoreById(storeId))
                .thenReturn(storeResponseModel);
        when(clientServiceClient.createNewClient(clientRequestModel))
                .thenReturn(clientResponseModel);

        ClientResponseModel returned = clientsService.createNewClient(clientRequestModel);

        MatcherAssert.assertThat(clientResponseModel, samePropertyValuesAs(returned));

    }


}



