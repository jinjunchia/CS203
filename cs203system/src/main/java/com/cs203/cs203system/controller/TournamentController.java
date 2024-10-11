package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.*;
import com.cs203.cs203system.dtos.players.PlayerResponseDTOMapper;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.TournamentManagerService;
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
import java.util.stream.Collectors;

/**
 * Controller for managing tournaments.
 */
@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    private final TournamentManagerService tournamentManagerService;
    private final TournamentResponseDTOMapper tournamentResponseDTOMapper;
    private final TournamentRequestDTOMapper tournamentRequestDTOMapper;
    private final InputMatchDTOMapper inputMatchDTOMapper;
    private final PlayerResponseDTOMapper playerResponseDTOMapper;

    /**
     * Constructs a TournamentController with the required services and mappers.
     *
     * @param tournamentManagerService the service to manage tournaments
     * @param tournamentResponseDTOMapper the mapper for TournamentResponseDTO
     * @param tournamentRequestDTOMapper the mapper for TournamentRequestDTO
     * @param inputMatchDTOMapper the mapper for InputMatchDTO
     * @param playerResponseDTOMapper the mapper for PlayerResponseDTO
     */
    @Autowired
    public TournamentController(TournamentManagerService tournamentManagerService, TournamentResponseDTOMapper tournamentResponseDTOMapper,
                                TournamentRequestDTOMapper tournamentRequestDTOMapper,
                                InputMatchDTOMapper inputMatchDTOMapper,
                                PlayerResponseDTOMapper playerResponseDTOMapper) {
        this.tournamentManagerService = tournamentManagerService;
        this.tournamentResponseDTOMapper = tournamentResponseDTOMapper;
        this.tournamentRequestDTOMapper = tournamentRequestDTOMapper;
        this.inputMatchDTOMapper = inputMatchDTOMapper;
        this.playerResponseDTOMapper = playerResponseDTOMapper;
    }

    /**
     * Retrieves a list of all tournaments.
     *
     * @return ResponseEntity containing the list of TournamentResponseDTOs
     */
    @Operation(summary = "Find all tournaments", description = "Retrieve a list of all tournaments.")
    @ApiResponse(responseCode = "200", description = "List of tournaments retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> findAllTournaments() {
        List<TournamentResponseDTO> tournamentResponseDTOList =
                tournamentManagerService.findAllTournaments()
                        .stream()
                        .map(tournamentResponseDTOMapper::toDto)
                        .collect(Collectors.toList());
        return new ResponseEntity<>(tournamentResponseDTOList, HttpStatus.OK);
    }

    /**
     * Retrieves details of a specific tournament using its ID.
     *
     * @param id the ID of the tournament to be retrieved
     * @return ResponseEntity containing the TournamentResponseDTO
     */
    @Operation(summary = "Find a tournament by ID", description = "Retrieve details of a specific tournament using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> findTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentManagerService.findTournamentById(id).orElseThrow(() -> new NotFoundException("Tournament not found"));
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }

    /**
     * Retrieves the winner of a specific tournament using the tournament ID.
     *
     * @param tournamentId the ID of the tournament
     * @return ResponseEntity containing the PlayerResponseDTO of the winner
     */
    @Operation(summary = "Find the winner of a tournament", description = "Retrieve the winner of a specific tournament using the tournament ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Winner retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlayerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content)
    })
    @GetMapping("/winner/{tournamentId}")
    public ResponseEntity<PlayerResponseDTO> findWinnerTournamentById(@PathVariable Long tournamentId) {
        return new ResponseEntity<>(playerResponseDTOMapper
                .toDto(tournamentManagerService.determineWinner(tournamentId)), HttpStatus.OK);
    }

    /**
     * Creates a new tournament with a list of players.
     *
     * @param tournamentRequestDTO the request data for creating a tournament
     * @return ResponseEntity containing the created TournamentResponseDTO
     */
    @Operation(summary = "Create a tournament", description = "Create a new tournament with a list of players.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tournament created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TournamentResponseDTO> createTournament(@Valid @RequestBody TournamentRequestDTO tournamentRequestDTO) {
        // Map the DTO to the tournament and pass to the createTournament()
        Tournament tournament = tournamentManagerService
                .createTournament(tournamentRequestDTOMapper
                        .toEntity(tournamentRequestDTO));
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.CREATED);
    }

    /**
     * Adds or removes players from an existing tournament.
     *
     * @param tournamentId the ID of the tournament to be updated
     * @param tournamentRequestDTO the request data containing player IDs to be added or removed
     * @return ResponseEntity containing the updated TournamentResponseDTO
     */
    @Operation(summary = "Update players in a tournament", description = "Add or remove players from an existing tournament.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content)
    })
    @PutMapping("/{tournamentId}/players")
    public ResponseEntity<TournamentResponseDTO> updatePlayersToTournament(@PathVariable Long tournamentId, @RequestBody TournamentUpdatePlayerDTO tournamentRequestDTO) {
        Tournament tournament = tournamentManagerService
                .updatePlayersToTournament(tournamentId, tournamentRequestDTO.getPlayerIds());
        return new ResponseEntity<>(tournamentResponseDTOMapper.toDto(tournament), HttpStatus.OK);
    }

    /**
     * Starts an existing tournament by its ID.
     *
     * @param tournamentId the ID of the tournament to be started
     * @return ResponseEntity containing the updated TournamentResponseDTO
     */
    @Operation(summary = "Start a tournament", description = "Start an existing tournament by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament started successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content)
    })
    @PutMapping("/start/{tournamentId}")
    public ResponseEntity<TournamentResponseDTO> startTournament(@PathVariable Long tournamentId) {
        return new ResponseEntity<>(tournamentResponseDTOMapper
                .toDto(tournamentManagerService.startTournament(tournamentId)), HttpStatus.OK);
    }

    /**
     * Updates the results of a match in a tournament.
     *
     * @param matchDTO the request data containing match results
     * @return ResponseEntity containing the updated TournamentResponseDTO
     */
    @Operation(summary = "Update match results", description = "Update results of a match in a tournament.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Match results updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid match data",
                    content = @Content)
    })
    @PutMapping("/match")
    public ResponseEntity<TournamentResponseDTO> updateMatchResults(@RequestBody InputMatchDTO matchDTO) {
        return new ResponseEntity<>(tournamentResponseDTOMapper
                .toDto(tournamentManagerService
                        .inputResult(inputMatchDTOMapper.toEntity(matchDTO))), HttpStatus.ACCEPTED);
    }

    /**
     * Deletes an existing tournament by its ID. Only tournaments in the scheduled state can be deleted.
     *
     * @param id the ID of the tournament to be deleted
     * @return ResponseEntity with HTTP status NO_CONTENT
     */
    @Operation(summary = "Delete a tournament", description = "Delete an existing tournament by its ID. Only tournaments in the scheduled state can be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tournament deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Tournament cannot be deleted",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> deleteTournamentById(@PathVariable Long id) {
        tournamentManagerService.deleteTournamentById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
