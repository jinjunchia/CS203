package com.cs203.cs203system.dtos.players;

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

    // Getters and Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTotalPunches() {
        return totalPunches;
    }

    public void setTotalPunches(int totalPunches) {
        this.totalPunches = totalPunches;
    }

    public int getTotalDodges() {
        return totalDodges;
    }

    public void setTotalDodges(int totalDodges) {
        this.totalDodges = totalDodges;
    }

    public int getTotalKOs() {
        return totalKOs;
    }

    public void setTotalKOs(int totalKOs) {
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
