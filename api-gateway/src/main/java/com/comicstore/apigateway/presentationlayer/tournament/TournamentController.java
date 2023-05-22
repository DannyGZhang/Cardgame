package com.comicstore.apigateway.presentationlayer.tournament;

import com.comicstore.apigateway.businesslayer.tournament.TournamentService;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameController;
import com.comicstore.apigateway.presentationlayer.Client.ClientsController;
import com.comicstore.apigateway.presentationlayer.Store.Store.StoreController;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/lab2/v1/tournaments")
public class TournamentController {

    ////todo: use response entity
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping()
    public ResponseEntity<TournamentClientCarGameResponseModel> createTournaments(@RequestBody TournamentRequestModel tournamentRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(addLinks(tournamentService.createTournament(tournamentRequest)));
    }


    @GetMapping()
    public ResponseEntity<List<TournamentResponseModel>> getTournaments(@RequestParam Map<String, String> querryParams){
        List<TournamentResponseModel> tournamentResponseModels = Arrays.stream(tournamentService.getTournaments(querryParams)).toList();

        tournamentResponseModels.forEach(tournamentResponseModel -> addLinksTournament(tournamentResponseModel));
        return ResponseEntity.status(HttpStatus.OK).body(tournamentResponseModels);
    }


    @GetMapping("/{tournamentId}")
    public TournamentClientCarGameResponseModel getTournamentById(@PathVariable String tournamentId){
        return addLinks(tournamentService.getTournamentById(tournamentId));
    }

    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(@PathVariable String tournamentId){
        tournamentService.deleteTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{tournamentId}")
    public ResponseEntity<Void> updateTournament(@PathVariable String tournamentId, @RequestBody TournamentRequestModel tournamentRequestModel){
        tournamentService.updateTournament(tournamentId,tournamentRequestModel);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    public static TournamentClientCarGameResponseModel addLinks(@MappingTarget TournamentClientCarGameResponseModel model){


        model.setCardGame(CardGameController.addLinksCardGame(model.getCardGame()));

        model.setWinner(ClientsController.addLinks(model.getWinner()));

        model.setLocation(StoreController.addLinks(model.getLocation()));

        model.getPlayers().forEach(ClientsController::addLinks);

        Link selfLink = linkTo(methodOn(TournamentController.class).getTournamentById(model.getTournamentId())).withSelfRel();
        model.add(selfLink);

        Link allTournaments = linkTo(methodOn(TournamentController.class).getTournaments(null)).withRel("Tournaments");
        model.add(allTournaments);

        return model;
    }


    public static TournamentResponseModel addLinksTournament(@MappingTarget TournamentResponseModel model){

        Link selfLink = linkTo(methodOn(TournamentController.class).getTournamentById(model.getTournamentId())).withSelfRel();
        model.add(selfLink);

        Link allTournaments = linkTo(methodOn(TournamentController.class).getTournaments(null)).withRel("Tournaments");
        model.add(allTournaments);

        Link cardGameLink = linkTo(methodOn(CardGameController.class).getCardGame(model.getCardGame())).withRel("Card game");
        model.add(cardGameLink);

        Link storeLink = linkTo(methodOn(StoreController.class).getStore(model.getLocation())).withRel("Store where the game was played");
        model.add(storeLink);


        Link winnerLink = linkTo(methodOn(ClientsController.class).getClientAggregateById(model.getWinner())).withRel("Winner");
        model.add(winnerLink);

        model.getResults().forEach(result -> {

            result.getClientId();
            Link playerLink = linkTo(methodOn(ClientsController.class).getClientAggregateById(result.getClientId())).withRel("Player : " + result.getClientId());

            model.add(playerLink);

        });



        return model;
    }
}
