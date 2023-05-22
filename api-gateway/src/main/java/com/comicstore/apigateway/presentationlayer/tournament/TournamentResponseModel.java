package com.comicstore.apigateway.presentationlayer.tournament;

import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TournamentResponseModel extends RepresentationModel<TournamentResponseModel> {
    private String tournamentId;

    private String location;
    private String cardGame;

    private double entryCost;
    private String winner;
    private List<Result> results;
}
