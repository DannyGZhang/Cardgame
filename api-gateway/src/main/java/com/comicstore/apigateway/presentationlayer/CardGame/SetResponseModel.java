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
public class SetResponseModel extends RepresentationModel<SetResponseModel> {
    private String setId;

    private String cardGame;

    private String name;

    private LocalDate releaseDate;

    private int numberOfCards;

}
