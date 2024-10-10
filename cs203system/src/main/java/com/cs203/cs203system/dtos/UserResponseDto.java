package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.User;
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