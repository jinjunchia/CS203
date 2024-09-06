package com.cs203.cs203system.controller;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    /**
     * Get all items (READ operation)
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Get a single item by ID (READ operation)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Create a new Tournament (CREATE operation)
     *
     * @param tournament
     * @return
     */
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /**
     * Update an item by ID (UPDATE operation)
     *
     * @param id
     * @param updatedTournament
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateItem(@PathVariable Long id, @RequestBody Tournament updatedTournament) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Delete an item by ID (DELETE operation)
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        boolean removed = true;
        if (removed) {
            return new ResponseEntity<>("Item deleted successfully", HttpStatus.OK); // 200 OK
        } else {
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
