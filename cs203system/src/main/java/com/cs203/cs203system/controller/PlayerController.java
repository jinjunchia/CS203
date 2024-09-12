package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> findAllPlayers() {
        return new ResponseEntity<>(playerService.findAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> findPlayerById(@PathVariable Integer id) {
        Optional<Player> Player = playerService.findPlayerById(id);
        return Player
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Integer id, @Valid @RequestBody PlayerUpdateRequest updatedPlayer) {
        Player savedPlayer = playerService.updatePlayer(id, updatedPlayer);
        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@PathVariable Integer id, @Valid @RequestBody PlayerUpdateRequest updatedPlayer) {
        Player savedPlayer = playerService.updatePlayer(id, updatedPlayer);
        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return new ResponseEntity<>("Player deleted", HttpStatus.OK);
    }



}
