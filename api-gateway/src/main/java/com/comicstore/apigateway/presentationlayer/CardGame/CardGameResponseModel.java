package com.comicstore.apigateway.presentationlayer.CardGame;

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
public class CardGameResponseModel extends RepresentationModel<CardGameResponseModel> {
    private String cardId;

    private String cardGameName;

    private String company;

    private LocalDate releaseDate;

    private Boolean isActive;

}
