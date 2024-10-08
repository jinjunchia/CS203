package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Match}
 */
@Value
public class MatchDto implements Serializable {
    Long id;
    Long player1Id;
    PlayerBracket player1Bracket;
    Long player2Id;
    PlayerBracket player2Bracket;
    Long tournamentId;
    String tournamentName;
    LocalDate tournamentStartDate;
    LocalDate tournamentEndDate;
    String tournamentLocation;
    TournamentStatus tournamentStatus;
    Long winnerId;
}