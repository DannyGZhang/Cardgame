package com.comicstore.apigateway.presentationlayer.Store.Inventory;

import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetResponseModel;
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
class InventoryControllerIntegrationTests {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    private InventoryResponseModel inventoryResponseModel1;

    @BeforeEach
    public void setUp() {
        inventoryResponseModel1 = new InventoryResponseModel("2be6ab80-6693-404d-beed-66850aa1e7cf","1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",LocalDate.now(),"IN_STORE","OPEN");
      mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void updateInventory_ShouldReturnIsOK() throws URISyntaxException {
        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel1.getStoreId())
                .status(inventoryResponseModel1.getStatus())
                .type(inventoryResponseModel1.getType())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                );


        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateInventoryWithInvalidStatus_ShouldReturnIsOK() throws URISyntaxException, JsonProcessingException {
        InventoryRequestModel inventoryRequestModel =  InventoryRequestModel.builder()
                .storeId(inventoryResponseModel1.getStoreId())
                .status("PENDING")
                .type(inventoryResponseModel1.getType())
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Invalid Status")))

                );


        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();
        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Invalid Status")
                            , samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void getInventoryById_ShouldReturnInventory() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inventoryResponseModel1))
                );
        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(InventoryResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(inventoryResponseModel1, samePropertyValuesAs(dto));
                });

    }

    @Test
    void getInventoryByInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Not found")
                        ))
                );
        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Not found")
                            , samePropertyValuesAs(dto,"timestamp"));
                });

    }

    @Test
    void deleteInventory_ShouldReturnNoContent() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();

        webTestClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    void deleteInventoryWithInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Not found"))));
        String url = "api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId();

        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/inventories/"+inventoryResponseModel1.getInventoryId(),"Not found"), samePropertyValuesAs(dto,"timestamp"));
                });
    }
}