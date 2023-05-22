package com.comicstore.storeservice.presentationlayer.Store;

import com.comicstore.storeservice.datalayer.Inventory.InventoryRepository;
import com.comicstore.storeservice.datalayer.Store.Status;
import com.comicstore.storeservice.datalayer.Store.Store;
import com.comicstore.storeservice.datalayer.Store.StoreRepository;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryRequestModel;
import com.comicstore.storeservice.presentationlayer.Inventory.InventoryResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-mysql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StoreControllerIntegrationTest {

    private final String BASE_URI_STORE = "/api/lab2/v1/stores";

    private final String VALID_STORE_DATE_OPENED = "2016-05-12";
    private final String VALID_STORE_PROVINCE = "Quebec";
    private final String VALID_STORE_CITY = "Windsor";
    private final String VALID_STORE_POSTAL_CODE = "J1S 5J4";
    private final String VALID_STORE_EMAIL = "email@email1.com";
    private final String VALID_STORE_PHONE = "222-666-7777";
    private final Status VALID_STORE_STATUS = Status.OPEN;
    private final String VALID_STORE_STREET_ADDRESS = "971 Oneill Trail";

    private final String VALID_STORE_ID = "c293820a-d989-48ff-8410-24062a69d99e";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Test
    void WhenCreateStoreWithValidValues_thenReturnNewStore() {
        String date = "2023-04-05";
        String expectedDate = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.post().uri(BASE_URI_STORE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .jsonPath("$.dateOpened").isEqualTo(expectedDate)
                .jsonPath("$.streetAddress").isEqualTo(expectedStreetAddress)
                .jsonPath("$.city").isEqualTo(expectedCity)
                .jsonPath("$.province").isEqualTo(expectedProvince)
                .jsonPath("$.postalCode").isEqualTo(expectedPostalCode)
                .jsonPath("$.email").isEqualTo(expectedEmail)
                .jsonPath("$.status").isEqualTo(expectedStatus)
                .jsonPath("$.phoneNumber").isEqualTo(expectedPhone)
                .jsonPath("$.storeId").isNotEmpty();

    }

    @Test
    void WhenCreateStoreWithInvalidStatus_thenThrowInvalidInputException() {
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OUT";

        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.post().uri(BASE_URI_STORE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE)
                .jsonPath("$.message").isEqualTo("Status entered is not a valid type : " + storeRequestModel.getStatus());


    }

    @Test
    void WhenStoresExists_thenReturnAllStores() {
        Integer expectedNumStores = 2;

        webTestClient.get()
                .uri(BASE_URI_STORE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumStores);

    }

    @Test
    void WhenStoreExist_thenReturnStore() {

        webTestClient.get()
                .uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.storeId").isEqualTo(VALID_STORE_ID)
                .jsonPath("$.dateOpened").isEqualTo(VALID_STORE_DATE_OPENED)
                .jsonPath("$.streetAddress").isEqualTo(VALID_STORE_STREET_ADDRESS)
                .jsonPath("$.city").isEqualTo(VALID_STORE_CITY)
                .jsonPath("$.province").isEqualTo(VALID_STORE_PROVINCE)
                .jsonPath("$.postalCode").isEqualTo(VALID_STORE_POSTAL_CODE)
                .jsonPath("$.email").isEqualTo(VALID_STORE_EMAIL)
                .jsonPath("$.status").isEqualTo(VALID_STORE_STATUS.toString())
                .jsonPath("$.phoneNumber").isEqualTo(VALID_STORE_PHONE);
    }

    @Test
    void WhenCreateInventoryWithValidValues_thenReturnNewInventory() {
        String expectedType = "IN_STORE";
        String expectedStatus = "OPEN";
        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(VALID_STORE_ID, expectedStatus, expectedType);

        //act and assert
        webTestClient.post().uri(BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(InventoryResponseModel.class)
                .value(val -> {
                    assertNotNull(val.getInventoryId());
                    assertEquals(val.getStoreId(), VALID_STORE_ID);
                    assertEquals(val.getStatus(), expectedStatus);
                    assertEquals(val.getType(), expectedType);
                    assertNotNull(val.getLastUpdated());
                });
    }


    @Test
    void WhenCreateInventoryWithInvalidStatus_throwInvalidInputException() {
        String invalidType = "OUT";
        String expectedStatus = "OPEN";
        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(VALID_STORE_ID, expectedStatus, invalidType);


        webTestClient.post().uri(BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .jsonPath("$.message").isEqualTo("Type entered is not a valid type : " + inventoryRequestModel.getType());

    }

    @Test
    void WhenCreateInventoryWithInvalidType_throwInvalidInputException() {
        String expectedType = "IN_STORE";
        String invalidStatus = "OUT";
        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(VALID_STORE_ID, invalidStatus, expectedType);


        webTestClient.post().uri(BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .jsonPath("$.message").isEqualTo("Status entered is not a valid type : " + inventoryRequestModel.getStatus());

    }

    @Test
    void WhenCreateInventoryWithInvalidStoreId_throwNotFoundException() {
        String expectedType = "IN_STORE";
        String invalidStatus = "OPEN";
        String invalidStoreId = "1";
        InventoryRequestModel inventoryRequestModel = createNewInventoryRequestModel(invalidStoreId, invalidStatus, expectedType);


        webTestClient.post().uri(BASE_URI_STORE + "/" + invalidStoreId + "/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + invalidStoreId + "/inventories")
                .jsonPath("$.message").isEqualTo("No Store with id : " + inventoryRequestModel.getStoreId() + " was found !");

    }

    @Test
    void WhenStoreDoesNotExists_thenThrowNotFoundException() {
        String invalidStoreId = "1";
        webTestClient.get()
                .uri(BASE_URI_STORE + "/" + invalidStoreId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + invalidStoreId)
                .jsonPath("$.message")
                .isEqualTo("Store with id : " + invalidStoreId + " was not found !");
    }


    @Test
    public void WhenUpdateStoreWithValidValues_thenReturnUpdatedStore() {
        //arrange
        String date = "2023-04-05";
        String expectedDate = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .jsonPath("$.storeId").isEqualTo(VALID_STORE_ID)
                .jsonPath("$.dateOpened").isEqualTo(expectedDate)
                .jsonPath("$.streetAddress").isEqualTo(expectedStreetAddress)
                .jsonPath("$.city").isEqualTo(expectedCity)
                .jsonPath("$.province").isEqualTo(expectedProvince)
                .jsonPath("$.postalCode").isEqualTo(expectedPostalCode)
                .jsonPath("$.email").isEqualTo(expectedEmail)
                .jsonPath("$.status").isEqualTo(expectedStatus)
                .jsonPath("$.phoneNumber").isEqualTo(expectedPhone);

    }

    @Test
    public void WhenUpdateStoreWithInvalidStoreId_thenThrowNotFoundException() {
        //arrange
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        String invalidStoreId = "1";
        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + invalidStoreId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + invalidStoreId)
                .jsonPath("$.message")
                .isEqualTo("Store with id : " + invalidStoreId + " was not found !");
    }

    @Test
    public void WhenUpdateStoreWithInvalidStatus_thenThrowInvalidInputExceptionn() {
        //arrange
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OUT";

        String invalidStoreId = "1";
        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + invalidStoreId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + invalidStoreId)
                .jsonPath("$.message")
                .isEqualTo("Status entered is not a valid type : " + storeRequestModel.getStatus());
    }

    @Test
    public void WhenUpdateStoreWithInvalidPostalCode_thenThrowInvalidInputException() {
        //arrange
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "-5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID)
                .jsonPath("$.message")
                .isEqualTo("The postal code is not in the proper format : " + storeRequestModel.getPostalCode());
    }

    @Test
    public void WhenUpdateStoreWithInvalidEmail_thenThrowInvalidInputException() {
        //arrange
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID)
                .jsonPath("$.message")
                .isEqualTo("Email entered is not valid : " + storeRequestModel.getEmail());
    }
    @Test
    public void WhenUpdateStoreWithInvalidDateFormat_thenThrowInvalidInputException() {
        //arrange
        String date = "2023/04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .dateOpened(date).streetAddress(expectedStreetAddress)
                .city(expectedCity).province(expectedProvince)
                .postalCode(expectedPostalCode)
                .email(expectedEmail).status(expectedStatus).phoneNumber(expectedPhone).build();

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID)
                .jsonPath("$.message")
                .isEqualTo("Date opened entered is not in format YYYY-MM-DD : " + storeRequestModel.getDateOpened());
    }

    @Test
    public void WhenUpdateStoreWithInvalidPhone_thenThrowInvalidInputException() {
        //arrange
        String date = "2023-04-05";
        String expectedStreetAddress = "StreetAddress";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-2-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";

        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .dateOpened(date).streetAddress(expectedStreetAddress)
                .city(expectedCity).province(expectedProvince)
                .postalCode(expectedPostalCode)
                .email(expectedEmail).status(expectedStatus).phoneNumber(expectedPhone).build();

        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID)
                .jsonPath("$.message")
                .isEqualTo("Phone entered is not valid : " + storeRequestModel.getPhoneNumber());
    }

    @Test
    void WhenStoresExists_thenReturnAllInventoriesOfStore() {
        Integer expectedNumInventories = 1;
        webTestClient.get()
                .uri(BASE_URI_STORE + "/" + VALID_STORE_ID + "/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumInventories);
    }

    @Test
    void WhenStoresIdIsInInvalid_thenThrowNotFoundException() {
        String invalidStoreId = "1";
        webTestClient.get()
                .uri(BASE_URI_STORE + "/" + invalidStoreId + "/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + invalidStoreId + "/inventories")
                .jsonPath("$.message")
                .isEqualTo("Store with id : " + invalidStoreId + " was not found !");
    }

    @Test
    public void WhenDeleteExistingStore_thenSoftDeleteStore() {

        Status expectedStatus = Status.CLOSED;

        webTestClient.delete().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();

        Store deletedStore = storeRepository.findByStoreIdentifier_StoreId(VALID_STORE_ID);
        assertNotNull(deletedStore);

        assertEquals(expectedStatus, deletedStore.getStatus());

    }

    @Test
    void WhenTwoStoresWithSameLocation_thenThrowDuplicateStoreLocationException() {
        String date = "2023-04-05";
        String expectedStreetAddress = "971 Oneill Trail";
        String expectedCity = "City";
        String expectedProvince = "Province";
        String expectedPostalCode = "J5R 5J4";
        String expectedPhone = "111-111-1111";
        String expectedEmail = "d@gmail.com";
        String expectedStatus = "OPEN";


        StoreRequestModel storeRequestModel = createNewStoreRequestModel(date, expectedStreetAddress, expectedCity, expectedProvince, expectedPostalCode, expectedEmail, expectedStatus, expectedPhone);


        //act and assert
        webTestClient.post().uri(BASE_URI_STORE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE)
                .jsonPath("$.message").isEmpty();
    }


    @Test
    void WhenTwoStoresWithSameLocationUpdate_thenThrowDuplicateStoreLocationException() {
        String duplicateAddress = "73046 Clarendon Terrace";


        Store existingStore = storeRepository.findByStoreIdentifier_StoreId(VALID_STORE_ID);


        StoreRequestModel storeRequestModel = createNewStoreRequestModel(existingStore.getDateOpened().toString(), duplicateAddress, existingStore.getAddress().getCity(), existingStore.getAddress().getProvince(), existingStore.getAddress().getPostalCode(), existingStore.getContact().getEmail(), existingStore.getStatus().toString(), existingStore.getContact().getPhoneNumber());


        //act and assert
        webTestClient.put().uri(BASE_URI_STORE + "/" + VALID_STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("uri=" + BASE_URI_STORE + "/" + VALID_STORE_ID)
                .jsonPath("$.message").isEmpty();
    }

    private StoreRequestModel createNewStoreRequestModel(String date, String s_Add, String city, String province, String postalCode, String email, String status, String phoneNumber) {


        return StoreRequestModel.builder()
                .dateOpened(LocalDate.parse(date).toString()).streetAddress(s_Add)
                .city(city).province(province)
                .postalCode(postalCode)
                .email(email).status(status).phoneNumber(phoneNumber).build();
    }


    private InventoryRequestModel createNewInventoryRequestModel(String storeId, String status, String type) {
        return InventoryRequestModel.builder()
                .storeId(storeId).status(status).type(type).build();
    }
}