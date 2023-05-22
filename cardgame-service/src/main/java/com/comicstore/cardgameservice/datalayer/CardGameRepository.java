package com.comicstore.cardgameservice.datalayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardGameRepository extends JpaRepository<CardGame, Integer> {
    CardGame getCardGameByCardIdentifier_CardId(String cardId);
}
