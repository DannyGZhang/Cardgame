package com.comicstore.cardgameservice.datalayer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRepository  extends JpaRepository<Set, Integer> {
    List<Set> getSetByCardIdentifier_CardId(String cardId);

    Set getSetBySetIdentifier_SetId(String setId);
}
