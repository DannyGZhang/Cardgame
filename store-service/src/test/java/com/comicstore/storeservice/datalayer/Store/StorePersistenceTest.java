package com.comicstore.storeservice.datalayer.Store;

import com.comicstore.storeservice.datalayer.Inventory.Inventory;
import com.comicstore.storeservice.datalayer.Inventory.InventoryRepository;
import com.comicstore.storeservice.datalayer.Inventory.InventoryStatus;
import com.comicstore.storeservice.datalayer.Inventory.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StorePersistenceTest {
    @Autowired
    private StoreRepository storeRepository;

    private Store presavedStore1;
    @BeforeEach
    public void setup(){
        storeRepository.deleteAll();
        presavedStore1 = storeRepository.save(new Store(LocalDate.now(),new Address("45 rue des Pensees","City of Quebec","Quebec","J5R-5J4"), Status.OPEN));
    }
    @Test
    void findByStoreIdentifier_ShouldSucceed() {
        Store found = storeRepository.findByStoreIdentifier_StoreId(presavedStore1.getStoreIdentifier().getStoreId());
        assertNotNull(found);
    }
    @Test
    void findByStoreIdentifier_ShouldReturnNull() {
        Store found = storeRepository.findByStoreIdentifier_StoreId("");
        assertNull(found);
    }

    @Test
    void existsByStoreIdentifier_ShouldReturnTrue() {
        Boolean found = storeRepository.existsByStoreIdentifier_StoreId(presavedStore1.getStoreIdentifier().getStoreId());
        assertTrue(found);
    }
    @Test
    void existsByStoreIdentifier_ShouldReturnFalse() {
        Boolean found = storeRepository.existsByStoreIdentifier_StoreId("");
        assertFalse(found);
    }

    @Test
    void deleteStoreByInventoryIdentifier_ShouldReturnFalse(){
        storeRepository.delete(presavedStore1);

        Boolean found = storeRepository.existsByStoreIdentifier_StoreId(presavedStore1.getStoreIdentifier().getStoreId());

        assertFalse(found);
    }

    //post
    @Test
    void createInventoryWithValidValues_ShouldSucceed(){
        LocalDate expectedDate = LocalDate.now();
        String expectedStreetAddress = "StreetAddress";
        String expectedCity= "City";
        String expectedProvince= "Province";
        String expectedPostalCode= "Postal";
        Status expectedStatus= Status.OPEN;

        Store newStore = storeRepository.save(new Store(expectedDate,new Address(expectedStreetAddress,expectedCity,expectedProvince,expectedPostalCode),expectedStatus));


        assertNotNull(newStore);

        assertEquals(expectedDate, newStore.getDateOpened());
        assertEquals(expectedStreetAddress, newStore.getAddress().getStreetAddress());
        assertEquals(expectedCity, newStore.getAddress().getCity());
        assertEquals(expectedProvince, newStore.getAddress().getProvince());
        assertEquals(expectedPostalCode, newStore.getAddress().getPostalCode());
        assertEquals(expectedStatus, newStore.getStatus());
    }


    //put
    @Test
    void updateInventoryWithValidValues_ShouldSucceed(){
        Status initialStatus = presavedStore1.getStatus();
        Status expectedStatus = Status.CLOSED;
        presavedStore1.setStatus(expectedStatus);
        Store updatedStore = storeRepository.save(presavedStore1);

        assertEquals(updatedStore.getStatus(),expectedStatus );
        assertNotEquals(updatedStore.getStatus(),initialStatus);


    }
}