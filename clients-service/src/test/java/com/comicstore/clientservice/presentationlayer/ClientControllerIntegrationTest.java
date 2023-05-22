package com.comicstore.clientservice.presentationlayer;

import com.comicstore.clientservice.Utils.HttpErrorInfo;
import com.comicstore.clientservice.datalayer.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-mysql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientControllerIntegrationTest {
    private final String BASE_URI_CLIENTS = "/api/lab2/v1/stores";
    private final String VALID_CLIENT_ID = "2df29d30-4dd9-49a6-ad0e-c5810677f522";
    private final String VALID_CLIENT_FIRSTNAME = "Donovan";
    private final String VALID_CLIENT_LASTNAME = "Danbi";
    private final String VALID_CLIENT_EMAIL = "ddanbi0@businesswire.com";
    private final String VALID_CLIENT_PHONE = "674-869-0540";
    private final Double VALID_CLIENT_TOTAL = 995.88;
    private final Double VALID_CLIENT_REBATE = 0.0;
    private final String VALID_STORE_ID = "c293820a-d989-48ff-8410-24062a69d99e";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ClientRepository clientRepository;


    @Test
    void ValidStoreGetClients_ThenReturnAllClientsFromStore() {
        Integer expectedNumOfClients = 92;

        webTestClient.get()
                .uri(BASE_URI_CLIENTS +"/" + VALID_STORE_ID + "/clients")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumOfClients);
    }

    @Test
    void getClients_ShouldReturnAllClients() {
        Integer expectedNumOfClients = 200;

        webTestClient.get()
                .uri(BASE_URI_CLIENTS +"/clients")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.length()").isEqualTo(expectedNumOfClients);
    }

    @Test
    void getClientByValidId_ShouldReturnClient() {
        webTestClient.get()
                .uri(BASE_URI_CLIENTS +"/clients/" + VALID_CLIENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ClientResponseModel.class)
                .value(dto ->{
                    assertEquals(dto.getClientId(),VALID_CLIENT_ID);
                    assertEquals(dto.getFirstName(),VALID_CLIENT_FIRSTNAME);
                    assertEquals(dto.getLastName(),VALID_CLIENT_LASTNAME);
                    assertEquals(dto.getEmail(),VALID_CLIENT_EMAIL);
                    assertEquals(dto.getPhoneNumber(),VALID_CLIENT_PHONE);
                    assertEquals(dto.getTotalBought(),VALID_CLIENT_TOTAL);
                    assertEquals(dto.getRebate(),VALID_CLIENT_REBATE);
                });

    }
    @Test
    void getClientByInvalidId_ShouldThrowNotFoundException() {
        webTestClient.get()
                .uri(BASE_URI_CLIENTS +"/clients/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getPath(),"uri="+BASE_URI_CLIENTS +"/clients/" + 1);
                    assertEquals(dto.getMessage(),"No client found with id : " + 1);
                });

    }

    @Test
    void updateClientByValidId_ShouldReturnUpdatedClient() {
        Double newTotal = 1300.90;
        Double expectedRebate = 10.0;
        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,VALID_CLIENT_FIRSTNAME,VALID_CLIENT_LASTNAME,VALID_CLIENT_EMAIL,VALID_CLIENT_PHONE,newTotal);

        webTestClient.put().uri(BASE_URI_CLIENTS + "/clients/" + VALID_CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(ClientResponseModel.class)
                .value(dto ->{
                    assertEquals(dto.getClientId(),VALID_CLIENT_ID);
                    assertEquals(dto.getFirstName(),VALID_CLIENT_FIRSTNAME);
                    assertEquals(dto.getLastName(),VALID_CLIENT_LASTNAME);
                    assertEquals(dto.getEmail(),VALID_CLIENT_EMAIL);
                    assertEquals(dto.getPhoneNumber(),VALID_CLIENT_PHONE);
                    assertEquals(dto.getTotalBought(),newTotal);
                    assertEquals(dto.getRebate(),expectedRebate);
                });



    }

    @Test
    void updateClientByDuplicateName_ShouldThrowDuplicateClientInfoException() {
        String duplicateFirstName = "Demetra";
        String duplicateLastName = "Toxell";
        Double newTotal = 1300.90;
        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,duplicateFirstName,duplicateLastName,VALID_CLIENT_EMAIL,VALID_CLIENT_PHONE,newTotal);

        webTestClient.put().uri(BASE_URI_CLIENTS + "/clients/" + VALID_CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients/" + VALID_CLIENT_ID);
                    assertEquals(dto.getMessage(),"A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !");

                });



    }
    @Test
    void updateClientWithInvalidId_ShouldThrowNotFoundException() {
        String duplicateFirstName = "Dylan";
        String duplicateLastName = "Brassard";
        Double newTotal = 1300.90;
        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,duplicateFirstName,duplicateLastName,VALID_CLIENT_EMAIL,VALID_CLIENT_PHONE,newTotal);

        webTestClient.put().uri(BASE_URI_CLIENTS + "/clients/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients/" + 1);
                    assertEquals(dto.getMessage(),"No client found with id : " + 1);
                });
    }

    @Test
    void createClient_ShouldReturnNewClient() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        String newEmail = "dylan.brassard@outlook.com";
        String newPhone = "450-144-1223";
        Double newTotal = 2000.0;
        Double expectedRebate = 15.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.CREATED)
                .expectBody(ClientResponseModel.class)
                .value(dto ->{
                    assertNotNull(dto.getClientId());
                    assertEquals(dto.getFirstName(),newFN);
                    assertEquals(dto.getLastName(),newLN);
                    assertEquals(dto.getEmail(),newEmail);
                    assertEquals(dto.getPhoneNumber(),newPhone);
                    assertEquals(dto.getTotalBought(),newTotal);
                    assertEquals(dto.getRebate(),expectedRebate);
                });
    }
    @Test
    void createClientWithInvalidEmail_ShouldThrowConflict() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        String newEmail = "gdobrowolski9@networksolutions.com";
        String newPhone = "450-144-1223";
        Double newTotal = 2000.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertNotNull(dto);
                    assertNotNull(dto.getMessage());
                });
    }


    @Test
    void createClientWithInvalidBody_ShouldThrowBadRequest() {

        String newFN = null;
        String newLN = null;
        String newEmail = null;
        String newPhone = "450-144-1223";
        Double newTotal = 2000.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertNotNull(dto);
                });
    }

    @Test
    void createClientWithDuplicateName_ShouldThrowDuplicateClientInformationException() {


        String newEmail = "dylan.brassard@outlook.com";
        String newPhone = "450-144-1223";
        Double newTotal = 2000.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,VALID_CLIENT_FIRSTNAME,VALID_CLIENT_LASTNAME,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients");
                    assertEquals(dto.getMessage(),"A client with the name : " + clientRequestModel.getFirstName() +" "+ clientRequestModel.getLastName() + " already exists !");
                });
    }


    @Test
    void createClientWithInvalidTotal_ShouldThrowInvalidInputException() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        String newEmail = "dylan.brassard@outlook.com";
        String newPhone = "450-144-1223";
        Double newTotal = -10.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getMessage(),"A client can't have a negative total bought : " + clientRequestModel.getTotalBought());
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients");

                });
    }


    @Test
    void createClientWithInvalidEmail_ShouldThrowInvalidInputException() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        String newEmail = "dylan.brassardoutlook.com";
        String newPhone = "450-144-1223";
        Double newTotal = 2030.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getMessage(),"Email is in an invalid format ! : " + clientRequestModel.getEmail());
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients");

                });
    }


    @Test
    void createClientWithInvalidPhone_ShouldThrowInvalidInputException() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        String newEmail = "dylan.brassard@outlook.com";
        String newPhone = "4501441223";
        Double newTotal = 2030.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,newEmail,newPhone,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getMessage(),"Phone number is in an invalid format ! : " + clientRequestModel.getPhoneNumber());
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients");

                });
    }


    @Test
    void createClientWithNoPhoneAndEmail_ShouldThrowNoEmailAndPhoneException() {

        String newFN = "Dylan";
        String newLN = "Brassard";
        Double newTotal = 2030.0;

        ClientRequestModel clientRequestModel = createNewClientRequestModel(VALID_STORE_ID,newFN,newLN,null,null,newTotal);

        webTestClient.post().uri(BASE_URI_CLIENTS + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(clientRequestModel).accept(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getMessage(),"You must enter an email or a phone number");
                    assertEquals(dto.getPath(),"uri=" +BASE_URI_CLIENTS + "/clients");

                });
    }
    @Test
    void deleteClient_ShouldReturnNoContent() {
        webTestClient.delete().uri(BASE_URI_CLIENTS + "/clients/" +VALID_CLIENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNoContent();

        assertNull(clientRepository.findClientByClientIdentifier_ClientId(VALID_CLIENT_ID));
    }

    @Test
    void deleteClientWithInvalidId_ShouldThrowNotFoundException() {
        webTestClient.delete().uri(BASE_URI_CLIENTS + "/clients/" +1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound().expectBody(HttpErrorInfo.class)
                .value(dto ->{
                    assertEquals(dto.getPath(),"uri="+BASE_URI_CLIENTS + "/clients/" +1);
                    assertEquals(dto.getMessage(),"No client found with id : " + 1);
                });

    }


    private ClientRequestModel createNewClientRequestModel(String storeIdentifier, String fn, String ln,String email,String phone, Double total){
        return ClientRequestModel.builder()
                .storeId(storeIdentifier)
                .firstName(fn)
                .lastName(ln)
                .email(email)
                .phoneNumber(phone)
                .totalBought(total)
                .build();
    }
}