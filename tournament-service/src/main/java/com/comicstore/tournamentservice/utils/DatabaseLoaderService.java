package com.comicstore.tournamentservice.utils;

import com.comicstore.tournamentservice.datalayer.*;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseLoaderService implements CommandLineRunner {

    @Autowired
    TournamentRepository tournamentRepository;

    @Override
    public void run(String... args) throws Exception {
        CardIdentifier cardIdentifier = new CardIdentifier("6dfc9e76-1aa7-4786-8398-f4ae25737324");
        ClientIdentifier winner = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier1 = new ClientIdentifier("b5521e15-ee99-4862-af8d-75e761a964f2");
        ClientIdentifier clientIdentifier2 = new ClientIdentifier("c80690a8-746d-4d23-817c-235579e516b3");
        ClientIdentifier clientIdentifier3 = new ClientIdentifier("e42a6c05-e304-4402-ac1a-e118d2ff278e");
        ClientIdentifier clientIdentifier4 = new ClientIdentifier("9d11ccd6-f518-47e0-aad5-4d75530dd53b");
        StoreIdentifier storeIdentifier = new StoreIdentifier("1b5fb4a0-8761-47a6-bacb-ab3c99f8c480");
        List<Result> results = new ArrayList<>();
        List<ClientIdentifier> players = new ArrayList<>();

        players.add(clientIdentifier1);
        players.add(clientIdentifier2);
        players.add(clientIdentifier3);
        players.add(clientIdentifier4);
        results.add(new Result(clientIdentifier1,1,3,0));
        results.add(new Result(clientIdentifier2,3,1,0));
        results.add(new Result(clientIdentifier3,1,2,1));
        results.add(new Result(clientIdentifier4,0,4,0));


        Tournament tournament1 = Tournament.builder()
                .tournamentIdentifier(new TournamentIdentifier("d2b04961-be64-45fd-8ea3-c273064338e3"))
                .cardGame(cardIdentifier)
                .results(results)
                .entryCost(5.50)
                .players(players)
                .location(storeIdentifier)
                .winner(winner)
                        .build();
        tournamentRepository.insert(tournament1);




        CardIdentifier cardIdentifier2 = new CardIdentifier("80c063e1-2cea-4145-87e4-aafbc5854795");
        ClientIdentifier winner2 = new ClientIdentifier("e42a6c05-e304-4402-ac1a-e118d2ff278e");


        Tournament tournament2 = Tournament.builder()
                .tournamentIdentifier(new TournamentIdentifier("7a6b0e9e-d71d-415a-a2e6-1ff84275adc7"))
                .cardGame(cardIdentifier2)
                .results(results)
                .entryCost(5.50)
                .players(players)
                .location(storeIdentifier)
                .winner(winner2)
                .build();
        tournamentRepository.insert(tournament2);
    }
}
