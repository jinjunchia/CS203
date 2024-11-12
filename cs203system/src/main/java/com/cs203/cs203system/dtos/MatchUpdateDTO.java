package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Match;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Match}
 */
@Value
public class MatchUpdateDTO implements Serializable {
    @NotNull(message = "Match date cannot be null")
    LocalDateTime matchDate;
    @Length(max = 1000)
    String description;
}