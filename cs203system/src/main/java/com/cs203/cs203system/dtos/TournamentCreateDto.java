package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * DTO for {@link Tournament}
 */
@Value
public class TournamentCreateDto implements Serializable {

    @NotNull(message = "Name cannot be null")
    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @NotNull(message = "Location cannot be null")
    String location;

    @NotNull(message = "Format cannot be null")
    TournamentFormat format;

//    @NotNull
//    Double minEloRating; // Minimum ELO rating allowed for participants
//
//    @NotNull
//    Double maxEloRating; // Maximum ELO rating allowed for participants admins can set this

    List<Long> playerIds;
}