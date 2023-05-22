package com.comicstore.clientservice.datamapperlayer;

import com.comicstore.clientservice.datalayer.Client;
import com.comicstore.clientservice.presentationlayer.ClientController;
import com.comicstore.clientservice.presentationlayer.ClientResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Mapper(componentModel = "spring")
public interface ClientResponseMapper {
    @Mapping(expression = "java(client.getClientIdentifier().getClientId())",  target = "clientId")
    @Mapping(expression = "java(client.getStoreIdentifier().getStoreId())",  target = "storeId")
    @Mapping(expression = "java(client.getContact().getEmail())", target = "email" )
    @Mapping(expression = "java(client.getContact().getPhoneNumber())", target = "phoneNumber" )
    ClientResponseModel entityToResponseModel(Client client);

    List<ClientResponseModel> entityListToResponseModelList(List<Client> clients);




}
