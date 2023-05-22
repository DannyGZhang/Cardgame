package com.comicstore.storeservice.datalayer.Inventory;

import com.comicstore.storeservice.businesslayer.Inventory.InventoryService;
import com.comicstore.storeservice.datalayer.Store.Status;
import com.comicstore.storeservice.datalayer.Store.StoreIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryPersistenceTest {


    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory presavedInventory1;
    private Inventory presavedInventory2;

    @BeforeEach
    public void setup(){
        inventoryRepository.deleteAll();
        presavedInventory1 = inventoryRepository.save(
                new Inventory(new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480"),
                        LocalDate.now(),Type.IN_STORE, InventoryStatus.OPEN));
        presavedInventory2 = inventoryRepository.save(
                new Inventory(new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480"),
                        LocalDate.now(),Type.OUTSIDE_STORE, InventoryStatus.CLOSED));
    }

    @Test
    void getInventoryByValidInventoryIdentifier_InventoryId_ShouldSucceed() {
        Inventory found = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(presavedInventory1.getInventoryIdentifier().getInventoryId());
        assertThat(found, samePropertyValuesAs(presavedInventory1));
    }

    @Test
    void getInventoriesByStoreIdentifier_StoreId_ShouldSucceed() {
        Integer expectedSize = 2;
        List<Inventory> found = inventoryRepository.getInventoriesByStoreIdentifier_StoreId(presavedInventory1.getStoreIdentifier().getStoreId());
        assertEquals(expectedSize, found.size());
    }

    @Test
    void existsByInventoryIdentifier_InventoryId_ShouldSucceed() {
        Boolean found = inventoryRepository.existsByInventoryIdentifier_InventoryId(presavedInventory2.getInventoryIdentifier().getInventoryId());
        assertTrue(found);
    }

    @Test
    void existsByInventoryIdentifier_InventoryId_ShouldReturnFalse() {
        Boolean found = inventoryRepository.existsByInventoryIdentifier_InventoryId("");
        assertFalse(found);
    }


    @Test
    void deleteInventoryByValidInventoryIdentifier_ShouldReturnNull(){
        inventoryRepository.delete(presavedInventory1);

        assertNull(inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(presavedInventory1.getInventoryIdentifier().getInventoryId()));
        assertNotNull(inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(presavedInventory2.getInventoryIdentifier().getInventoryId()));
    }

    //post
    @Test
    void postNewInventoryWithValidValues_ShouldReturnNewInventory(){
        String expectedStoreId = "c293820a-d989-48ff-8410-24062a69d99e";
        LocalDate expectedDate = LocalDate.now();
        Type expectedType = Type.IN_STORE;
        InventoryStatus expectedStatus = InventoryStatus.OPEN;

        Inventory newInventory = inventoryRepository.save(new Inventory(new StoreIdentifier(expectedStoreId),expectedDate,expectedType,expectedStatus));

        assertEquals(expectedStoreId, newInventory.getStoreIdentifier().getStoreId());
        assertEquals(expectedDate, newInventory.getLastUpdated());
        assertEquals(expectedType, newInventory.getType());
        assertEquals(expectedStatus,newInventory.getStatus());
    }

    //put

    @Test
    void updateInventoryWithValidParam_ShouldSucceed(){
        Type initialType = presavedInventory1.getType();
        Type expectedType = Type.OUTSIDE_STORE;
        presavedInventory1.setType(expectedType);
        Inventory savedInventory = inventoryRepository.save(presavedInventory1);
        assertEquals(expectedType,savedInventory.getType());
        assertNotEquals(initialType, savedInventory.getType());
    }
}