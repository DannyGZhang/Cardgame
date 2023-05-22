package com.comicstore.storeservice.datalayer.Inventory;

import com.comicstore.storeservice.datalayer.Store.StoreIdentifier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "inventories")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private InventoryIdentifier inventoryIdentifier;

    @NotNull
    @Embedded
    private StoreIdentifier storeIdentifier;

    private LocalDate lastUpdated;
    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;


    public Inventory() {
        this.inventoryIdentifier = new InventoryIdentifier();
    }

    public Inventory(StoreIdentifier storeIdentifier, LocalDate lastUpdated, Type type, InventoryStatus status) {
        inventoryIdentifier = new InventoryIdentifier();
        this.storeIdentifier = storeIdentifier;
        this.lastUpdated = lastUpdated;
        this.type = type;
        this.status = status;
    }
}
