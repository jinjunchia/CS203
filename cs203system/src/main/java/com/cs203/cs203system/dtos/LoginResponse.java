package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserResponseDto user;
    private String jwt;
}
