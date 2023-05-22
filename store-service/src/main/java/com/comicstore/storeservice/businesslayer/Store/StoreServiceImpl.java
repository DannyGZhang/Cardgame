package com.comicstore.storeservice.businesslayer.Store;

import com.comicstore.storeservice.Utils.Exceptions.DuplicateStoreLocationException;
import com.comicstore.storeservice.Utils.Exceptions.InvalidInputException;
import com.comicstore.storeservice.Utils.Exceptions.NotFoundException;
import com.comicstore.storeservice.datalayer.Inventory.InventoryRepository;
import com.comicstore.storeservice.datalayer.Inventory.Type;
import com.comicstore.storeservice.datalayer.Store.*;
import com.comicstore.storeservice.datamapperlayer.Inventory.InventoryResponseMapper;
import com.comicstore.storeservice.datamapperlayer.Store.StoreClientsResponseMapper;
import com.comicstore.storeservice.datamapperlayer.Store.StoreRequestMapper;
import com.comicstore.storeservice.datamapperlayer.Store.StoreResponseMapper;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryResponseModel;
import com.comicstore.storeservice.presentationlayer.Store.StoreRequestModel;
import com.comicstore.storeservice.presentationlayer.Store.StoreResponseModel;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StoreServiceImpl implements StoreService {
    //Repositories
    StoreRepository storeRepository;
    InventoryRepository inventoryRepository;


    //Mappers
    StoreClientsResponseMapper storeClientsResponseMapper;
    StoreResponseMapper storeResponseMapper;
    InventoryResponseMapper inventoryResponseMapper;
    StoreRequestMapper storeRequestMapper;

    public StoreServiceImpl(StoreRepository storeRepository,/* ClientRepository clientRepository, */InventoryRepository inventoryRepository, /*ClientResponseMapper clientResponseMapper,*/ StoreClientsResponseMapper storeClientsResponseMapper, StoreResponseMapper storeResponseMapper, InventoryResponseMapper inventoryResponseMapper, StoreRequestMapper storeRequestMapper) {
        this.storeRepository = storeRepository;
        //this.clientRepository = clientRepository;
        this.inventoryRepository = inventoryRepository;
        //this.clientResponseMapper = clientResponseMapper;
        this.storeClientsResponseMapper = storeClientsResponseMapper;
        this.storeResponseMapper = storeResponseMapper;
        this.inventoryResponseMapper = inventoryResponseMapper;
        this.storeRequestMapper = storeRequestMapper;
    }



    @Override
    public List<StoreResponseModel> getStores() {
        return storeResponseMapper.entitiesToResponseModel(storeRepository.findAll());
    }

    @Override
    public StoreResponseModel getStore(String storeId) {
        Store store = storeRepository.findByStoreIdentifier_StoreId(storeId);
        if (store == null) {
            throw new NotFoundException("Store with id : " + storeId +" was not found !");
        }
        return storeResponseMapper.entityToResponseModel(store);
    }

    @Override
    public StoreResponseModel createStore(StoreRequestModel storeRequestModel) {
        if (!findByStatus(storeRequestModel.getStatus())) {
            throw new InvalidInputException("Status entered is not a valid type : " + storeRequestModel.getStatus());
        }
        Store store = storeRequestMapper.requestModelToEntity(storeRequestModel);
        store.setAddress(new Address(storeRequestModel.getStreetAddress(), storeRequestModel.getCity(), storeRequestModel.getProvince(), storeRequestModel.getPostalCode()));
        store.setContact(new ContactStore(storeRequestModel.getEmail(), storeRequestModel.getPhoneNumber()));
        return getStoreResponseModel(storeRequestModel, store);
    }

    @Override
    public StoreResponseModel updateStore(String storeId,StoreRequestModel storeRequestModel) {
        if (!findByStatus(storeRequestModel.getStatus())) {
            throw new InvalidInputException("Status entered is not a valid type : " + storeRequestModel.getStatus());
        }
        Store existingStore = storeRepository.findByStoreIdentifier_StoreId(storeId);
        if(existingStore == null)
            throw new NotFoundException("Store with id : " + storeId +" was not found !");


        Store store = storeRequestMapper.requestModelToEntity(storeRequestModel);
        store.setAddress(new Address(storeRequestModel.getStreetAddress(), storeRequestModel.getCity(), storeRequestModel.getProvince(), storeRequestModel.getPostalCode()));

        store.setContact(new ContactStore(storeRequestModel.getEmail(), storeRequestModel.getPhoneNumber()));

        store.setStoreIdentifier(new StoreIdentifier(existingStore.getStoreIdentifier().getStoreId()));

        store.setId(existingStore.getId());
        return getStoreResponseModel(storeRequestModel, store);
    }

    private StoreResponseModel getStoreResponseModel(StoreRequestModel storeRequestModel, Store store) throws DuplicateStoreLocationException {
        try {
            return storeResponseMapper.entityToResponseModel(storeRepository.save(store));
        } catch (
                DataAccessException ex) {
            if(ex.getCause().toString().contains("ConstraintViolationException")){
                throw new DuplicateStoreLocationException();
            }
            else if (ex.getMessage().contains("constraint [street_address]")) {
                throw new DuplicateStoreLocationException("Street address is already occupied by another store : " + storeRequestModel.getStreetAddress());
            } else if (ex.getMessage().contains("constraint [postal_code]")) {
                throw new DuplicateStoreLocationException("Postal code is already occupied by another store : " + storeRequestModel.getPostalCode());
            } else throw new InvalidInputException(("An unknown error as occurred"));
        }
    }

    @Override
        public List<InventoryResponseModel> getInventoryByStoreId (String storeId){
            if(!storeRepository.existsByStoreIdentifier_StoreId(storeId)){
                throw new NotFoundException("Store with id : " + storeId +" was not found !");
            }
            return inventoryResponseMapper.entitiesToResponseModel(inventoryRepository.getInventoriesByStoreIdentifier_StoreId(storeId));
        }

    @Override
    public void deleteStore(String storeId) {
        Store store = storeRepository.findByStoreIdentifier_StoreId(storeId);
        store.setStatus(Status.CLOSED);

        storeRepository.save(store);
    }


    public static Boolean findByStatus(String statusStr) {
        boolean found = false;
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(statusStr)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
