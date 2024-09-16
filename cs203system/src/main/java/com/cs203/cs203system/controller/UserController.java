package com.cs203.cs203system.controller;


import com.cs203.cs203system.dtos.UserUpdateRequest;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController (UserService userService) { this.userService = userService;}

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser (@PathVariable Integer id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("Player deleted", HttpStatus.OK);
    }
}
