package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.UserType;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.cs203.cs203system.model.Player}
 */
@Value
public class PlayerResponseDTO implements Serializable {
    Long id;
    String username;
    String email;
    UserType userType;
    String name;
    Double eloRating;
}