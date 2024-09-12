package com.cs203.cs203system.dtos;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

@Data
public class PlayerUpdateRequest implements Serializable {
    //    @NotNull(message = "ID cannot be null")
    Optional<String> name = Optional.empty();

    //    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the format YYYY-MM-DD")
}
