package com.comicstore.clientservice.datalayer;


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
class ClientPersistenceTest {

    @Autowired
    private ClientRepository clientRepository;

    private final String VALID_STORE_ID = "c293820a-d989-48ff-8410-24062a69d99e";
    private Client presavedClient;


    @BeforeEach
    public void setup(){
        clientRepository.deleteAll();

        Client client = new Client("Dylan","Brassard",1000);
        client.setStoreIdentifier(new StoreIdentifier(VALID_STORE_ID));

        presavedClient = clientRepository.save(client);

    }
    @Test
    void findClientByClientIdentifier_ShouldSucceed() {
        Client found = clientRepository.findClientByClientIdentifier_ClientId(presavedClient.getClientIdentifier().getClientId());

        assertThat(found, samePropertyValuesAs(presavedClient));
    }

    @Test
    void existsByFirstNameAndLastName_ShouldBeTrue() {
        Boolean found = clientRepository.existsByFirstNameAndLastName(presavedClient.getFirstName(),presavedClient.getLastName());

        assertTrue(found);
    }

    @Test
    void findClientByFirstNameAndLastName_ShouldReturnClient() {
        Client found = clientRepository.findClientByFirstNameAndLastName(presavedClient.getFirstName(),presavedClient.getLastName());

        assertThat(found, samePropertyValuesAs(presavedClient));
    }

    @Test
    void existsByEmail_ShouldReturnClient() {
        presavedClient.getContact().setEmail("dylan.brassard@outlook.com");

        Boolean found = clientRepository.existsByContact_Email(presavedClient.getContact().getEmail());

        assertTrue(found);
    }

    @Test
    void findClientByStoreIdentifier_ShouldReturnTwoClient() {
        Client client = new Client("Denisa","Hategan",1000);

        client.setStoreIdentifier(new StoreIdentifier(VALID_STORE_ID));

        clientRepository.save(client);
        List<Client> clientList = clientRepository.findClientByStoreIdentifier_StoreId(presavedClient.getStoreIdentifier().getStoreId());

        int expectedNumOfClients = 2;

        assertEquals(expectedNumOfClients,clientList.size());

    }
}