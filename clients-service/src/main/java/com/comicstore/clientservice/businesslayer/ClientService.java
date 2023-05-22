package com.comicstore.clientservice.businesslayer;


import com.comicstore.clientservice.presentationlayer.ClientRequestModel;
import com.comicstore.clientservice.presentationlayer.ClientResponseModel;

import java.util.List;

public interface ClientService {
    List<ClientResponseModel> getClients();

    List<ClientResponseModel> getStoreClients(String storeId);


        ClientResponseModel getClientById(String clientId);

    ClientResponseModel createClient(ClientRequestModel clientRequestModel);

    ClientResponseModel updateClient(ClientRequestModel clientRequestModel, String clientId);

    void deleteClient(String clientId);

}
