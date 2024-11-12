package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.UserResponseDto;
import com.cs203.cs203system.dtos.players.UserResponseMapper;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public UserController(UserService userService,
                          UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.userResponseMapper = userResponseMapper;
    }

    @GetMapping
    public List<UserResponseDto> getAllUser() {
        return userService.
                getAllUsers()
                .stream()
                .map(userResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userResponseMapper.toDto(userService
                .getUserById(id)
                .orElseThrow(() -> new NotFoundException("User id of " + id + " does not exist")));
    }
}
