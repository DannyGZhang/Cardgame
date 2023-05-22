package com.comicstore.cardgameservice.datalayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SetPersistenceTest {
    @Autowired
    private SetRepository setRepository;

    private final String VALID_CARDGAME_ID = "6dfc9e76-1aa7-4786-8398-f4ae25737324";
    private Set presavedSet;


    @BeforeEach
    public void setup(){
        setRepository.deleteAll();
        presavedSet = setRepository.save(
                new Set(new CardIdentifier(VALID_CARDGAME_ID),"M20",LocalDate.now(),190));
       }
    @Test
    void getSetsByValidCardIdentifier_ShouldSucceed() {
        Integer expectedNumOfSets = 1;
        List<Set> found = setRepository.getSetByCardIdentifier_CardId(VALID_CARDGAME_ID);

        assertEquals(expectedNumOfSets, found.size());
    }

    @Test
    void getSetByValidSetIdentifier_ShouldSucceed() {
        Set found = setRepository.getSetBySetIdentifier_SetId(presavedSet.getSetIdentifier().getSetId());
        assertNotNull(found);
        assertThat(found, samePropertyValuesAs(presavedSet));
    }


    @Test
    void deleteSetByValidSetId_ShouldReturnNull(){

        setRepository.delete(presavedSet);

        Set found = setRepository.getSetBySetIdentifier_SetId(presavedSet.getSetIdentifier().getSetId());

        assertNull(found);
    }

    @Test
    void createSetWithValidValues_ShouldSucceed(){
        LocalDate expectedDate = LocalDate.now();
        String expectedName = "M22";
        int expectedNumOfCards = 190;

        Set newSet = setRepository.save(new Set(new CardIdentifier(VALID_CARDGAME_ID),expectedName,expectedDate,expectedNumOfCards));


        assertNotNull(newSet);

        assertEquals(expectedDate, newSet.getReleaseDate());
        assertEquals(expectedName, newSet.getName());
        assertEquals(expectedNumOfCards, newSet.getNumberOfCards());

    }

    @Test
    void updateSetWithValidValues_ShouldSucceed(){
        String expectedName = "M19";
        String initialName = presavedSet.getName();
        presavedSet.setName(expectedName);
        Set updatedSet = setRepository.save(presavedSet);


        assertEquals(updatedSet.getName(),expectedName );
        assertEquals(updatedSet.getSetIdentifier(),presavedSet.getSetIdentifier() );
        assertNotEquals(updatedSet.getName(),initialName);


    }
}