package com.comicstore.apigateway.presentationlayer.Store.Store;

import com.comicstore.apigateway.businesslayer.Client.ClientsService;
import com.comicstore.apigateway.businesslayer.Store.Inventory.InventoryService;
import com.comicstore.apigateway.businesslayer.Store.Store.StoreService;
import com.comicstore.apigateway.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.apigateway.presentationlayer.Client.ClientRequestModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientsController;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryController;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/lab2/v1/stores")
public class StoreController {
    StoreService storeService;
    InventoryService inventoryService;

    ClientsService clientsService;
    private final String postalCode = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";//this canadian postal code
    private final String phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";
    private final String dateFormat = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String emailFormat = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public StoreController(StoreService storeService, InventoryService inventoryService, ClientsService clientServiceClient) {
        this.storeService = storeService;
        this.inventoryService = inventoryService;
        this.clientsService = clientServiceClient;
    }

    @GetMapping(
            value = "/{storeId}/clients",
            produces = "application/json"
    )
    public ResponseEntity<List<ClientResponseModel>> getClientAggregatesFromStoreId(@PathVariable String storeId){
        log.debug("1, Received in get all");
        List<ClientResponseModel> clientResponseModels = Arrays.stream(clientsService.getAllClientsOfStore(storeId)).toList();
        clientResponseModels.forEach(ClientsController::addLinks);
        return ResponseEntity.ok().body(clientResponseModels);
    }

    //todo: Add links
    @PostMapping("/{storeId}/inventories")
    public ResponseEntity<InventoryResponseModel> createInventory(@PathVariable String storeId,@Valid @RequestBody InventoryRequestModel inventoryRequestModel){
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryController.addLinks(inventoryService.createInventory(storeId,inventoryRequestModel)));
    }

    @GetMapping()
    public ResponseEntity<List<StoreResponseModel>> getStores(){
        List<StoreResponseModel> storeResponseModels = Arrays.stream(storeService.getStores()).toList();
        storeResponseModels.forEach(storeResponseModel -> addLinks(storeResponseModel));
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseModels);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseModel> getStore(@PathVariable String storeId){
        return ResponseEntity.status(HttpStatus.OK).body(addLinks(storeService.getStore(storeId)));
    }


    @PostMapping()
    public ResponseEntity<StoreResponseModel> createStore(@Valid @RequestBody StoreRequestModel storeRequestModel){

        storeChecks(storeRequestModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(addLinks(storeService.createStore(storeRequestModel)));
    }

    private void storeChecks(@RequestBody @Valid StoreRequestModel storeRequestModel) {
        if (!Pattern.compile(postalCode).matcher(storeRequestModel.getPostalCode()).matches())
            throw new InvalidInputException("The postal code is not in the proper format : " + storeRequestModel.getPostalCode());

        if (!Pattern.compile(dateFormat).matcher(storeRequestModel.getDateOpened()).matches())
            throw new InvalidInputException("Date opened entered is not in format YYYY-MM-DD : " + storeRequestModel.getDateOpened());
        if (!Pattern.compile(emailFormat).matcher(storeRequestModel.getEmail()).matches())
            throw new InvalidInputException("Email entered is not valid : " + storeRequestModel.getEmail());
        if (!Pattern.compile(phoneRegex).matcher(storeRequestModel.getPhoneNumber()).matches())
            throw new InvalidInputException("Phone entered is not valid : " + storeRequestModel.getPhoneNumber());

    }

    @PutMapping("/{storeId}")
    public ResponseEntity<Void> updateStore(@Valid @RequestBody StoreRequestModel storeRequestModel, @PathVariable String storeId){
        storeChecks(storeRequestModel);
        storeService.updateStore(storeId,storeRequestModel);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{storeId}/inventories")
    public ResponseEntity<List<InventoryResponseModel>> getInventoriesByStoreId(@PathVariable String storeId){
        List<InventoryResponseModel> inventoryResponseModels = Arrays.stream(storeService.getInventoryByStoreId(storeId)).toList();
        inventoryResponseModels.forEach(inventoryResponseModel -> InventoryController.addLinks(inventoryResponseModel));
        return ResponseEntity.status(HttpStatus.OK).body(inventoryResponseModels);
    }


    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable String storeId){
        storeService.deleteStore(storeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    public static StoreResponseModel addLinks(@MappingTarget StoreResponseModel model){
        Link selfLink = linkTo(methodOn(StoreController.class)
                .getStore(model.getStoreId()))
                .withSelfRel();

        model.add(selfLink);


        Link storesLink = linkTo(methodOn(StoreController.class)
                .getStores())
                .withRel("Stores");

        model.add(storesLink);

        Link inventoriesForStoreLink = linkTo(methodOn(StoreController.class)
                .getInventoriesByStoreId(model.getStoreId()))
                .withRel("Inventories for store");

        model.add(inventoriesForStoreLink);

        return model;
    }
}
