package com.cs203.cs203system.controller;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Tournament;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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

//    LocalDate endDate;

    @NotNull
    String location;

    @NotNull
    TournamentFormat format;

    @Size(min = 2)
    List<Long> playerIds;
}
