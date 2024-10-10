package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.TournamentRequestDTO;
import com.cs203.cs203system.dtos.TournamentRequestDTOMapper;
import com.cs203.cs203system.dtos.TournamentUpdatePlayerDTO;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.dtos.TournamentResponseDTO;
import com.cs203.cs203system.dtos.TournamentResponseDTOMapper;
import com.cs203.cs203system.service.TournamentManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    private final TournamentManagerService tournamentManagerService;
    private final TournamentResponseDTOMapper tournamentResponseDTOMapper;
    private final TournamentRequestDTOMapper tournamentRequestDTOMapper;

    @Autowired
    public TournamentController(TournamentManagerService tournamentManagerService, TournamentResponseDTOMapper tournamentResponseDTOMapper,
                                TournamentRequestDTOMapper tournamentRequestDTOMapper) {
        this.tournamentManagerService = tournamentManagerService;
        this.tournamentResponseDTOMapper = tournamentResponseDTOMapper;
        this.tournamentRequestDTOMapper = tournamentRequestDTOMapper;
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> findAllTournaments() {
        List<TournamentResponseDTO> tournamentResponseDTOList =
                tournamentManagerService.findAllTournaments()
                        .stream()
                        .map(tournamentResponseDTOMapper::toDto)
                        .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentResponseDTOList, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> findTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentManagerService.findTournamentById(id).orElseThrow(() -> new NotFoundException("Tournament not found"));
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }


    // This is how the tournaments are created
    // User will send a request to the back through this method.
    // User wants the all the pair all the players and get the back the tournament + list of new matches
    @PostMapping
    public ResponseEntity<TournamentResponseDTO> createTournament(@Valid @RequestBody TournamentRequestDTO tournamentRequestDTO) {
        // Map the DTO to the tournament and pass to the createTournament()
        Tournament tournament = tournamentManagerService
                .createTournament(tournamentRequestDTOMapper
                        .toEntity(tournamentRequestDTO));
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.CREATED);
    }

    // This is simply find the existing tournament and updates the list of players in the tournament
    // Update means it can add or remove players.
    @PutMapping("/{tournamentId}/players")
    public ResponseEntity<TournamentResponseDTO> updatePlayersToTournament(@PathVariable Long tournamentId, @RequestBody TournamentUpdatePlayerDTO tournamentRequestDTO) {
        Tournament tournament = tournamentManagerService
                .updatePlayersToTournament(tournamentId, tournamentRequestDTO.getPlayerIds());
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }

    // This simply starts the tournament
    @PutMapping("/start/{tournamentId}")
    public ResponseEntity<TournamentResponseDTO> startTournament(@PathVariable Long tournamentId) {
        return new ResponseEntity<>(tournamentResponseDTOMapper
                .toDto(tournamentManagerService.startTournament(tournamentId)), HttpStatus.OK);
    }

    @PutMapping("/{tournamentId}/match/{matchId}")
    public ResponseEntity<TournamentResponseDTO> updateMatchResults(@PathVariable Long tournamentId, @RequestBody Match match) {
        return null;
    }

    // This simply deletes the tournament. It will delete the Orphan Match, but it should not delete the players in it.
    // Note that a tournament can only be deleted if it's scheduled. For record keeping, previous completed or ongoing tournaments should be tracked.
    @DeleteMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> deleteTournamentById(@PathVariable Long id) {
        tournamentManagerService.deleteTournamentById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
