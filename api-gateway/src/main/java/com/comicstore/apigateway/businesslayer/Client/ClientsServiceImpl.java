package com.comicstore.apigateway.businesslayer.Client;


import com.comicstore.apigateway.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.apigateway.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientsServiceImpl implements ClientsService{
    private ClientServiceClient clientServiceClient;

    private StoreServiceClient storeServiceClient;

    public ClientsServiceImpl(ClientServiceClient clientServiceClient, StoreServiceClient storeServiceClient) {
        this.clientServiceClient = clientServiceClient;
        this.storeServiceClient = storeServiceClient;
    }


    @Override
    public ClientResponseModel getClientAggregateById(String clientId) {
        return clientServiceClient.getClientAggregateById(clientId);

    }

    @Override
    public ClientResponseModel createNewClient(ClientRequestModel clientRequestModel) {
        storeServiceClient.getStoreById(clientRequestModel.getStoreId());
        return clientServiceClient.createNewClient(clientRequestModel);
    }

    @Override
    public void updateClient(ClientRequestModel clientRequestModel, String clientId) {
        storeServiceClient.getStoreById(clientRequestModel.getStoreId());
        clientServiceClient.updateClient(clientRequestModel,clientId);
    }

    @Override
    public void deleteClient(String clientId) {
        clientServiceClient.deleteClient(clientId);
    }

    @Override
    public ClientResponseModel[] getAllClients() {
        return clientServiceClient.getAllClient();
    }

    @Override
    public ClientResponseModel[] getAllClientsOfStore(String storeId) {
        return clientServiceClient.getAllClientFromStoreId(storeId);
    }
}
