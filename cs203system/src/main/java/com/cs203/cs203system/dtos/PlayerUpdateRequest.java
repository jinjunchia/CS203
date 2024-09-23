package com.cs203.cs203system.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class PlayerUpdateRequest implements Serializable {
    Optional<String> name = Optional.empty();

    Optional<String> username = Optional.empty();

    Optional<String> email = Optional.empty();
}
