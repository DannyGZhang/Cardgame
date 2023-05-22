package com.comicstore.apigateway.presentationlayer.CardGame;

import com.comicstore.apigateway.businesslayer.CardGame.CardGameService;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import jakarta.validation.Valid;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/lab2/v1/cardgames",
        produces = "application/json"
)
public class CardGameController {

    private final CardGameService cardGameService;
    private final String dateFormat = "^\\d{4}-\\d{2}-\\d{2}$";


    public CardGameController(CardGameService cardGameService) {
        this.cardGameService = cardGameService;
    }

    //
    @GetMapping()
    public ResponseEntity<List<CardGameResponseModel>> getCardGames() {
        List<CardGameResponseModel> cardGameResponseModels = Arrays.stream(cardGameService.getCardGames()).toList();
        cardGameResponseModels.forEach(cardGameResponseModel -> addLinksCardGame(cardGameResponseModel));
        return ResponseEntity.status(HttpStatus.OK).body(cardGameResponseModels);
    }


    @GetMapping("/{cardGameId}")
    public ResponseEntity<CardGameResponseModel> getCardGame(@PathVariable String cardGameId) {
        return ResponseEntity.status(HttpStatus.OK).body(addLinksCardGame(cardGameService.getCardGame(cardGameId)));
    }

    //
    @PostMapping("/{cardGameId}/sets")
    public ResponseEntity<SetResponseModel> addSetToCardGame(@PathVariable String cardGameId, @Valid @RequestBody SetRequestModel setRequestModel) {
        if (!Pattern.compile(dateFormat).matcher(setRequestModel.getReleaseDate()).matches())
            throw new InvalidInputException("Release date entered is not in format YYYY-MM-DD : " + setRequestModel.getReleaseDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(addLinksSets(cardGameService.addCardGameSet(cardGameId, setRequestModel)));
    }

    //
    @GetMapping("/{cardGameId}/sets")
    public ResponseEntity<List<SetResponseModel>> getCardGameSets(@PathVariable String cardGameId) {
        List<SetResponseModel> setResponseModels = Arrays.stream(cardGameService.getCardGameSets(cardGameId)).toList();
        setResponseModels.forEach(setResponseModel -> addLinksSets(setResponseModel));
        return ResponseEntity.status(HttpStatus.OK).body(setResponseModels);
    }


    //
    @DeleteMapping("/{cardGameId}")
    public ResponseEntity<Void> deleteCardGame(@PathVariable String cardGameId) {
        cardGameService.deleteCardGame(cardGameId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //
    @PostMapping()
    public ResponseEntity<CardGameResponseModel> addCardGame(@Valid @RequestBody CardGameRequestModel cardGameRequestModel) {
        if (!Pattern.compile(dateFormat).matcher(cardGameRequestModel.getReleaseDate().toString()).matches())
            throw new InvalidInputException("Release date entered is not in format YYYY-MM-DD : " + cardGameRequestModel.getReleaseDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(addLinksCardGame(cardGameService.addCardGame(cardGameRequestModel)));
    }

    //
    @DeleteMapping("/sets/{setId}")
    ResponseEntity<Void> deleteCardGameSet(@PathVariable String setId) {
        cardGameService.deleteCardGameSet(setId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //
    @PutMapping("/sets/{setId}")
    ResponseEntity<SetResponseModel> updateSet(@Valid @RequestBody SetRequestModel setRequestModel, @PathVariable String setId) {
        if (!Pattern.compile(dateFormat).matcher(setRequestModel.getReleaseDate()).matches())
            throw new InvalidInputException("Release date entered is not in format YYYY-MM-DD : " + setRequestModel.getReleaseDate());
        cardGameService.updateCardGameSet(setId, setRequestModel);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //
    @PutMapping("/{cardId}")
    ResponseEntity<CardGameResponseModel> updateCardGame(@PathVariable String cardId, @Valid @RequestBody CardGameRequestModel cardGameRequestModel) {
        if (!Pattern.compile(dateFormat).matcher(cardGameRequestModel.getReleaseDate().toString()).matches())
            throw new InvalidInputException("Release date entered is not in format YYYY-MM-DD : " + cardGameRequestModel.getReleaseDate());
        cardGameService.updateCardGame(cardId, cardGameRequestModel);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    SetResponseModel addLinksSets(@MappingTarget SetResponseModel model) {
        Link selfLink = linkTo(methodOn(CardGameController.class)
                .getCardGameSets(model.getSetId()))
                .withSelfRel();

        model.add(selfLink);

        Link cardgameLink = linkTo(methodOn(CardGameController.class)
                .getCardGame(model.getCardGame()))
                .withRel("Card Game");

        model.add(cardgameLink);


        Link cardgamesLink = linkTo(methodOn(CardGameController.class)
                .getCardGames())
                .withRel("Card Games");

        model.add(cardgamesLink);

        return model;
    }


    public static CardGameResponseModel addLinksCardGame(@MappingTarget CardGameResponseModel model) {
        Link selfLink = linkTo(methodOn(CardGameController.class)
                .getCardGame(model.getCardId()))
                .withSelfRel();

        model.add(selfLink);

        Link setsForCardGameLink = linkTo(methodOn(CardGameController.class)
                .getCardGameSets(model.getCardId()))
                .withRel("Sets for card game");

        model.add(setsForCardGameLink);

        Link cardgamesLink = linkTo(methodOn(CardGameController.class)
                .getCardGames())
                .withRel("Card Games");

        model.add(cardgamesLink);

        return model;


    }
}

