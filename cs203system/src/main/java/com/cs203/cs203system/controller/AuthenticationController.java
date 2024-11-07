package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.LoginRequest;
import com.cs203.cs203system.dtos.players.LoginResponse;
import com.cs203.cs203system.dtos.players.UserResponseMapper;
import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.dtos.UserResponseDto;
import com.cs203.cs203system.service.impl.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user authentication and registration.
 */
@RestController
@RequestMapping("api/auth")
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserResponseMapper userResponseMapper;

    /**
     * Constructs an AuthenticationController with the required service and mapper.
     *
     * @param authenticationService the service for authentication operations
     * @param userResponseMapper the mapper for UserResponseDto
     */
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                    UserResponseMapper userResponseMapper) {
        this.authenticationService = authenticationService;
        this.userResponseMapper = userResponseMapper;
    }

    /**
     * Registers a new user.
     *
     * @param body the request body containing user details
     * @return the registered User
     */
    @Operation(summary = "Register a new user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content)
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerUser(@RequestBody CreateUserRequest body){
        return userResponseMapper.toDto(authenticationService.register(body));
    }

    /**
     * Logs in a user.
     *
     * @param body the request body containing login credentials
     * @return ResponseEntity containing the LoginResponse
     */
    @Operation(summary = "Login a user", description = "Authenticates a user and returns a login response containing a JWT token if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest body){
        LoginResponse loginResponse = authenticationService.loginUser(body.getUsername(), body.getPassword());
        if (loginResponse.getUser() == null) {
            return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing the UserResponseDto
     */
    @Operation(summary = "Get current authenticated user", description = "Retrieves the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content)
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();
        User user = authenticationService.getCurrentUser(username);
        return new ResponseEntity<>(userResponseMapper.toDto(user), HttpStatus.OK);
    }

}
