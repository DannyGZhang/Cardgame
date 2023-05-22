package com.comicstore.apigateway.presentationlayer.Store.Store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StoreResponseModel extends RepresentationModel<StoreResponseModel> {

    private String storeId;
    private LocalDate dateOpened;
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;
    private String email;
    private String status;
    private String phoneNumber;

}
