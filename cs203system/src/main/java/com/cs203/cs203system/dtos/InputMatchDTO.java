package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.MatchStatus;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.cs203.cs203system.model.Match}
 */
@Value
public class InputMatchDTO implements Serializable {
    Long id;
    Integer durationInMinutes;
    MatchStatus status;
    Integer player1Score;
    Integer player2Score;
    LocalDate matchDate;
}