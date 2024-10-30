package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.MatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.cs203.cs203system.model.Match}
 */
@Value
public class InputMatchDTO implements Serializable {
    @NotNull
    Long id;
    Integer durationInMinutes;
    @NotNull
    MatchStatus status;
    @NotNull
    Integer player1Score;
    @NotNull
    Integer player2Score;
    LocalDateTime matchDate;
}