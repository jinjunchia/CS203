//package com.cs203.cs203system.controller;
//
//import com.cs203.cs203system.model.Tournament;
//import com.cs203.cs203system.service.TournamentService;
//import com.cs203.cs203system.service.TournamentServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/tournament")
//public class TournamentController {
//
//    private final TournamentService tournamentService;
//
//    @Autowired
//    public TournamentController (TournamentService tournamentService) {
//        this.tournamentService = tournamentService;
//    }
//
//    @GetMapping
//    public ReponseEntity<List<TournamentResponseDTO>> findAllTournaments() {
//        return new ResponseEntity<List<TournamentResponseDTO>>(tournamentService.findAllTournaments(), HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<TournamentResponseDTO> findTournamentById(@PathVariable Long id) {
//        Optional<Tournament> tournament = tournamentService.findTournamentById(id);
//        return new ResponseEntity<TournamentResponseDTO>(findTournamentById(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<TournamentResponseDTO> createTournament() {
//        Optional<Tournament>
//    }
//
//}
