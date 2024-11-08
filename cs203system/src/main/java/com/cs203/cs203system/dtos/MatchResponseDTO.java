package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.*;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.cs203.cs203system.model.Match}
 */
@Value
public class MatchResponseDTO implements Serializable {
    Long id;
    Integer durationInMinutes;
    MatchStatus status;
    MatchBracket bracket;
    Integer player1Score;
    Integer player2Score;
    LocalDate matchDate;
    Integer round;
    Player1Dto player1;
    Player2Dto player2;
    TournamentDto tournament;
    WinnerDto winner;

    // New fields for punches and dodges
    Integer punchesPlayer1;
    Integer punchesPlayer2;
    Integer dodgesPlayer1;
    Integer dodgesPlayer2;

    // New fields for KO information
    boolean koByPlayer1;   // Whether Player 1 performed a KO
    boolean koByPlayer2;   // Whether Player 2 performed a KO

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class Player1Dto implements Serializable {
        Long id;
        String username;
        UserType userType;
        String name;
        Double eloRating;
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class Player2Dto implements Serializable {
        Long id;
        String username;
        UserType userType;
        String name;
        Double eloRating;
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Tournament}
     */
    @Value
    public static class TournamentDto implements Serializable {
        Long id;
        String name;
        LocalDate startDate;
        LocalDate endDate;
        String location;
        TournamentStatus status;
        Double minEloRating;
        Double maxEloRating;
        TournamentFormat format;
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class WinnerDto implements Serializable {
        Long id;
        String username;
        UserType userType;
        String name;
    }
}