package com.comicstore.apigateway.presentationlayer.Store.Inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InventoryResponseModel extends RepresentationModel<InventoryResponseModel> {
    private String inventoryId;

    private String storeId;

    private LocalDate lastUpdated;

    private String type;
    private String status;
}
