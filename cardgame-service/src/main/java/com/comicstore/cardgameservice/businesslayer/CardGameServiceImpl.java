package com.comicstore.cardgameservice.businesslayer;

import com.comicstore.cardgameservice.Utils.Exceptions.DuplicateCardGameNameException;
import com.comicstore.cardgameservice.Utils.Exceptions.DuplicateSetNameException;
import com.comicstore.cardgameservice.Utils.Exceptions.InvalidInputException;
import com.comicstore.cardgameservice.Utils.Exceptions.NotFoundException;
import com.comicstore.cardgameservice.datalayer.*;
import com.comicstore.cardgameservice.datamapperlayer.CardGameRequestMapper;
import com.comicstore.cardgameservice.datamapperlayer.CardGameResponseMapper;
import com.comicstore.cardgameservice.datamapperlayer.SetRequestMapper;
import com.comicstore.cardgameservice.datamapperlayer.SetResponseMapper;
import com.comicstore.cardgameservice.presentationlayer.CardGameRequestModel;
import com.comicstore.cardgameservice.presentationlayer.CardGameResponseModel;
import com.comicstore.cardgameservice.presentationlayer.SetRequestModel;
import com.comicstore.cardgameservice.presentationlayer.SetResponseModel;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardGameServiceImpl implements CardGameService {
    private final CardGameRepository cardGameRepository;
    private final SetRepository setRepository;

    //private final TournamentRepository tournamentRepository;
    private final SetRequestMapper setRequestMapper;
    private final SetResponseMapper setResponseMapper;
    private final CardGameResponseMapper cardGameResponseMapper;
    private final CardGameRequestMapper cardGameRequestMapper;


    public CardGameServiceImpl(CardGameRepository cardGameRepository, SetRepository setRepository,/* TournamentRepository tournamentRepository,*/ SetRequestMapper setRequestMapper, SetResponseMapper setResponseMapper, CardGameResponseMapper cardGameResponseMapper, CardGameRequestMapper cardGameRequestMapper) {
        this.cardGameRepository = cardGameRepository;
        this.setRepository = setRepository;
        //this.tournamentRepository = tournamentRepository;
        this.setRequestMapper = setRequestMapper;
        this.setResponseMapper = setResponseMapper;
        this.cardGameResponseMapper = cardGameResponseMapper;
        this.cardGameRequestMapper = cardGameRequestMapper;
    }

    @Override
    public List<CardGameResponseModel> getCardGames() {
        return cardGameResponseMapper.entitiesToResponseModel(cardGameRepository.findAll());
    }

    @Override
    public CardGameResponseModel updateCardGame(String cardGameId, CardGameRequestModel cardGameRequestModel) {

        CardGame existingCardGame = cardGameRepository.getCardGameByCardIdentifier_CardId(cardGameId);
        if (existingCardGame == null)
            throw new NotFoundException("Card game with id : " + cardGameId + " was not found !");

        CardGame cardGame = cardGameRequestMapper.requestModelToEntity(cardGameRequestModel);
        cardGame.setCardIdentifier(existingCardGame.getCardIdentifier());
        cardGame.setId(existingCardGame.getId());
        try {
            return cardGameResponseMapper.entityToResponseModel(cardGameRepository.save(cardGame));
        } catch (
                DataAccessException ex) {
            if (ex.getMessage().contains("constraint [card_game_name]") || ex.getCause().toString().contains("ConstraintViolationException")) {
                throw new DuplicateCardGameNameException("Name provided is a duplicate : " + cardGameRequestModel.getCardGameName());
            } else throw new InvalidInputException(("An unknown error as occurred"));
        }    }

    @Override
    public CardGameResponseModel getCardGame(String cardGameId) {
        CardGame cardGame = cardGameRepository.getCardGameByCardIdentifier_CardId(cardGameId);
        if (cardGame == null)
            throw new NotFoundException("Card game with id : " + cardGameId + " was not found !");

        return cardGameResponseMapper.entityToResponseModel(cardGame);
    }


    @Override
    public SetResponseModel addCardGameSet(String cardGameId, SetRequestModel setRequestModel) {

        CardGame cardGame = cardGameRepository.getCardGameByCardIdentifier_CardId(cardGameId);

        if (cardGame == null) {
            throw new NotFoundException("No card game with id : " + cardGameId + " was found !");
        }

        Set set = setRequestMapper.entityToResponseModel(setRequestModel, new CardIdentifier(setRequestModel.getCardId()));

        set.setCardIdentifier(cardGame.getCardIdentifier());

        if (cardGame.getReleaseDate().isAfter(set.getReleaseDate()))
            throw new InvalidInputException("A set can't be release be for the game : \nGame was released : " + cardGame.getReleaseDate() + " \nSimpleDateFormat entered for set : " + set.getReleaseDate());


        try {
            return setResponseMapper.entityToResponseModel(setRepository.save(set));
        } catch (
                DataAccessException ex) {
            if (ex.getMessage().contains("constraint [name]") || ex.getCause().toString().contains("ConstraintViolationException" )) {
                throw new DuplicateSetNameException("Name provided is a duplicate : " + setRequestModel.getName());
            } else throw new InvalidInputException(("An unknown error as occurred"));
        }
    }

    @Override
    public List<SetResponseModel> getCardGameSets(String cardGameId) {
        List<Set> sets = setRepository.getSetByCardIdentifier_CardId(cardGameId);
        return setResponseMapper.entitiesToResponseModel(sets);
    }

    @Override
    public List<SetResponseModel> getAllSets() {
        return setResponseMapper.entitiesToResponseModel(setRepository.findAll());
    }

    @Override
    public void deleteCardGame(String cardId) {
        CardGame cardGame = cardGameRepository.getCardGameByCardIdentifier_CardId(cardId);

        if (cardGame == null)
            throw new NotFoundException("No card game with id : " + cardId + " was found !");

        List<Set> sets = setRepository.getSetByCardIdentifier_CardId(cardId);
        if (sets != null)
            setRepository.deleteAll(sets);

        /*List<Tournament> tournaments = tournamentRepository.getTournamentsByCardGame_CardId(cardGame.getCardIdentifier().getCardId());
        if(tournaments == null)
            return;
        tournamentRepository.deleteAll(tournaments);

*/
        cardGameRepository.delete(cardGame);
    }

    @Override
    public CardGameResponseModel addCardGame(CardGameRequestModel cardGameRequestModel) {
        CardGame cardGame = cardGameRequestMapper.requestModelToEntity(cardGameRequestModel);

        try {
            return cardGameResponseMapper.entityToResponseModel(cardGameRepository.save(cardGame));
        } catch (
                DataAccessException ex) {
            if (ex.getMessage().contains("constraint [card_game_name]") || ex.getCause().toString().contains("ConstraintViolationException")) {
                throw new DuplicateCardGameNameException("Name provided is a duplicate : " + cardGameRequestModel.getCardGameName());
            } else throw new InvalidInputException(("An unknown error as occurred"));
        }
    }

    @Override
    public void deleteCardGameSet(String setId) {
        Set set = setRepository.getSetBySetIdentifier_SetId(setId);
        if (set == null)
            throw new NotFoundException("No set with id : " + setId + " was found !");

        setRepository.delete(set);
    }

    @Override
    public SetResponseModel updateCardGameSet(String setId, SetRequestModel setRequestModel) {
        Set existingSet = setRepository.getSetBySetIdentifier_SetId(setId);

        if (existingSet == null) {
            throw new NotFoundException("No set with id : " + setId + " was found !");
        }

        if (setRequestModel.getNumberOfCards() < 0)
            throw new InvalidInputException("Number of cards can't be negative : " + setRequestModel.getNumberOfCards());

        Set set = setRequestMapper.entityToResponseModel(setRequestModel, new CardIdentifier(setRequestModel.getCardId()));
        if(setRequestModel.getCardId() == null)
            set.setCardIdentifier(existingSet.getCardIdentifier());
        else
            set.setCardIdentifier(new CardIdentifier(setRequestModel.getCardId()));

        CardGame cardGame = cardGameRepository.getCardGameByCardIdentifier_CardId(set.getCardIdentifier().getCardId());
        if (cardGame.getReleaseDate().isAfter(set.getReleaseDate()))
            throw new InvalidInputException("A set can't be release be for the game : \nGame was released : " + cardGame.getReleaseDate() + " \nSimpleDateFormat entered for set : " + set.getReleaseDate());

        set.setId(existingSet.getId());
        set.setSetIdentifier(existingSet.getSetIdentifier());

        try {
            return setResponseMapper.entityToResponseModel(setRepository.save(set));
        } catch (
                DataAccessException ex) {
            if (ex.getMessage().contains("constraint [name]") || ex.getCause().toString().contains("ConstraintViolationException")) {
                throw new DuplicateCardGameNameException("Name provided is a duplicate : " + setRequestModel.getName());
            } else throw new InvalidInputException(("An unknown error as occurred"));
        }
    }
}
