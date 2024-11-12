package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Tournament;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Tournament}
 */
@Value
public class TournamentRequestDTO implements Serializable {
    Long id;

    @NotNull(message="Name cannot be null")
    String name;

    @NotNull(message="Date cannot be null")
    LocalDate startDate;

    @NotNull(message="Location cannot be null")
    String location;

    @NotNull(message="Minimum EloRating cannot be null")
    Double minEloRating;

    @NotNull(message="Maximum EloRating cannot be null")
    Double maxEloRating;

    @NotNull(message = "Tournament format cannot be null")
    TournamentFormat format;

    @Length(max = 1000)
    String description;
}
