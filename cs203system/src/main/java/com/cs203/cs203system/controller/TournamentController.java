package com.cs203.cs203system.controller;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.TournamentService;
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

    /**
     * Get all items (READ operation)
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return new ResponseEntity<>(tournamentService.findAllTournaments(), HttpStatus.OK);
    }

    /**
     * Get a single item by ID (READ operation)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Integer id) {
        Optional<Tournament> tournament = tournamentService.findTournamentById(id);
        return tournament
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create a new Tournament (CREATE operation)
     * TODO: Use Tournament DTOs
     *
     * @param tournament
     * @return
     */
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        return new ResponseEntity<>(tournamentService.createTournament(tournament), HttpStatus.CREATED);
    }

    /**
     * Update an item by ID (UPDATE operation)
     * <p>
     * TODO: Edit update
     *
     * @param id
     * @param updatedTournament
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Integer id, @RequestBody Tournament updatedTournament) {
        Optional<Tournament> existingTournament = tournamentService.findTournamentById(id);

        if (existingTournament.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Tournament savedTournament = tournamentService.updateTournament(existingTournament.get());

        return new ResponseEntity<>(savedTournament, HttpStatus.OK);
    }

    /**
     * Delete an item by ID (DELETE operation)
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Integer id) {
        tournamentService.deleteTournamentById(id);
        return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
    }
}
