package com.cs203.cs203system.repository;

import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.cs203.cs203system.model.Player}
 */
@Data
public class PlayerDto implements Serializable {
    Long id;
    @NotNull
    String username;
    double points;
    Integer ranking;
    Integer totalGamesPlayed;
    Double eloRating;
    int wins;
    int losses;
    int draws;
    PlayerBracket bracket;
    PlayerStatus status;
}