package com.cs203.cs203system.dtos.players;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
}
