package com.comicstore.clientservice.datalayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ClientIdentifier {
    @Column(name = "client_id")
    private String clientId;

    public ClientIdentifier() {
        this.clientId = UUID.randomUUID().toString();
    }


    public String getClientId() {
        return this.clientId;
    }

}
