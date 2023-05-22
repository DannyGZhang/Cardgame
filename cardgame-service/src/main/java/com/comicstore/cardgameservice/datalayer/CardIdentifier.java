package com.comicstore.cardgameservice.datalayer;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class CardIdentifier {
    private String cardId;

    public CardIdentifier() {
        this.cardId = UUID.randomUUID().toString();
    }
    public CardIdentifier(String cardId) {
        this.cardId = cardId;
    }



    public String getCardId() {
        return this.cardId;
    }

}
