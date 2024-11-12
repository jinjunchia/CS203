package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.PlayerStatsDTO;
import com.cs203.cs203system.service.PlayerStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing player statistics.
 *
 * This controller provides endpoints to retrieve aggregated statistics for all players
 * as well as individual player statistics based on player ID.
 */
@RestController
@RequestMapping("/api/player-stats")
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;

    /**
     * Constructs a PlayerStatsController with the necessary dependency.
     *
     * @param playerStatsService the service for handling player statistics operations
     */
    @Autowired
    public PlayerStatsController(PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    /**
     * Retrieves aggregated statistics for all players.
     *
     * This endpoint provides a summary of statistics, such as punches, dodges, and KOs, for each player.
     *
     * @return a list of {@link PlayerStatsDTO} objects containing the statistics for all players
     */
    @GetMapping
    public List<PlayerStatsDTO> getAllPlayerStats() {
        return playerStatsService.getAllPlayerStats();
    }

    /**
     * Retrieves statistics for a specific player by their ID.
     *
     * This endpoint provides detailed statistics for a single player, identified by their player ID.
     *
     * @param playerId the ID of the player whose statistics are to be retrieved
     * @return a {@link PlayerStatsDTO} object containing the statistics for the specified player
     */
    @GetMapping("/player/{playerId}")
    public PlayerStatsDTO getPlayerStatsByPlayerId(@PathVariable Long playerId) {
        return playerStatsService.getPlayerStatsByPlayerId(playerId);
    }
}

