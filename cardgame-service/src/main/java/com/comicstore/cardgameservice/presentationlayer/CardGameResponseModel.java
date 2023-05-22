package com.comicstore.cardgameservice.presentationlayer;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.Date;

@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class CardGameResponseModel extends RepresentationModel<CardGameResponseModel> {
    private String cardId;

    private String cardGameName;

    private String company;

    private LocalDate releaseDate;

    private Boolean isActive;

}
