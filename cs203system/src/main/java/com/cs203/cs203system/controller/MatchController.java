package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.MatchResponseDTO;
import com.cs203.cs203system.dtos.MatchResponseDTOMapper;
import com.cs203.cs203system.dtos.MatchStatsUpdateRequest;
import com.cs203.cs203system.dtos.players.PlayerStatsDTO;
import com.cs203.cs203system.service.MatchService;
import com.cs203.cs203system.service.PlayerStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin("*")
public class MatchController {

    private final MatchService matchService;
    private final MatchResponseDTOMapper matchResponseDTOMapper;
    private final PlayerStatsService playerStatsService;

    /**
     * Constructor for MatchController.
     *
     * @param matchService           Service layer for handling match operations.
     * @param matchResponseDTOMapper Mapper to convert Match entities to MatchResponseDTO.
     */
    @Autowired
    public MatchController(MatchService matchService,
                           MatchResponseDTOMapper matchResponseDTOMapper, PlayerStatsService playerStatsService) {
        this.matchService = matchService;
        this.matchResponseDTOMapper = matchResponseDTOMapper;
        this.playerStatsService = playerStatsService;
    }

    /**
     * Fetch all matches from the system.
     *
     * @return ResponseEntity containing a list of MatchResponseDTOs and an HTTP status of OK.
     */
    @Operation(summary = "Find all matches", description = "Retrieve a list of all matches.")
    @ApiResponse(responseCode = "200", description = "List of matches retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponseDTO.class)))
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
    @Operation(summary = "Find a match by ID", description = "Retrieve details of a specific match using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Match not found",
                    content = @Content)
    })
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
    @Operation(summary = "Find all matches by tournament ID", description = "Retrieve a list of all matches related to a specific tournament using the tournament ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of matches retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content)
    })
    @GetMapping("/tournament/{id}")
    public ResponseEntity<List<MatchResponseDTO>> findTournamentMatches(@PathVariable Long id) {
        return new ResponseEntity<>(matchService
                .findAllMatchesByTournamentId(id)
                .stream()
                .map(matchResponseDTOMapper::toDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping("/match/{id}/stats")
    public ResponseEntity<Void> updateMatchStats(@PathVariable Long id,
                                                 @RequestBody MatchStatsUpdateRequest request) {
        matchService.updateAndSaveMatchStats(id, request.getPunchesPlayer1(), request.getPunchesPlayer2(),
                request.getDodgesPlayer1(), request.getDodgesPlayer2(),
                request.isKoByPlayer1(), request.isKoByPlayer2());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/player-stats")
    public List<PlayerStatsDTO> getAllPlayerStats() {
        return playerStatsService.getAllPlayerStats();
    }


}
