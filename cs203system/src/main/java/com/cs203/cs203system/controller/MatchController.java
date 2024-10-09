package com.cs203.cs203system.controller;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.service.MatchService;
import com.cs203.cs203system.service.MatchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAllMatches() {
        return new ResponseEntity<List<MatchResponseDTO>>(matchService.findAllMatches(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> findMatchById(@PathVariable Long id) {
        Optional<Match> match =
        return new ResponseEntity<>(matchService.findMatchById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MatchResponseDTO> createMatch() {

    }
}
