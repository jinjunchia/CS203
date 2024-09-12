package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.TeamUpdateRequest;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.service.PlayerService;
import com.cs203.cs203system.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<Team>> findAllTeams() {
        return new ResponseEntity<>(teamService.findAllTeams(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> findTeamById(@PathVariable Integer id) {
        Optional<Team> Team = teamService.findTeamById(id);
        return Team
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Integer id, @Valid @RequestBody TeamUpdateRequest teamUpdateRequest) {
        Team updatedTeam = teamService.updateTeam(id, teamUpdateRequest);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return new ResponseEntity<Team>(teamService.createTeam(team), HttpStatus.CREATED);
    }

}
