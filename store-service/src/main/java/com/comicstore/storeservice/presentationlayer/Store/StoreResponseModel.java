package com.comicstore.storeservice.presentationlayer.Store;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
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
