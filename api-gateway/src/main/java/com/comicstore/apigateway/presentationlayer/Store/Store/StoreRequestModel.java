package com.comicstore.apigateway.presentationlayer.Store.Store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreRequestModel {
    private String dateOpened;
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;
    private String email;
    private String status;
    private String phoneNumber;
}
