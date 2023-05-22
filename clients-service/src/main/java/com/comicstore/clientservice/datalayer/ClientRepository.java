package com.comicstore.clientservice.datalayer;

import com.comicstore.clientservice.presentationlayer.ClientResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {

Client findClientByClientIdentifier_ClientId(String ClientId);

Boolean existsByFirstNameAndLastName(String firstName,String lastName);

Client findClientByFirstNameAndLastName(String firstName,String lastName);

Boolean existsByContact_Email(String email);

List<Client> findClientByStoreIdentifier_StoreId(String storeId);

}
