package com.cs203.cs203system.dtos.players;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStatsDTO {
    private Long playerId;
    private String playerName;
    private int totalPunches;
    private int totalDodges;
    private int totalKOs;

    // Constructor
    public PlayerStatsDTO(Long playerId, String playerName, int totalPunches, int totalDodges, int totalKOs) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.totalPunches = totalPunches;
        this.totalDodges = totalDodges;
        this.totalKOs = totalKOs;
    }

    // Helper methods to increment stats
    public PlayerStatsDTO addPunches(int punches) {
        this.totalPunches += punches;
        return this;
    }

    public PlayerStatsDTO addDodges(int dodges) {
        this.totalDodges += dodges;
        return this;
    }

    public PlayerStatsDTO addKOs(int kos) {
        this.totalKOs += kos;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerStatsDTO{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", totalPunches=" + totalPunches +
                ", totalDodges=" + totalDodges +
                ", totalKOs=" + totalKOs +
                '}';
    }
}
