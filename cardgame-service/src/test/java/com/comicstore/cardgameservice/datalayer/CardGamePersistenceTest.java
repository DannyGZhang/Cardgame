package com.comicstore.cardgameservice.datalayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CardGamePersistenceTest {
    @Autowired
    private CardGameRepository cardGameRepository;


    private CardGame presavedCardGame;

    @BeforeEach
    public void setup(){
        cardGameRepository.deleteAll();
        presavedCardGame = cardGameRepository.save(
                new CardGame("MTG","Wizard", LocalDate.now(),true));
    }


    @Test
    void getCardGameByValidCardIdentifier_ShouldSucceed() {
        CardGame found = cardGameRepository.getCardGameByCardIdentifier_CardId(presavedCardGame.getCardIdentifier().getCardId());

        assertNotNull(found);
        assertThat(found, samePropertyValuesAs(presavedCardGame));
    }


    @Test
    void updateCardGameWithValidValues_ShouldSucceed() {
        String expectedName = "Magic the Gathering";
        presavedCardGame.setCardGameName(expectedName);
        cardGameRepository.save(presavedCardGame);

        CardGame found = cardGameRepository.getCardGameByCardIdentifier_CardId(presavedCardGame.getCardIdentifier().getCardId());
        assertEquals(found.getCardGameName(),expectedName);
    }

    @Test
    void deleteCardGameWithValidId_ShouldReturnNull(){
        cardGameRepository.delete(cardGameRepository.getCardGameByCardIdentifier_CardId(presavedCardGame.getCardIdentifier().getCardId()));
        assertNull(cardGameRepository.getCardGameByCardIdentifier_CardId(presavedCardGame.getCardIdentifier().getCardId()));
    }

    @Test
    void createCardGameWithValidValues_ShouldSucceed(){
        String expectedName = "Magic the Gathering";
        String expectedCompany = "Wizard";
        LocalDate expectedRelease = LocalDate.now();
        Boolean expectedActive = false;

        CardGame newCardGame = new CardGame(expectedName,expectedCompany, expectedRelease,expectedActive);

        CardGame saved = cardGameRepository.save(newCardGame);

        assertEquals(saved.getCardGameName(),expectedName);
        assertEquals(saved.getCompany(),expectedCompany);
        assertEquals(saved.getReleaseDate(),expectedRelease);
        assertEquals(saved.getIsActive(),expectedActive);

        assertNotNull(saved.getCardIdentifier().getCardId());
        assertNotNull(saved.getId());

    }



}