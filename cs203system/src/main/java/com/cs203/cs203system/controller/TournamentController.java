package com.cs203.cs203system.controller;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Tournament;
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
    public TournamentController (TournamentManagerService tournamentManagerService, TournamentResponseDTOMapper tournamentResponseDTOMapper,
                                 TournamentRequestDTOMapper tournamentRequestDTOMapper) {
        this.tournamentManagerService = tournamentManagerService;
        this.tournamentResponseDTOMapper = tournamentResponseDTOMapper;
        this.tournamentRequestDTOMapper = tournamentRequestDTOMapper;
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> findALLTournaments() {
        List<TournamentResponseDTO> tournamentResponseDTOList =
                tournamentManagerService.findAllTournaments()
                        .stream()
                        .map(tournamentResponseDTOMapper::toDto)
                        .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentResponseDTOList, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> findTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentManagerService.findTournamentById(id).orElseThrow(()-> new NotFoundException("Tournament not found"));
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }


    // This is how the tournaments
    // User will send a request to the back through this method.
    // User wants the all the pair all the players and get the back the tournament + list of new matches
    @PostMapping
    public ResponseEntity<TournamentResponseDTO> createTournament(@Valid @RequestBody TournamentRequestDTO tournamentRequestDTO) {
        // Map the DTO to the tournament and pass to the createTournament()
        Tournament tournament = tournamentManagerService.createTournament(tournamentRequestDTOMapper.toEntity(tournamentRequestDTO), tournamentRequestDTO.getPlayerIds().stream().toList());
        return new ResponseEntity<TournamentResponseDTO>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> deleteTournamentById(@PathVariable Long id) {
        tournamentManagerService.deleteTournamentById(id);
        return new ResponseEntity<TournamentResponseDTO>(HttpStatus.NO_CONTENT);
    }

}
