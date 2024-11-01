package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.PlayerStatsDTO;
import com.cs203.cs203system.service.PlayerStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/player-stats")
public class PlayerStatsController {

    @Autowired
    private PlayerStatsService playerStatsService;

    /**
     * Endpoint to retrieve aggregated statistics for all players.
     *
     * @return A list of PlayerStatsDto with punches, dodges, and KOs for each player.
     */
    @GetMapping
    public List<PlayerStatsDTO> getAllPlayerStats() {
        return playerStatsService.getAllPlayerStats();
    }
}
