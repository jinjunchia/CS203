package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.cs203.cs203system.model.Player}
 */
@Data
public class CreateUserRequest implements Serializable {
    @NotNull(message = "Username cannot be null")
    String username;
    @NotNull(message = "Password cannot be null.")
    String password;
    @NotNull(message = "Email cannot be null.")
    @Email
    String email;
    @NotBlank(message = "Name cannot be null.")
    String name;
    @NotNull(message = "userType cannot be null.")
    private UserType userType;
}