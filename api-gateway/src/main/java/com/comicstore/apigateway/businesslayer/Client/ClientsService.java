package com.comicstore.apigateway.businesslayer.Client;

import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;

import java.util.List;

public interface ClientsService {
    ClientResponseModel getClientAggregateById(String clientId);

    ClientResponseModel createNewClient(ClientRequestModel clientRequestModel);

    void updateClient(ClientRequestModel clientRequestModel, String clientId);

    void deleteClient(String clientId);

    ClientResponseModel[] getAllClients();

    ClientResponseModel[] getAllClientsOfStore(String storeId);
}
