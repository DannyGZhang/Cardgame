package com.comicstore.clientservice.datalayer;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clients")
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    private ClientIdentifier clientIdentifier;

    @Embedded
    private StoreIdentifier storeIdentifier;
    private String firstName;

    private String lastName;

    private int rebate;

    private double totalBought;

    @Embedded
    private Contact contact;

    public Client() {
        this.clientIdentifier = new ClientIdentifier();
    }

    public Client(String firstName, String lastName, double totalBought) {
        this.clientIdentifier = new ClientIdentifier();
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalBought = totalBought;
        if(totalBought > 2000){
            this.rebate = 20;
        }else if(totalBought > 1500){
            this.rebate = 15;
        }
        else if(totalBought > 1000){
            this.rebate = 10;
        }
        else {
            this.rebate = 0;
        }
        this.contact = new Contact();
    }


}