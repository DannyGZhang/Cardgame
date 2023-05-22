package com.comicstore.tournamentservice.presentationlayer;

import com.comicstore.tournamentservice.businesslayer.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/lab2/v1/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping()
    public ResponseEntity<TournamentClientCarGameResponseModel> createTournaments(@RequestBody TournamentRequestModel tournamentRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(tournamentRequest));
    }


    @GetMapping()
    public ResponseEntity<List<TournamentResponseModel>> getTournaments(@RequestParam Map<String, String> querryParams){
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.getTournaments(querryParams));
    }


    @GetMapping("/{tournamentId}")
    public ResponseEntity<TournamentClientCarGameResponseModel> getTournamentById(@PathVariable String tournamentId){
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.getTournamentById(tournamentId));
    }

    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(@PathVariable String tournamentId){
        tournamentService.deleteTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{tournamentId}")
    public ResponseEntity<TournamentClientCarGameResponseModel> updateTournament(@PathVariable String tournamentId, @RequestBody TournamentRequestModel tournamentRequestModel){
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.updateTournament(tournamentId,tournamentRequestModel));
    }





}
