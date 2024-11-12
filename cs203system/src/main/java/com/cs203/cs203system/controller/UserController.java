package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.UserResponseDto;
import com.cs203.cs203system.dtos.players.UserResponseMapper;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user-related operations.
 *
 * This controller provides endpoints to retrieve information about all users
 * and individual users based on their ID.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    /**
     * Constructs a UserController with the necessary dependencies.
     *
     * @param userService the service for handling user-related operations
     * @param userResponseMapper the mapper for converting user entities to DTOs
     */
    @Autowired
    public UserController(UserService userService,
                          UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.userResponseMapper = userResponseMapper;
    }

    /**
     * Retrieves a list of all users.
     *
     * This endpoint returns a list of user details for all users in the system.
     *
     * @return a list of {@link UserResponseDto} objects representing all users
     */
    @GetMapping
    public List<UserResponseDto> getAllUser() {
        return userService
                .getAllUsers()
                .stream()
                .map(userResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves details of a specific user by their ID.
     *
     * This endpoint provides detailed information about a user, identified by their user ID.
     *
     * @param id the ID of the user to retrieve
     * @return a {@link UserResponseDto} object containing the details of the specified user
     * @throws NotFoundException if the user with the given ID does not exist
     */
    @GetMapping("{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userResponseMapper.toDto(userService
                .getUserById(id)
                .orElseThrow(() -> new NotFoundException("User id of " + id + " does not exist")));
    }
}

