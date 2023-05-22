package com.comicstore.apigateway.presentationlayer.Store.Store;

import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryRequestModel;
import com.comicstore.apigateway.presentationlayer.Store.Inventory.InventoryResponseModel;
import com.comicstore.apigateway.utils.HttpErrorInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
class StoreControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    private InventoryResponseModel inventoryResponseModel1;
    private InventoryResponseModel inventoryResponseModel2;

    private ClientResponseModel clientResponseModel;

    private StoreResponseModel storeResponseModel1;
    private StoreResponseModel storeResponseModel2;
    @BeforeEach
    public void setUp() {
        clientResponseModel = new ClientResponseModel("6428645f-d56d-4ee5-841e-f7cee01e4cc3",
                "c80690a8-746d-4d23-817c-235579e516b3",
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");
        storeResponseModel1 = new StoreResponseModel("6428645f-d56d-4ee5-841e-f7cee01e4cc3",
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");
        storeResponseModel2 =  new StoreResponseModel("8d80c869-f45e-41fa-b592-35f863c8005a",
                LocalDate.parse("2015-02-03"), "45 Flowers Terrace", "Montreal", "Quebec", "J5X 5J4", "email2@email4.com", "OPEN", "450-777-8889");

        clientResponseModel = new ClientResponseModel();
        inventoryResponseModel1 = new InventoryResponseModel("2be6ab80-6693-404d-beed-66850aa1e7cf",storeResponseModel1.getStoreId(), LocalDate.now(),"IN_STORE","OPEN");
        inventoryResponseModel2 = new InventoryResponseModel("c1e32b5b-30ff-4802-82c5-4d4460148284",storeResponseModel1.getStoreId(),LocalDate.parse("2000-09-09"),"IN_STORE","OPEN");
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mapper.registerModule(new JavaTimeModule());
    }
    @Test
    void getClientAggregatesFromStoreId_ShouldReturnAllClientsWithStoreId() throws URISyntaxException, JsonProcessingException {
        ClientResponseModel[] clientResponseModels = {clientResponseModel};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/" + storeResponseModel1.getStoreId()+"/clients")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(clientResponseModels))                );

        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/clients";
        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(ClientResponseModel[].class)
                .value((dto) -> {
                    assertEquals(1,dto.length);
                });


    }

    @Test
    void createInventory_ShouldReturnCreatedInventory() throws URISyntaxException, JsonProcessingException {
        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel1.getStoreId())
                .status(inventoryResponseModel1.getStatus())
                .type(inventoryResponseModel1.getType())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inventoryResponseModel1))
                );


        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InventoryResponseModel.class)
                .value((dto)->{
                    MatcherAssert.assertThat(inventoryResponseModel1, samePropertyValuesAs(dto));
                });
    }
    @Test
    void createInventoryWithInvalidType_ShouldThrowInvalidInputException() throws URISyntaxException, JsonProcessingException {
        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel1.getStoreId())
                .status("ONLINE")
                .type(inventoryResponseModel1.getType())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories","Invalid type")))
                );


        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories","Invalid type"), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void getStores_ShouldReturnAllStores() throws JsonProcessingException, URISyntaxException {
        StoreResponseModel[] storeResponseModels = {storeResponseModel1,storeResponseModel2};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(storeResponseModels)
                        ));
        String url = "api/lab2/v1/stores";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(StoreResponseModel[].class)
                .value((dto) -> {
                    MatcherAssert.assertThat(storeResponseModels, samePropertyValuesAs(dto));
                });
    }

    @Test
    void getStore_Should_ReturnStoreWithId() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeResponseModel1.getStoreId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(storeResponseModel1)
                        ));
        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(StoreResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(storeResponseModel1, samePropertyValuesAs(dto));
                });
    }

    @Test
    void createStore_ShouldReturnCreatedStore() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status(storeResponseModel1.getStatus())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(storeResponseModel1)
                ));


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(StoreResponseModel.class)
                .value((dto)->{
                    MatcherAssert.assertThat(storeResponseModel1, samePropertyValuesAs(dto));
                    assertNotNull(dto.getLinks());
                });
    }


    @Test
    void createStoreWithInvalidStatus_ShouldReturnCreatedStore() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status("PENDING")
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores","Invalid status"))
                        ));


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores","Invalid status"), samePropertyValuesAs(dto, "timestamp"));
                });
    }

    @Test
    void createStoreWithInvalidDateFormat_ShouldThrowInvalidException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened("202002")
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status("PENDING")
                .build();


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertEquals("Date opened entered is not in format YYYY-MM-DD : " + storeRequestModel.getDateOpened(),dto.getMessage());
                });
    }

    @Test
    void createStoreWithInvalidPostalCode_ShouldThrowInvalidException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode("JJJJ")
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status("PENDING")
                .build();


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertEquals(    "The postal code is not in the proper format : " + storeRequestModel.getPostalCode()
                            ,dto.getMessage());
                });
    }

    @Test
    void createStoreWithInvalidEmailFormat_ShouldThrowInvalidException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email("gkjlaegbkj")
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status("OPEN")
                .build();


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertEquals("Email entered is not valid : " + storeRequestModel.getEmail()
                            ,dto.getMessage());
                });
    }

    @Test
    void createStoreWithInvalidPhoneFormat_ShouldThrowInvalidException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber("iugiyik")
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status("OPEN")
                .build();


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    assertEquals("Phone entered is not valid : " + storeRequestModel.getPhoneNumber()
                            ,dto.getMessage());
                });
    }

    @Test
    void createStoreWithDuplicateLocation_ShouldThrowDuplicateStoreLocationException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status(storeResponseModel1.getStatus())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores","Street address is already occupied by another store : " + storeRequestModel.getStreetAddress()))
                        ));


        String url = "api/lab2/v1/stores";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores","Street address is already occupied by another store : " + storeRequestModel.getStreetAddress()), samePropertyValuesAs(dto,"timestamp"));
                });
    }
    @Test
    void updateStore_ShouldReturnOK() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status(storeResponseModel1.getStatus())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeResponseModel1.getStoreId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(storeResponseModel1)
                        ));


        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void updateStoreWithInvalidId_ShouldThrowNotFoundException() throws JsonProcessingException, URISyntaxException {
        StoreRequestModel storeRequestModel = StoreRequestModel.builder()
                .city(storeResponseModel1.getCity())
                .dateOpened(storeResponseModel1.getDateOpened().toString())
                .email(storeResponseModel1.getEmail())
                .phoneNumber(storeResponseModel1.getPhoneNumber())
                .postalCode(storeResponseModel1.getPostalCode())
                .province(storeResponseModel1.getProvince())
                .streetAddress(storeResponseModel1.getStreetAddress())
                .status(storeResponseModel1.getStatus())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/invalid")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/stores/invalid","Not found"))
                        ));


        String url = "api/lab2/v1/stores/invalid";
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(storeRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto)->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/stores/invalid","Not found"), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void getInventoriesByStoreId() throws JsonProcessingException, URISyntaxException {
        InventoryResponseModel[] inventoryResponseModels = {inventoryResponseModel1,inventoryResponseModel2};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/" + storeResponseModel1.getStoreId()+"/inventories")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inventoryResponseModels))                );

        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId()+"/inventories";
        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(ClientResponseModel[].class)
                .value((dto) -> {
                    assertEquals(2,dto.length);
                });

    }

    @Test
    void deleteStore_ShouldReturnNoContent() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeResponseModel1.getStoreId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        String url = "api/lab2/v1/stores/"+storeResponseModel1.getStoreId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }
}