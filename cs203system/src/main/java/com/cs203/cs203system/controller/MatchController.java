package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.MatchResponseDTO;
import com.cs203.cs203system.model.MatchResponseDTOMapper;
import com.cs203.cs203system.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class to manage API endpoints for Match-related operations.
 * Provides functionality to fetch all matches and find a match by its ID.
 * <br>
 * Admins should not be allowed to update the matches directly.
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;
    private final MatchResponseDTOMapper matchResponseDTOMapper;

    /**
     * Constructor for MatchController.
     *
     * @param matchService           Service layer for handling match operations.
     * @param matchResponseDTOMapper Mapper to convert Match entities to MatchResponseDTO.
     */
    @Autowired
    public MatchController(MatchService matchService,
                           MatchResponseDTOMapper matchResponseDTOMapper) {
        this.matchService = matchService;
        this.matchResponseDTOMapper = matchResponseDTOMapper;
    }

    /**
     * Fetch all matches from the system.
     *
     * @return ResponseEntity containing a list of MatchResponseDTOs and an HTTP status of OK.
     */
    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAllMatches() {
        return new ResponseEntity<>(matchService.
                findAllMatches()
                .stream()
                .map(matchResponseDTOMapper::toDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Fetch a match by its ID.
     *
     * @param id The ID of the match to be fetched.
     * @return ResponseEntity containing the MatchResponseDTO of the found match and an HTTP status of OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> findMatchById(@PathVariable Long id) {
        return new ResponseEntity<>(matchResponseDTOMapper
                .toDto(matchService.findMatchById(id)), HttpStatus.OK);
    }

    /**
     * Fetch all matches related to a specific tournament by its ID.
     *
     * @param id The ID of the tournament.
     * @return ResponseEntity containing a list of MatchResponseDTOs and an HTTP status of OK.
     */
    @GetMapping("/tournament/{id}")
    public ResponseEntity<List<MatchResponseDTO>> findTournamentMatches(@PathVariable Long id) {
        return new ResponseEntity<>(matchService
                .findAllMatchesByTournamentId(id)
                .stream()
                .map(matchResponseDTOMapper::toDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

}
