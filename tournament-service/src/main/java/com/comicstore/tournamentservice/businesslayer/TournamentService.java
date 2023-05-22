package com.comicstore.tournamentservice.businesslayer;

import com.comicstore.tournamentservice.presentationlayer.TournamentClientCarGameResponseModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentRequestModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentResponseModel;

import java.util.List;
import java.util.Map;

public interface TournamentService {
    TournamentClientCarGameResponseModel createTournament(TournamentRequestModel tournamentRequestModel);

    List<TournamentResponseModel> getTournaments(Map<String, String> querryParams);

    TournamentClientCarGameResponseModel getTournamentById(String tournamentId);

    TournamentClientCarGameResponseModel updateTournament(String tournamentId, TournamentRequestModel tournamentRequestModel);

    void deleteTournament(String tournamentId);



}
