package com.comicstore.clientservice.businesslayer;

import com.comicstore.clientservice.Utils.Exceptions.DuplicateClientInformationException;
import com.comicstore.clientservice.Utils.Exceptions.InvalidInputException;
import com.comicstore.clientservice.Utils.Exceptions.NoEmailAndPhoneException;
import com.comicstore.clientservice.Utils.Exceptions.NotFoundException;
import com.comicstore.clientservice.datalayer.Client;
import com.comicstore.clientservice.datalayer.ClientIdentifier;
import com.comicstore.clientservice.datalayer.ClientRepository;
import com.comicstore.clientservice.datalayer.StoreIdentifier;
import com.comicstore.clientservice.datamapperlayer.ClientResponseMapper;
import com.comicstore.clientservice.presentationlayer.ClientRequestModel;
import com.comicstore.clientservice.presentationlayer.ClientResponseModel;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    //private final TournamentRepository tournamentRepository;
    private final ClientResponseMapper clientResponseMapper;


    public ClientServiceImpl(ClientRepository clientRepository, /*TournamentRepository tournamentRepository, */ ClientResponseMapper clientResponseMapper) {
        this.clientRepository = clientRepository;
        //this.tournamentRepository = tournamentRepository;
        this.clientResponseMapper = clientResponseMapper;
    }

    @Override
    public List<ClientResponseModel> getStoreClients(String storeId) {
        /*
        Store store = storeRepository.findByStoreIdentifier_StoreId(storeId);
        if(store == null){
            return null;
        }
        */
        return clientResponseMapper.entityListToResponseModelList(clientRepository.findClientByStoreIdentifier_StoreId(storeId));



    }
    @Override
    public List<ClientResponseModel> getClients() {
        return clientResponseMapper.entityListToResponseModelList(clientRepository.findAll());
    }

    @Override
    public ClientResponseModel getClientById(String clientId) {
        Client client = clientRepository.findClientByClientIdentifier_ClientId(clientId);
        if(client == null){
            throw new NotFoundException("No client found with id : " + clientId);
        }
        return clientResponseMapper.entityToResponseModel(client);
    }

    @Override
    public ClientResponseModel createClient(ClientRequestModel clientRequestModel) {


        if(clientRequestModel.getPhoneNumber() == null && clientRequestModel.getEmail() == null){
            throw new NoEmailAndPhoneException("You must enter an email or a phone number");
        }

        if( clientRepository.existsByFirstNameAndLastName(clientRequestModel.getFirstName(),clientRequestModel.getLastName())){
            throw new DuplicateClientInformationException("A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !");
        }

        Client client = new Client(clientRequestModel.getFirstName(),clientRequestModel.getLastName(),
                clientRequestModel.getTotalBought());
        if(clientRequestModel.getEmail() !=null)
            client.getContact().setEmail(clientRequestModel.getEmail());

        if(clientRequestModel.getPhoneNumber() != null)
            client.getContact().setPhoneNumber(clientRequestModel.getPhoneNumber());

        client.setClientIdentifier(new ClientIdentifier());
        //need to check if store exists
        client.setStoreIdentifier(new StoreIdentifier(clientRequestModel.getStoreId()));
        try {
            return clientResponseMapper.entityToResponseModel(clientRepository.save(client));
        }
        catch (DataAccessException ex){
            if(ex.getMessage().contains("constraint [email]") ||
                    ex.getCause().toString().contains("ConstraintViolationException")){
                throw new DuplicateClientInformationException("Email provided is a duplicate: "+ clientRequestModel.getEmail() );
            }
            else throw new InvalidInputException("An unknown error as occurred");
        }
    }

    @Override
    public ClientResponseModel updateClient(ClientRequestModel clientRequestModel, String clientId) {

        //this finds the client with last name and first name, then compares the id, if they are the same that means
        //that the one found is the one we are currently modifying
        Client checkerClient = clientRepository.findClientByFirstNameAndLastName(clientRequestModel.getFirstName(),clientRequestModel.getLastName());
        if(checkerClient != null && !clientId.equals(checkerClient.getClientIdentifier().getClientId())) {
                throw new DuplicateClientInformationException("A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !");
        }


        //continue if everything is good
        Client existingClient = clientRepository.findClientByClientIdentifier_ClientId(clientId);
        if(existingClient == null){
            throw new NotFoundException("No client found with id : " + clientId);
        }
        Client client = new Client(clientRequestModel.getFirstName(),clientRequestModel.getLastName(),
                clientRequestModel.getTotalBought());

        if(clientRequestModel.getEmail() !=null)
            client.getContact().setEmail(clientRequestModel.getEmail());
        else if (existingClient.getContact().getEmail() != null) {
            client.getContact().setEmail(existingClient.getContact().getEmail());
        }

        if(clientRequestModel.getPhoneNumber() != null)
            client.getContact().setPhoneNumber(clientRequestModel.getPhoneNumber());
        else if(existingClient.getContact().getPhoneNumber() != null)
            client.getContact().setPhoneNumber(existingClient.getContact().getPhoneNumber());
        client.setClientIdentifier(existingClient.getClientIdentifier());
        client.setId(existingClient.getId());
//check if store exists

            client.setStoreIdentifier(new StoreIdentifier(clientRequestModel.getStoreId()));


        return clientResponseMapper.entityToResponseModel(clientRepository.save(client));
    }

    @Override
    public void deleteClient(String clientId) {
        Client client = clientRepository.findClientByClientIdentifier_ClientId(clientId);

        if(client == null)
            throw new NotFoundException("No client found with id : " + clientId);
        /*
        List<Tournament> tournaments = tournamentRepository.getTournamentsByPlayers(client.getClientIdentifier());
        if(tournaments == null)
            return;
        tournamentRepository.deleteAll();
*/
        clientRepository.delete(client);

    }


}
