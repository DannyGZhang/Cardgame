package com.comicstore.tournamentservice.datalayer;


import java.util.UUID;

public class ClientIdentifier {
    private String clientId;

    public ClientIdentifier() {
        this.clientId = UUID.randomUUID().toString();
    }

    public ClientIdentifier(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return this.clientId;
    }

}
