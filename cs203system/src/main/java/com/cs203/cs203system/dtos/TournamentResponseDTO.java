package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.cs203.cs203system.model.Tournament}
 */
@Value
public class TournamentResponseDTO implements Serializable {
    Long id;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    String location;
    TournamentStatus status;
    Double minEloRating;
    Double maxEloRating;
    TournamentFormat format;
    Integer roundsCompleted;
    Integer currentRoundNumber;
    Integer totalSwissRounds;
    List<WinnerDto> winnersBracket;
    List<LoserDto> losersBracket;
    List<MatchDto> matches;
    AdminDto admin;
    List<PlayerDto> players;

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class WinnerDto implements Serializable {
        Long id;
        String username;
        String name;
        Double eloRating;
    }

    @Value
    public static class AdminDto implements Serializable {
        Long id;
        String username;
        String email;
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class LoserDto implements Serializable {
        Long id;
        String username;
        String name;
        Double eloRating;
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Match}
     */
    @Value
    public static class MatchDto implements Serializable {
        Long id;
        Integer durationInMinutes;
        MatchStatus status;
        MatchBracket bracket;
        Integer player1Score;
        Integer player2Score;
        LocalDate matchDate;
        Player1Dto player1;
        Player2Dto player2;

        @Value
        public static class Player1Dto implements Serializable {
            Long id;
            String username;
            String name;
            double points;
        }

        @Value
        public static class Player2Dto implements Serializable {
            Long id;
            String username;
            String name;
            double points;
        }
    }

    /**
     * DTO for {@link com.cs203.cs203system.model.Player}
     */
    @Value
    public static class PlayerDto implements Serializable {
        Long id;
        String username;
        String name;
    }
}