package com.comicstore.cardgameservice.businesslayer;


import com.comicstore.cardgameservice.presentationlayer.CardGameRequestModel;
import com.comicstore.cardgameservice.presentationlayer.CardGameResponseModel;
import com.comicstore.cardgameservice.presentationlayer.SetRequestModel;
import com.comicstore.cardgameservice.presentationlayer.SetResponseModel;

import java.util.List;

public interface CardGameService {
    List<CardGameResponseModel> getCardGames();

    CardGameResponseModel updateCardGame(String cardGameId, CardGameRequestModel cardGameRequestModel);


    CardGameResponseModel getCardGame(String cardGameId);

    SetResponseModel addCardGameSet(String cardGameId, SetRequestModel setRequestModel);

    List<SetResponseModel> getCardGameSets(String cardGameId);

    List<SetResponseModel> getAllSets();

    void deleteCardGame(String cardId);

    CardGameResponseModel addCardGame(CardGameRequestModel cardGameRequestModel);

    void deleteCardGameSet(String setId);

    SetResponseModel updateCardGameSet(String setId, SetRequestModel setRequestModel);
}
