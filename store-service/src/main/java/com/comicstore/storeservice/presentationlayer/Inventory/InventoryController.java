package com.comicstore.storeservice.presentationlayer.Inventory;

import com.comicstore.storeservice.businesslayer.Inventory.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/lab2/v1/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponseModel> updateInventory(@PathVariable String inventoryId, @Valid @RequestBody InventoryRequestModel inventoryRequestModel){
        return  ResponseEntity.status(HttpStatus.OK).body(inventoryService.updateInventory(inventoryId, inventoryRequestModel));
    }

    @GetMapping()
    public ResponseEntity<List<InventoryResponseModel>> getInventories(){
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getInventories());
    }
    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponseModel> getInventoryById(@PathVariable String inventoryId){
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getInventoryById(inventoryId));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String inventoryId){
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
