package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/player")
@CrossOrigin("*")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerWithOutStatsDto> getPlayerById(@PathVariable Long playerId) {
        return new ResponseEntity<>(playerService.findPlayerById(playerId).orElseThrow(NotFoundException::new), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<PlayerWithOutStatsDto>> getAllPlayer() {
        return new ResponseEntity<>(playerService.findAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PlayerWithOutStatsDto>> getPlayerRanking() {
        return new ResponseEntity<>(playerService.findAllPlayersOrderByEloRating(), HttpStatus.OK);
    }
}
