package com.comicstore.apigateway.presentationlayer.CardGame;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SetRequestModel {
    private String cardId;

    @NotBlank
    private String name;

    @NotBlank
    private String releaseDate;

    private int numberOfCards;
}
