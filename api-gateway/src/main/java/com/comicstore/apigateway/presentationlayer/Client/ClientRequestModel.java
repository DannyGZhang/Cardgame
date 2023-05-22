package com.comicstore.apigateway.presentationlayer.Client;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientRequestModel {

    @NotBlank(message = "A store must be assigned to the client (the store where the client created his account)")
    private String storeId;
    @NotBlank(message = "First name must no be left empty !")
    private String firstName;
    private String lastName;

    private double totalBought;
    private String email;
    private String phoneNumber;
}
