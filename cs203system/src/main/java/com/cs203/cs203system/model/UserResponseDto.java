package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.UserType;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserResponseDto implements Serializable {
    Long id;
    String username;
    String email;
    UserType userType;
}