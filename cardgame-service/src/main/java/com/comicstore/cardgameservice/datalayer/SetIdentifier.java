package com.comicstore.cardgameservice.datalayer;

import jakarta.persistence.Embeddable;

import java.util.UUID;
@Embeddable
public class SetIdentifier {
    private String setId;

    public SetIdentifier() {
        this.setId = UUID.randomUUID().toString();
    }
    public SetIdentifier(String setId) {
        this.setId = setId;
    }



    public String getSetId() {
        return this.setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }
}
