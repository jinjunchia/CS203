package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.Player;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Player}
 */
@Data
public class PlayerWithOutStatsDto implements Serializable {
    Long id;
    String username;
    String email;
    UserType userType;
    String name;
    Integer totalGamesPlayed;
    Double eloRating;
}