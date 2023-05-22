package com.comicstore.storeservice.datalayer.Store;

import jakarta.persistence.*;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "stores")
@Data
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    private StoreIdentifier storeIdentifier;
    private LocalDate dateOpened;
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Embedded
    private ContactStore contact;

    public Store() {
    this.storeIdentifier = new StoreIdentifier();
    }

    public Store(LocalDate dateOpened, Address address, Status status) {
        this.storeIdentifier = new StoreIdentifier();
        this.dateOpened = dateOpened;
        this.address = address;
        this.status = status;
    }
}
