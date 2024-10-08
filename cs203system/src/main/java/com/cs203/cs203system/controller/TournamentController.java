package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.*;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.service.TournamentService;
import com.cs203.cs203system.utility.TournamentManager;
import com.cs203.cs203system.utility.TournamentManagerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournament")
@CrossOrigin("*")
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;
    private final TournamentManager tournamentManager;
    private final PlayerRepository playerRepository;
    private final TournamentResponseDTOMapper tournamentResponseDTOMapper;

    @Autowired
    public TournamentController(TournamentService tournamentService,
                                TournamentMapper tournamentMapper, TournamentManagerImpl tournamentManager,
                                PlayerRepository playerRepository,
                                TournamentResponseDTOMapper tournamentResponseDTOMapper) {
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
        this.tournamentManager = tournamentManager;
        this.playerRepository = playerRepository;
        this.tournamentResponseDTOMapper = tournamentResponseDTOMapper;
    }


    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return new ResponseEntity<>(tournamentService.findAllTournaments(), HttpStatus.OK);
    }

    @Operation(summary = "Get a tournament by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tournament",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content) })
    @GetMapping(path= "/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentService.findTournamentById(id);
        return tournament
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<TournamentResponseDto> createTournament(@RequestBody @Valid TournamentCreateDto tournamentCreateDto) {
        System.out.println("Players added: " + tournamentCreateDto.getPlayerIds());
        //Create Tournament First
        Tournament tournament = tournamentManager.initializeTournament(tournamentCreateDto);
        tournamentService.addPlayerToTournament(tournament.getId(),tournamentCreateDto.getPlayerIds());
        tournamentResponseDTOMapper.linkMatches(tournament);
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @Valid @RequestBody TournamentUpdateRequest updatedTournament) {
        Tournament savedTournament = tournamentService.updateTournament(id, updatedTournament);
        return new ResponseEntity<>(savedTournament, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournamentById(id);
        return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
    }
}