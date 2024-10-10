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
    Player1Dto player1;
    Player2Dto player2;
    TournamentDto tournament;
    RoundDto round;
    WinnerDto winner;

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class Player1Dto implements Serializable {
        Long id;
        String username;
        UserType userType;
        String name;
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
     * DTO for {@link com.cs203.cs203system.model.Round}
     */
    @Value
    public static class RoundDto implements Serializable {
        Long Id;
        int roundNumber;
        RoundType roundType;
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