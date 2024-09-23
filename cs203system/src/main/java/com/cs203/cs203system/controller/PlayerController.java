package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/player")
@CrossOrigin("*")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerWithOutStatsDto>> findAllPlayers() {
        return new ResponseEntity<>(playerService.findAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerWithOutStatsDto> findPlayerById(@PathVariable Long id) {
        Optional<PlayerWithOutStatsDto> Player = playerService.findPlayerById(id);
        return Player
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<PlayerWithOutStatsDto> updatePlayer(@PathVariable Long id, @Valid @RequestBody PlayerUpdateRequest updatedPlayer) {
//        Player savedPlayer = playerService.updatePlayer(id, updatedPlayer);
//        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
//    }

    @PostMapping
    public ResponseEntity<PlayerWithOutStatsDto> createPlayer(@Valid @RequestBody CreateUserRequest createUserRequest) {
        PlayerWithOutStatsDto savedPlayer = playerService.createPlayer(createUserRequest);
        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deletePlayer(@PathVariable Long id) {
//        playerService.deletePlayer(id);
//        return new ResponseEntity<>("Player deleted", HttpStatus.OK);
//    }



}
