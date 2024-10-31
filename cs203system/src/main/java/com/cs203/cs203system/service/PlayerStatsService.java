package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.players.PlayerStatsDTO;
import java.util.List;

public interface PlayerStatsService {

    /**
     * Retrieves aggregated stats for all players.
     *
     * @return A list of PlayerStatsDto with total punches, dodges, and KOs for each player.
     */
    List<PlayerStatsDTO> getAllPlayerStats();
}
