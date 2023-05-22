package com.comicstore.apigateway.presentationlayer.tournament;


import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.Client.ClientResponseModel;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TournamentClientCarGameResponseModel extends RepresentationModel<TournamentClientCarGameResponseModel> {

    private String tournamentId;
    private List<ClientResponseModel> players;
    private CardGameResponseModel cardGame;
    private StoreResponseModel location;
    private double entryCost;
    private ClientResponseModel winner;
    private List<Result> results;
}
