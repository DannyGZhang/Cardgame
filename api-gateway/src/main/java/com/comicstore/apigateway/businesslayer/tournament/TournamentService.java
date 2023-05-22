package com.comicstore.apigateway.businesslayer.tournament;

import com.comicstore.apigateway.presentationlayer.tournament.TournamentClientCarGameResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentRequestModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentResponseModel;

import java.util.Map;

public interface TournamentService {
    TournamentClientCarGameResponseModel createTournament(TournamentRequestModel tournamentRequestModel);

    TournamentResponseModel[] getTournaments(Map<String, String> querryParams);

    TournamentClientCarGameResponseModel getTournamentById(String tournamentId);

    void updateTournament(String tournamentId, TournamentRequestModel tournamentRequestModel);

    void deleteTournament(String tournamentId);

}
