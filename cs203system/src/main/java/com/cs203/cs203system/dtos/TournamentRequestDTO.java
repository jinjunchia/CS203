package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Tournament;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Tournament}
 */
@Value
public class TournamentRequestDTO implements Serializable {
    Long id;

    @NotNull
    String name;

    @NotNull
    LocalDate startDate;

    @NotNull
    String location;

    @NotNull
    Double minEloRating;

    @NotNull
    Double maxEloRating;

    @NotNull
    TournamentFormat format;
}
