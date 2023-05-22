package com.comicstore.apigateway.presentationlayer.Store.Inventory;

import com.comicstore.apigateway.businesslayer.Store.Inventory.InventoryService;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreController;
import jakarta.validation.Valid;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/lab2/v1/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @PutMapping("/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable String inventoryId, @Valid @RequestBody InventoryRequestModel inventoryRequestModel){
        inventoryService.updateInventory(inventoryId, inventoryRequestModel);
        return  ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponseModel> getInventoryById(@PathVariable String inventoryId){
        return ResponseEntity.status(HttpStatus.OK).body(addLinks(inventoryService.getInventoryById(inventoryId)));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String inventoryId){
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public static InventoryResponseModel addLinks(@MappingTarget InventoryResponseModel model){
        Link selfLink = linkTo(methodOn(InventoryController.class)
                .getInventoryById(model.getInventoryId()))
                .withSelfRel();

        model.add(selfLink);


        Link storesLink = linkTo(methodOn(StoreController.class)
                .getStores())
                .withRel("Stores");

        model.add(storesLink);


        Link storeLink = linkTo(methodOn(StoreController.class)
                .getStore(model.getStoreId()))
                .withRel("Inventory Store");

        model.add(storeLink);

        return model;
    }

}
