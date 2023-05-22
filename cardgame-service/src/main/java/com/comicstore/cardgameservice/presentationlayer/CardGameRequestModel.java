package com.comicstore.cardgameservice.presentationlayer;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardGameRequestModel {
    @NotBlank
    private String cardGameName;

    @NotBlank
    private String company;

    private String releaseDate;

    private Boolean isActive;
}
