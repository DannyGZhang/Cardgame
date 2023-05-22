package com.comicstore.cardgameservice.presentationlayer;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.Date;

@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class SetResponseModel extends RepresentationModel<SetResponseModel> {
    private String setId;

    private String cardGame;

    private String name;

    private LocalDate releaseDate;

    private int numberOfCards;

}
