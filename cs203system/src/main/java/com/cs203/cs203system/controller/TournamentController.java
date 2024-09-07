package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.TournamentUpdateRequest;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }


    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return new ResponseEntity<>(tournamentService.findAllTournaments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Integer id) {
        Optional<Tournament> tournament = tournamentService.findTournamentById(id);
        return tournament
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        return new ResponseEntity<>(tournamentService.createTournament(tournament), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Integer id, @Valid @RequestBody TournamentUpdateRequest updatedTournament) {
        Tournament savedTournament = tournamentService.updateTournament(id, updatedTournament);
        return new ResponseEntity<>(savedTournament, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Integer id) {
        tournamentService.deleteTournamentById(id);
        return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
    }
}
