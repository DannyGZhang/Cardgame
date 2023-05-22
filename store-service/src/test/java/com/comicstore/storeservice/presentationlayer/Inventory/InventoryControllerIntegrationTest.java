package com.comicstore.storeservice.presentationlayer.Inventory;

import com.comicstore.storeservice.datalayer.Inventory.Inventory;
import com.comicstore.storeservice.datalayer.Inventory.InventoryRepository;
import com.comicstore.storeservice.datalayer.Inventory.InventoryStatus;
import com.comicstore.storeservice.datalayer.Store.Status;
import com.comicstore.storeservice.datalayer.Store.Store;
import com.comicstore.storeservice.datalayer.Store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-mysql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InventoryControllerIntegrationTest {

    private final String BASE_URI_INVENTORY = "/api/lab2/v1/inventories";

    private final String VALID_INVENTORY_ID = "d9dfcbd7-2bff-4a45-91d8-adae65c80f0c";
    private final String VALID_STORE_ID = "c293820a-d989-48ff-8410-24062a69d99e";
    private final String VALID_SECOND_STORE_ID = "1b5fb4a0-8761-47a6-bacb-ab3c99f8c480";
    private final String VALID_INVENTORY_LAST_UPDATED = "2022-03-26";
    private final String VALID_INVENTORY_TYPE = "IN_STORE";
    private final String VALID_INVENTORY_STATUS = "OPEN";


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    InventoryRepository inventoryRepository;
    @Test
    void WhenUpdateInventoryWithValidValues_thenReturnUpdatedInventory() {

        Inventory existingInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);

        String expectedStatus = "CLOSED";

        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(expectedStatus,existingInventory.getType().toString());
        webTestClient.put().uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(InventoryResponseModel.class)
                .value(val -> {
                    assertNotNull(val.getInventoryId());
                    assertEquals(val.getStoreId(),VALID_STORE_ID);
                    assertEquals(val.getStatus(),expectedStatus);
                    assertEquals(val.getType(),existingInventory.getType().toString());
                    assertNotNull(val.getLastUpdated());
                });
    }

    @Test
    void WhenUpdateInventoryWithInvalidType_throwInvalidInputException() {

        Inventory existingInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);

        String invalidType = "OUT";

        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(existingInventory.getStatus().toString(), invalidType);
        webTestClient.put().uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID )
                .jsonPath("$.message").isEqualTo("Type entered is not a valid type : " + inventoryRequestModel.getType());
    }


    @Test
    void WhenUpdateInventoryWithInvalidStatus_throwInvalidInputException() {

        Inventory existingInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);

        String invalidStatus = "OUT";

        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(invalidStatus,existingInventory.getType().toString());
        webTestClient.put().uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID )
                .jsonPath("$.message")
                .isEqualTo("Status entered is not a valid type : " + inventoryRequestModel.getStatus());
    }


    @Test
    void WhenUpdateInventoryWithInvalidInventoryId_throwNotFoundException() {

        String invalidInventoryId = "1";
        Inventory existingInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);


        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(existingInventory.getStatus().toString(),existingInventory.getType().toString());
        webTestClient.put().uri(BASE_URI_INVENTORY + "/" + invalidInventoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_INVENTORY + "/" + invalidInventoryId )
                .jsonPath("$.message")
                .isEqualTo("No Inventory with id : " + invalidInventoryId + " was found !");
    }

    @Test
    void WhenUpdateInventoryWithValidValuesAndStoreId_thenReturnUpdatedInventory() {

        Inventory existingInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);

        String expectedStatus = "CLOSED";

        InventoryRequestModel inventoryRequestModel = InventoryRequestModel.builder().storeId(VALID_SECOND_STORE_ID).type(existingInventory.getType().toString()).status(expectedStatus).build();
        webTestClient.put().uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(InventoryResponseModel.class)
                .value(val -> {
                    assertNotNull(val.getInventoryId());
                    assertNotEquals(val.getStoreId(),VALID_STORE_ID);
                    assertEquals(val.getStoreId(),VALID_SECOND_STORE_ID);
                    assertEquals(val.getStatus(),expectedStatus);
                    assertEquals(val.getType(),existingInventory.getType().toString());
                    assertNotNull(val.getLastUpdated());
                });
    }

    @Test
    void WhenInventoriesExists_thenReturnInventories() {
        Integer expectedNumInventories = 2;
        webTestClient.get()
                .uri(BASE_URI_INVENTORY)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(expectedNumInventories);
    }

    @Test
    void WhenInventoryExists_thenReturnInventory() {
        webTestClient.get()
                .uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.storeId").isEqualTo(VALID_STORE_ID)
                .jsonPath("$.lastUpdated").isEqualTo(VALID_INVENTORY_LAST_UPDATED)
                .jsonPath("$.type").isEqualTo(VALID_INVENTORY_TYPE)
                .jsonPath("$.status").isEqualTo(VALID_INVENTORY_STATUS);
    }

    @Test
    void WhenInventoryDoesNotExists_thenThrowNotFoundException() {
        String invalidInventoryId = "1";
        webTestClient.get()
                .uri(BASE_URI_INVENTORY + "/" + invalidInventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_INVENTORY + "/" + invalidInventoryId )
                .jsonPath("$.message")
                .isEqualTo("No Inventory with id : " + invalidInventoryId + " was found !");
    }

    @Test
    public void WhenDeleteExistingInventory_thenSoftDeleteInventory() {

        InventoryStatus expectedStatus = InventoryStatus.CLOSED;

        webTestClient.delete().uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();

        Inventory deletedInventory = inventoryRepository.getInventoryByInventoryIdentifier_InventoryId(VALID_INVENTORY_ID);
        assertNotNull(deletedInventory);

        assertEquals(expectedStatus, deletedInventory.getStatus());

    }


    private InventoryRequestModel createNewInventoryRequestModel(String status, String type){
        return InventoryRequestModel.builder()
                .status(status).type(type).build();
    }
}