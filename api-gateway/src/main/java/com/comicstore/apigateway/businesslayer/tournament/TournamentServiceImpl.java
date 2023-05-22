package com.comicstore.apigateway.businesslayer.tournament;

import com.comicstore.apigateway.domainclientlayer.tournament.TournamentServiceClient;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentClientCarGameResponseModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentRequestModel;
import com.comicstore.apigateway.presentationlayer.tournament.TournamentResponseModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TournamentServiceImpl implements TournamentService{

    private final TournamentServiceClient tournamentServiceClient;

    public TournamentServiceImpl(TournamentServiceClient tournamentServiceClient) {
        this.tournamentServiceClient = tournamentServiceClient;
    }

    @Override
    public TournamentClientCarGameResponseModel createTournament(TournamentRequestModel tournamentRequestModel) {
        return tournamentServiceClient.createNewTournament(tournamentRequestModel);
    }

    @Override
    public TournamentResponseModel[] getTournaments(Map<String, String> querryParams) {
        return tournamentServiceClient.getAllTournaments();
    }

    @Override
    public TournamentClientCarGameResponseModel getTournamentById(String tournamentId) {
        return tournamentServiceClient.getTournamentByID(tournamentId);
    }

    @Override
    public void updateTournament(String tournamentId, TournamentRequestModel tournamentRequestModel) {
        tournamentServiceClient.updateTournament(tournamentRequestModel,tournamentId);
    }

    @Override
    public void deleteTournament(String tournamentId) {
        tournamentServiceClient.deleteTournament(tournamentId);
    }
}
