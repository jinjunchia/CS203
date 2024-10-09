package com.cs203.cs203system.controller;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Tournament}
 */
@Value
public class TournamentResponseDTO implements Serializable {
    Long id;
    String name;
    String location;
    List<MatchDto> matches;

    /**
     * DTO for {@link Match}
     */
    @Value
    public static class MatchDto implements Serializable {
        Long id;
        MatchBracket bracket;
        Integer player1Score;
        Integer player2Score;
        LocalDate matchDate;
        Long winnerId;
        String winnerName;
    }
}