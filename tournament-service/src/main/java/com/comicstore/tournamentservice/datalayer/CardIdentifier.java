package com.comicstore.tournamentservice.datalayer;


import java.util.UUID;

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
