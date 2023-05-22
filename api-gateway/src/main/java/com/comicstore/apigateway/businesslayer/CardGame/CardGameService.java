package com.comicstore.apigateway.businesslayer.CardGame;

import com.comicstore.apigateway.presentationlayer.CardGame.CardGameRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.CardGameResponseModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetRequestModel;
import com.comicstore.apigateway.presentationlayer.CardGame.SetResponseModel;

import java.util.List;

public interface CardGameService {

    void deleteCardGameSet(String setId);

    void deleteCardGame(String cardGameId);

    CardGameResponseModel addCardGame(CardGameRequestModel cardGameRequestModel);


    SetResponseModel[] getCardGameSets(String cardGameId);

    SetResponseModel addCardGameSet(String cardGameId, SetRequestModel setRequestModel);

    CardGameResponseModel[] getCardGames();

    CardGameResponseModel getCardGame(String cardGameId);

    void updateCardGameSet(String setId, SetRequestModel setRequestModel);

    void updateCardGame(String cardId, CardGameRequestModel cardGameRequestModel);
}
