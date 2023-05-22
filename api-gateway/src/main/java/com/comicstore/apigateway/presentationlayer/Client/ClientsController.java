package com.comicstore.apigateway.presentationlayer.Client;

import com.comicstore.apigateway.businesslayer.Client.ClientsService;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreController;
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
@RequestMapping("api/lab2/v1/stores/clients")
public class ClientsController {
    private final static String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private final static String phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";

    private ClientsService clientsService;

    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @PostMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<ClientResponseModel> createNewClient(@Valid @RequestBody ClientRequestModel clientRequestModel){
        log.debug("1, Received in api-gateway client create");
        ClientsController.ClientExceptions(clientRequestModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(ClientsController.addLinks(clientsService.createNewClient(clientRequestModel)));
    }

    @GetMapping(
            value = "/{clientId}",
            produces = "application/json"
    )
    public ResponseEntity<ClientResponseModel> getClientAggregateById(@PathVariable String clientId){
        log.debug("1, Received in api-gateway inventories controller getClientAggregate with clientId: " + clientId);
        return ResponseEntity.ok().body(addLinks(clientsService.getClientAggregateById(clientId)));
    }



    @PutMapping(
            value = "/{clientId}",
            produces = "application/json"
    )
    public ResponseEntity<ClientResponseModel> updateClient(@Valid @RequestBody ClientRequestModel clientRequestModel, @PathVariable String clientId){
        log.debug("1. Received in api-gateway client update");
        ClientExceptions(clientRequestModel);

        clientsService.updateClient(clientRequestModel,clientId);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping(
            value = "/{clientId}",
            produces = "application/json"
    )
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId){
        log.debug("1. Received in api-gateway client update");
        clientsService.deleteClient(clientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }





    @GetMapping()
    public ResponseEntity<List<ClientResponseModel>> getClients() {
        List<ClientResponseModel> clientResponseModels = Arrays.stream(clientsService.getAllClients()).toList();
        clientResponseModels.forEach(ClientsController::addLinks);
        return ResponseEntity.status(HttpStatus.OK).body(clientResponseModels);
    }

    public static ClientResponseModel addLinks(@MappingTarget ClientResponseModel model){
        Link selfLink = linkTo(methodOn(ClientsController.class)
                .getClientAggregateById(model.getClientId()))
                .withSelfRel();
        model.add(selfLink);

        Link clientsLink = linkTo(methodOn(StoreController.class)
                .getClientAggregatesFromStoreId(model.getStoreId()))
                .withRel("Clients for store " + model.getStoreId());

        model.add(clientsLink);

        Link storesLink = linkTo(methodOn(StoreController.class)
                .getStores())
                .withRel("Stores");

        model.add(storesLink);

        Link storeLink = linkTo(methodOn(StoreController.class)
                .getStore(model.getStoreId())).withRel("Store : "+model.getStoreId());

        model.add(storeLink);

        return model;
    }
    public static void ClientExceptions(@RequestBody @Valid ClientRequestModel clientRequestModel) {
        if (clientRequestModel.getTotalBought() < 0)
            throw new InvalidInputException("A client can't have a negative total bought : " + clientRequestModel.getTotalBought());

        if (clientRequestModel.getEmail() != null && !Pattern.compile(emailRegex).matcher(clientRequestModel.getEmail())
                .matches())
            throw new InvalidInputException("Email is in an invalid format ! : " + clientRequestModel.getEmail());

        if (clientRequestModel.getPhoneNumber() != null && !Pattern.compile(phoneRegex).matcher(clientRequestModel.getPhoneNumber())
                .matches())
            throw new InvalidInputException("Phone number is in an invalid format ! : " + clientRequestModel.getPhoneNumber());
    }
}
