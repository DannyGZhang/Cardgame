package com.comicstore.tournamentservice.businesslayer;

import com.comicstore.tournamentservice.datalayer.*;
import com.comicstore.tournamentservice.datamapperlayer.TournamentClientCardGameResponseMapper;
import com.comicstore.tournamentservice.datamapperlayer.TournamentRequestMapper;
import com.comicstore.tournamentservice.datamapperlayer.TournamentResponseMapper;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.CardGame.CardGameServiceClient;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientRequestModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Client.ClientServiceClient;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreResponseModel;
import com.comicstore.tournamentservice.domainclientlayer.Store.StoreServiceClient;
import com.comicstore.tournamentservice.presentationlayer.TournamentClientCarGameResponseModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentRequestModel;
import com.comicstore.tournamentservice.presentationlayer.TournamentResponseModel;
import com.comicstore.tournamentservice.utils.exceptions.IllegalEntryCostChange;
import com.comicstore.tournamentservice.utils.exceptions.InvalidInputException;
import com.comicstore.tournamentservice.utils.exceptions.NotFoundException;
import com.comicstore.tournamentservice.utils.exceptions.WinnerNotInPlayerListException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRequestMapper tournamentRequestMapper;

    private final TournamentResponseMapper tournamentResponseMapper;


    private final TournamentClientCardGameResponseMapper tournamentClientCardGameResponseMapper;

    private final TournamentRepository tournamentRepository;

    private final CardGameServiceClient cardGameServiceClient;
    private final ClientServiceClient clientServiceClient;
    private final StoreServiceClient storeServiceClient;


    @Override
    public TournamentClientCarGameResponseModel createTournament(TournamentRequestModel tournamentRequestModel) {
        if(tournamentRequestModel.getEntryCost() < 0)
            throw new InvalidInputException("Entry cost is negative !");
        List<Result> playersIds = tournamentRequestModel.getResults();
        ArrayList<ClientResponseModel> players = new ArrayList<>();

        for (Result playerId : playersIds) {

            ClientResponseModel tempClient = clientServiceClient.getClientAggregateById(playerId.getClientId());
            if (tempClient == null) {
                throw new NotFoundException("One of the players does not exist with ID : " + playerId.getClientId());
            }
            tempClient.setTotalBought(tempClient.getTotalBought() + tournamentRequestModel.getEntryCost());
            ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                    .email(tempClient.getEmail())
                    .phoneNumber(tempClient.getPhoneNumber())
                    .firstName(tempClient.getFirstName())
                    .lastName(tempClient.getLastName())
                    .storeId(tempClient.getStoreId())
                    .totalBought(tempClient.getTotalBought())
                    .build();
            clientServiceClient.updateClient(clientRequestModel, tempClient.getClientId());
            players.add(tempClient);

        }


        Tournament tournament = tournamentRequestMapper.requestModelToEntity(tournamentRequestModel, new CardIdentifier(tournamentRequestModel.getCardGame()), new ClientIdentifier(tournamentRequestModel.getWinner()));

        tournament.setTournamentIdentifier(new TournamentIdentifier());

        return getTournamentClientResponseModel(tournamentRequestModel, players, tournament);
    }

    @Override
    public List<TournamentResponseModel> getTournaments(Map<String, String> querryParams) {
        String playerId = querryParams.get("playerId");
        String cardGameId = querryParams.get("cardGameId");
        String winnerId = querryParams.get("winnerId");

        if (playerId != null && cardGameId != null) {
            ClientResponseModel player = clientServiceClient.getClientAggregateById(playerId);
            if (player == null)
                throw new NotFoundException("The players does not exist with ID : " + playerId);

            return tournamentResponseMapper.entitiesToResponseModel(tournamentRepository.getTournamentsByCardGame_CardIdAndPlayers(cardGameId, new ClientIdentifier(player.getClientId())));

        }


        if (playerId != null) {
            ClientResponseModel player = clientServiceClient.getClientAggregateById(playerId);

            return tournamentResponseMapper.entitiesToResponseModel(tournamentRepository.getTournamentsByPlayers(new ClientIdentifier(player.getClientId())));

        }

        if (winnerId != null) {
            ClientResponseModel winner = clientServiceClient.getClientAggregateById(winnerId);

            return tournamentResponseMapper.entitiesToResponseModel(tournamentRepository.getTournamentsByWinner(new ClientIdentifier(winner.getClientId())));
        }


        if (cardGameId != null) {

            CardGameResponseModel cardGameResponseModel = cardGameServiceClient.getCardGameById(cardGameId);
             return tournamentResponseMapper.entitiesToResponseModel(tournamentRepository.getTournamentsByCardGame_CardId(cardGameResponseModel.getCardId()));
        }

        return tournamentResponseMapper.entitiesToResponseModel(tournamentRepository.findAll());
    }

    @Override
    public TournamentClientCarGameResponseModel getTournamentById(String tournamentId) {


        Tournament tournament = tournamentRepository.getTournamentByTournamentIdentifier_TournamentId(tournamentId);


        if (tournament == null) {
            throw new NotFoundException("Tournament with ID : " + tournamentId + " does not exist !");
        }
        List<Result> playersIds = tournament.getResults();
        ArrayList<ClientResponseModel> players = new ArrayList<>();

        for (Result playerId : playersIds
        ) {
            ClientResponseModel tempClient = clientServiceClient.getClientAggregateById(playerId.getClientId());

            players.add(tempClient);

        }
        CardGameResponseModel cardGame = cardGameServiceClient.getCardGameById(tournament.getCardGame().getCardId());

        ClientResponseModel winner = clientServiceClient.getClientAggregateById(tournament.getWinner().getClientId());

        StoreResponseModel store = storeServiceClient.getStoreById(tournament.getLocation().getStoreId());

        return tournamentClientCardGameResponseMapper.entityToResponseModel(tournament, players, cardGame, winner, tournament.getResults(), store);
    }

    @Override
    public TournamentClientCarGameResponseModel updateTournament(String tournamentId, TournamentRequestModel tournamentRequestModel) {


        Tournament existingTournament = tournamentRepository.getTournamentByTournamentIdentifier_TournamentId(tournamentId);

        if (existingTournament == null)
            throw new NotFoundException("Tournament with ID : " + tournamentId + " does not exist !");


        if (existingTournament.getEntryCost() != tournamentRequestModel.getEntryCost())
            throw new IllegalEntryCostChange("The entry cost must be the same as the one defined at the time of creation \nEntry cost on creation : " + existingTournament.getEntryCost() + "\nThe one entered in update : " + tournamentRequestModel.getEntryCost());
        List<Result> playersIds = tournamentRequestModel.getResults();
        ArrayList<ClientResponseModel> players = new ArrayList<>();

        for (Result playerId : playersIds
        ) {
            ClientResponseModel tempClient = clientServiceClient.getClientAggregateById(playerId.getClientId());


            List<String> playersString = new ArrayList<>();
            existingTournament.getPlayers().forEach(clientIdentifier -> playersString.add(clientIdentifier.getClientId()));

            if(!playersString.contains(tempClient.getClientId())) {
                tempClient.setTotalBought(tempClient.getTotalBought() + tournamentRequestModel.getEntryCost());
                ClientRequestModel clientRequestModel = ClientRequestModel.builder()
                        .email(tempClient.getEmail())
                        .phoneNumber(tempClient.getPhoneNumber())
                        .firstName(tempClient.getFirstName())
                        .lastName(tempClient.getLastName())
                        .storeId(tempClient.getStoreId())
                        .totalBought(tempClient.getTotalBought())
                        .build();
                clientServiceClient.updateClient(clientRequestModel, playerId.getClientId());
            }
            players.add(tempClient);

        }

     

        Tournament tournament = tournamentRequestMapper.requestModelToEntity(tournamentRequestModel, new CardIdentifier(tournamentRequestModel.getCardGame()), new ClientIdentifier(tournamentRequestModel.getWinner()));
        tournament.setId(existingTournament.getId());
        tournament.setTournamentIdentifier(existingTournament.getTournamentIdentifier());
        tournament.setLocation(existingTournament.getLocation());

        return getTournamentClientResponseModel(tournamentRequestModel, players, tournament);
    }

    private TournamentClientCarGameResponseModel getTournamentClientResponseModel(TournamentRequestModel tournamentRequestModel, ArrayList<ClientResponseModel> players, Tournament tournament) {
        ClientResponseModel winner = clientServiceClient.getClientAggregateById(tournament.getWinner().getClientId());


        StoreResponseModel storeResponseModel = storeServiceClient.getStoreById(tournamentRequestModel.getLocation());



        CardGameResponseModel cardGame = cardGameServiceClient.getCardGameById(tournamentRequestModel.getCardGame());



        List<Result> results = tournament.getResults();
        ArrayList<ClientIdentifier> clientIdentifiers = new ArrayList<>();
        for (ClientResponseModel player : players) {
            clientIdentifiers.add(new ClientIdentifier(player.getClientId()));
        }

        List<String> playersString = new ArrayList<>();
        clientIdentifiers.forEach(clientIdentifier -> playersString.add(clientIdentifier.getClientId()));

        if (!playersString.contains(winner.getClientId())) {
            throw new WinnerNotInPlayerListException("The winner was not part of the result list");
        }


        tournament.setLocation(new StoreIdentifier(tournamentRequestModel.getLocation()));
        tournament.setPlayers(clientIdentifiers.stream().toList());

        return tournamentClientCardGameResponseMapper.entityToResponseModel(tournamentRepository.save(tournament), players.stream().toList(), cardGame, winner, results, storeResponseModel);
    }

    @Override
    public void deleteTournament(String tournamentId) {
        Tournament tournament = tournamentRepository.getTournamentByTournamentIdentifier_TournamentId(tournamentId);
        if (tournament == null)
            throw new NotFoundException("Tournament was not found with id : " + tournamentId);
        tournamentRepository.delete(tournament);
    }


}
