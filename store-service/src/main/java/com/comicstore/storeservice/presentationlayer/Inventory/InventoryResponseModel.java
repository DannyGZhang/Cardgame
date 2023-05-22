package com.comicstore.storeservice.presentationlayer.Inventory;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class InventoryResponseModel extends RepresentationModel<InventoryResponseModel> {
    private String inventoryId;

    private String storeId;

    private LocalDate lastUpdated;

    private String type;
    private String status;
}
