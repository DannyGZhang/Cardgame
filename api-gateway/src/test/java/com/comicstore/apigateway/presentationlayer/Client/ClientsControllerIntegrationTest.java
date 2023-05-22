package com.comicstore.apigateway.presentationlayer.Client;

import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.ClientIdentifier;
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
import java.util.Arrays;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ClientsControllerIntegrationTest {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    public void setUp() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createNewClient_ShouldReturnCreatedClient() throws JsonProcessingException, URISyntaxException {
        ClientIdentifier client = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";
        ClientResponseModel clientReponse = new ClientResponseModel(storeId,
                client.getClientId(),
                firstName,
                lastName, 20,
                total, email,
                phone);

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();

        StoreResponseModel locationResponse = new StoreResponseModel(storeId,
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(clientReponse))
                );



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ClientResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(clientReponse, samePropertyValuesAs(dto));
                    assertEquals(clientRequestModel.getFirstName(),dto.getFirstName());
                    assertEquals(clientRequestModel.getLastName(),dto.getLastName());
                    assertEquals(20,dto.getRebate());
                });
    }



    @Test
    void createNewClientWithInvalidFullName_ShouldReturnDuplicateFullNameException() throws JsonProcessingException, URISyntaxException {
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";


        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();

        StoreResponseModel locationResponse = new StoreResponseModel(storeId,
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/stores/clients","A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !")))
                );



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.CONFLICT,"uri=/api/lab2/v1/stores/clients","A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !"), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void createNewClientWithInvalidBody_ShouldThrowNoEmailAndPhoneException() throws JsonProcessingException, URISyntaxException {
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;



        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .totalBought(total)
                .build();

        StoreResponseModel locationResponse = new StoreResponseModel(storeId,
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );



        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.BAD_REQUEST,"uri=/api/lab2/v1/stores/clients","You must enter an email or a phone number")))
                );



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.BAD_REQUEST,"uri=/api/lab2/v1/stores/clients","You must enter an email or a phone number"), samePropertyValuesAs(dto,"timestamp"));

                });
    }

    @Test
    void createNewClientWithNegativeTotal_ShouldThrowInvalidInputException() throws JsonProcessingException, URISyntaxException {
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = -2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertEquals("A client can't have a negative total bought : " + clientRequestModel.getTotalBought(),dto.getMessage());
                    assertEquals("uri=/api/lab2/v1/stores/clients",dto.getPath());
                });
    }


    @Test
    void createNewClientWithInvalidEmailFormat_ShouldThrowInvalidInputException() throws JsonProcessingException, URISyntaxException {
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "netlog.com";
        String phone = "578-119-7669";

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertEquals("Email is in an invalid format ! : " + clientRequestModel.getEmail(),dto.getMessage());
                    assertEquals("uri=/api/lab2/v1/stores/clients",dto.getPath());
                });
    }


    @Test
    void createNewClientWithInvalidPhoneFormat_ShouldThrowInvalidInputException() throws JsonProcessingException, URISyntaxException {
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "bgilnl@netlog.com";
        String phone = "5789999119-7669";

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstName)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();



        String url = "api/lab2/v1/stores/clients";
        webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    assertEquals("Phone number is in an invalid format ! : " + clientRequestModel.getPhoneNumber(),dto.getMessage());
                    assertEquals("uri=/api/lab2/v1/stores/clients",dto.getPath());
                });
    }
    @Test
    void getClientAggregateById_ShouldReturnSpecificClient() throws JsonProcessingException, URISyntaxException {

        ClientIdentifier client = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");

        ClientResponseModel clientReponse = new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                client.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients/" + client.getClientId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(clientReponse))
                );

        String url = "api/lab2/v1/stores/clients/" + client.getClientId();

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(ClientResponseModel.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(clientReponse, samePropertyValuesAs(dto));
                    assertNotNull(dto.getLinks());
                });
    }



    @Test
    void updateClient_ShouldReturnNullAndUpdate() throws JsonProcessingException, URISyntaxException {

        ClientIdentifier client = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String firstNameUpdate = "Dylan";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";
        ClientResponseModel clientReponse = new ClientResponseModel(storeId,
                client.getClientId(),
                firstName,
                lastName, 20,
                total, email,
                phone);

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstNameUpdate)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();


        StoreResponseModel locationResponse = new StoreResponseModel(storeId,
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients/" + client.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                );

        String url = "api/lab2/v1/stores/clients/" + client.getClientId();

        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateClientWithUnknownError_ShouldThrowInvalidInputException() throws JsonProcessingException, URISyntaxException {

        ClientIdentifier client = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        String storeId = "c293820a-d989-48ff-8410-24062a69d99e";
        String firstName = "Stanleigh";
        String firstNameUpdate = "Dylan";
        String lastName = "Roller";
        double total = 2238.19;
        String email = "sroller1l@netlog.com";
        String phone = "578-119-7669";
        ClientResponseModel clientReponse = new ClientResponseModel(storeId,
                client.getClientId(),
                firstName,
                lastName, 20,
                total, email,
                phone);

        ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                .firstName(firstNameUpdate)
                .lastName(lastName)
                .storeId(storeId)
                .email(email)
                .totalBought(total)
                .phoneNumber(phone)
                .build();


        StoreResponseModel locationResponse = new StoreResponseModel(storeId,
                LocalDate.parse("2012-06-08"), "73046 Clarendon Terrace", "Magog", "Quebec", "J1X 6J9", "email1@email2.com", "OPEN", "255-777-8889");

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7003/api/lab2/v1/stores/"+storeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(locationResponse))
                );

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients/" + client.getClientId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores/clients/"+client.getClientId(),"Unknown Error")))

                );

        String url = "api/lab2/v1/stores/clients/" + client.getClientId();

        webTestClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto->{
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY,"uri=/api/lab2/v1/stores/clients/"+client.getClientId(),"Unknown Error"), samePropertyValuesAs(dto,"timestamp"));

                });
    }

    @Test
    void deleteClient_ShouldReturnNoContent() throws URISyntaxException {
        ClientIdentifier client = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");

        String url = "api/lab2/v1/stores/clients/" + client.getClientId();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients/" + client.getClientId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT)
                );
        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();
    }


    @Test
    void deleteClientWithInvalidId_ShouldThrowNotFoundException() throws URISyntaxException, JsonProcessingException {
        ClientIdentifier client = new ClientIdentifier("Invalid");

        String url = "api/lab2/v1/stores/clients/" + client.getClientId();
        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients/" + client.getClientId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/stores/clients/"+client.getClientId(),"Not found")))
                );
        webTestClient.delete().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value((dto) -> {
                    MatcherAssert.assertThat(new HttpErrorInfo(HttpStatus.NOT_FOUND,"uri=/api/lab2/v1/stores/clients/"+client.getClientId(),"Not found"), samePropertyValuesAs(dto,"timestamp"));
                });
    }

    @Test
    void getClients_ShouldReturnAllClients() throws URISyntaxException, JsonProcessingException {
        Integer expectedNum = 3;



        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("0e3a0308-ca93-472a-bcdc-c95175353a8d");

        ClientResponseModel clientReponse1 = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier1.getClientId(),
                "Pauline",
                "Grishmanov",
                20,
                3416.77, "pgrishmanov1r@tinyurl.com",
                "979-767-5807");


        ClientResponseModel clientReponse2 =  new ClientResponseModel("c293820a-d989-48ff-8410-24062a69d99e",
                clientIdentifier2.getClientId(),
                "Stanleigh",
                "Roller", 20,
                2238.19, "sroller1l@netlog.com",
                "578-119-7669");


        ClientResponseModel clientReponse3 = new ClientResponseModel("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480",
                clientIdentifier3.getClientId(),
                "Leodora",
                "Quodling", 20,
                2543.3, "lquodling4t@latimes.com",
                "229-316-8771");

        ClientResponseModel[] clientResponseModels = {clientReponse1,clientReponse2,clientReponse3};

        mockRestServiceServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:7001/api/lab2/v1/stores/clients")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(clientResponseModels)));

        String url = "api/lab2/v1/stores/clients";

        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(ClientResponseModel[].class)
                .value((dto) -> {
                    assertEquals(expectedNum, dto.length);
                    Arrays.stream(dto).toList().forEach(clientResponseModel -> assertNotNull(clientResponseModel.getLinks()));
                });
    }


}