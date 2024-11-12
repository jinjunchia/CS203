package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing player-related operations.
 *
 * This controller provides endpoints to retrieve player information,
 * including individual player details, all players, and player rankings.
 */
@RestController
@RequestMapping("api/player")
public class PlayerController {

    private final PlayerService playerService;

    /**
     * Constructs a PlayerController with the necessary dependency.
     *
     * @param playerService the service for handling player-related operations
     */
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Retrieves details of a specific player by their ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return a {@link ResponseEntity} containing the {@link PlayerWithOutStatsDto} of the specified player
     *         and an HTTP 200 OK status
     * @throws NotFoundException if the player with the given ID does not exist
     */
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerWithOutStatsDto> getPlayerById(@PathVariable Long playerId) {
        return new ResponseEntity<>(playerService.findPlayerById(playerId).orElseThrow(NotFoundException::new), HttpStatus.OK);
    }

    /**
     * Retrieves a list of all players.
     *
     * @return a {@link ResponseEntity} containing a list of {@link PlayerWithOutStatsDto} objects for all players
     *         and an HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<PlayerWithOutStatsDto>> getAllPlayer() {
        return new ResponseEntity<>(playerService.findAllPlayers(), HttpStatus.OK);
    }

    /**
     * Retrieves a ranking of all players sorted by their Elo rating.
     *
     * @return a {@link ResponseEntity} containing a list of {@link PlayerWithOutStatsDto} objects
     *         representing players ordered by Elo rating and an HTTP 200 OK status
     */
    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerWithOutStatsDto>> getPlayerRanking() {
        return new ResponseEntity<>(playerService.findAllPlayersOrderByEloRating(), HttpStatus.OK);
    }
}

