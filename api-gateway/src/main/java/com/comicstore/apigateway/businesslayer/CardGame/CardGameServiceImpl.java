package com.comicstore.apigateway.businesslayer.CardGame;

import com.comicstore.apigateway.domainclientlayer.CardGame.CardGameServiceClient;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CardGameServiceImpl implements CardGameService{

    private final CardGameServiceClient cardGameServiceClient;


    public CardGameServiceImpl(CardGameServiceClient cardGameServiceClient) {
        this.cardGameServiceClient = cardGameServiceClient;
    }

    @Override
    public void deleteCardGameSet(String setId) {
        cardGameServiceClient.deleteSet(setId);
    }

    @Override
    public void deleteCardGame(String cardGameId) {
        cardGameServiceClient.deleteCardGame(cardGameId);
    }

    @Override
    public CardGameResponseModel addCardGame(CardGameRequestModel cardGameRequestModel) {
        return cardGameServiceClient.createNewCardGame(cardGameRequestModel);
    }



    @Override
    public SetResponseModel[] getCardGameSets(String cardGameId) {
        return cardGameServiceClient.getAllCardGameSets(cardGameId);
    }

    @Override
    public SetResponseModel addCardGameSet(String cardGameId, SetRequestModel setRequestModel) {
        return cardGameServiceClient.createNewCardGameSet(setRequestModel,cardGameId);
    }

    @Override
    public CardGameResponseModel[] getCardGames() {
        return cardGameServiceClient.getAllCardGames();
    }

    @Override
    public CardGameResponseModel getCardGame(String cardGameId) {
        return cardGameServiceClient.getCardGameById(cardGameId);
    }

    @Override
    public void updateCardGameSet(String setId, SetRequestModel setRequestModel) {
        cardGameServiceClient.updateCardGameSet(setRequestModel,setId);
    }

    @Override
    public void updateCardGame(String cardId, CardGameRequestModel cardGameRequestModel) {
        cardGameServiceClient.updateCardGame(cardGameRequestModel,cardId);

    }
}
