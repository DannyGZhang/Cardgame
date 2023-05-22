package com.comicstore.apigateway.presentationlayer.CardGame;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

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
