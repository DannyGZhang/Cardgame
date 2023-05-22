package com.comicstore.storeservice.presentationlayer.Store;

import com.comicstore.storeservice.Utils.Exceptions.InvalidInputException;
import com.comicstore.storeservice.businesslayer.Inventory.InventoryService;
import com.comicstore.storeservice.businesslayer.Store.StoreService;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryRequestModel;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryResponseModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/lab2/v1/stores")
public class StoreController {
    StoreService storeService;
    InventoryService inventoryService;


    private final String postalCode = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";//this canadian postal code
    private final String phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";
    private final String dateFormat = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String emailFormat = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public StoreController(StoreService storeService, InventoryService inventoryService) {
        this.storeService = storeService;
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{storeId}/inventories")
    public ResponseEntity<InventoryResponseModel> createInventory(@PathVariable String storeId,@Valid @RequestBody InventoryRequestModel inventoryRequestModel){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(storeId,inventoryRequestModel));
    }

    @GetMapping()
    public ResponseEntity<List<StoreResponseModel>> getStores(){
        return ResponseEntity.status(HttpStatus.OK).body(storeService.getStores());
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseModel> getStore(@PathVariable String storeId){
        return ResponseEntity.status(HttpStatus.OK).body(storeService.getStore(storeId));
    }



    @PostMapping()
    public ResponseEntity<StoreResponseModel> createStore(@Valid @RequestBody StoreRequestModel storeRequestModel){

        storeChecks(storeRequestModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(storeRequestModel));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponseModel> updateStore(@Valid @RequestBody StoreRequestModel storeRequestModel, @PathVariable String storeId){
        storeChecks(storeRequestModel);

        return ResponseEntity.status(HttpStatus.OK).body(storeService.updateStore(storeId,storeRequestModel));
    }


    @GetMapping("/{storeId}/inventories")
    public ResponseEntity<List<InventoryResponseModel>> getInventoriesByStoreId(@PathVariable String storeId){
        return ResponseEntity.status(HttpStatus.OK).body(storeService.getInventoryByStoreId(storeId));
    }


    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable String storeId){
        storeService.deleteStore(storeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
