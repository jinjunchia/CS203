package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.LoginRequest;
import com.cs203.cs203system.dtos.players.LoginResponse;
import com.cs203.cs203system.dtos.players.UserResponseMapper;
import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.model.UserResponseDto;
import com.cs203.cs203system.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                    UserResponseMapper userResponseMapper) {
        this.authenticationService = authenticationService;
        this.userResponseMapper = userResponseMapper;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody CreateUserRequest body){
        return authenticationService.register(body);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest body){
        LoginResponse loginResponse = authenticationService.loginUser(body.getUsername(), body.getPassword());
        if (loginResponse.getUser() == null) {
            return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();
        User user = authenticationService.getCurrentUser(username);
        return new ResponseEntity<>(userResponseMapper.toDto(user), HttpStatus.OK);
    }

}
