package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.*;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.cs203.cs203system.model.Tournament}
 */
@Value
public class TournamentResponseDto implements Serializable {
    Long id;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    String location;
    TournamentStatus status;
    Double minEloRating;
    Double maxEloRating;
    Integer roundsCompleted;
    Integer currentRoundNumber;
    Integer totalSwissRounds;
    boolean doubleEliminationStarted;
    TournamentFormat format;
    List<MatchDto> matches;

    /**
     * DTO for {@link com.cs203.cs203system.model.Match}
     */
    @Value
    public static class MatchDto implements Serializable {
        Long id;
        Long winnerId;
        String winnerUsername;
        String winnerPassword;
        String winnerEmail;
        UserType winnerUserType;
        String winnerName;
        double winnerPoints;
        Integer winnerRanking;
        Integer winnerTotalGamesPlayed;
        int winnerTournamentLosses;
        Double winnerEloRating;
        int winnerWins;
        int winnerLosses;
        int winnerDraws;
        PlayerBracket winnerBracket;
        PlayerStatus winnerStatus;
    }
}