package com.comicstore.clientservice.presentationlayer;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientResponseModel extends RepresentationModel<ClientResponseModel> {
    private String storeId;
    private String clientId;
    private String firstName;
    private String lastName;
    private int rebate;
    private double totalBought;

    private String email;
    private String phoneNumber;
}
