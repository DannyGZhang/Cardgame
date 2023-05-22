package com.comicstore.storeservice.presentationlayer.Store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;



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
