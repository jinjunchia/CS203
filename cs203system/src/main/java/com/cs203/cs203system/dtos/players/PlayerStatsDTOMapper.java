package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.model.Player;

public class PlayerStatsDTOMapper {

    /**
     * Maps a Player and aggregated stats to a PlayerStatsDTO.
     *
     * @param player       The player entity containing basic player information.
     * @param totalPunches Total punches for the player across all matches.
     * @param totalDodges  Total dodges for the player across all matches.
     * @param totalKOs     Total knockouts for the player across all matches.
     * @return PlayerStatsDto containing the player's ID, name, and aggregated stats.
     */
    public static PlayerStatsDTO mapToDto(Player player, int totalPunches, int totalDodges, int totalKOs) {
        return new PlayerStatsDTO(
                player.getId(),
                player.getName(),
                totalPunches,
                totalDodges,
                totalKOs
        );
    }
}

