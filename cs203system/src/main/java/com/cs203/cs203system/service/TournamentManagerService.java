package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentManagerService {

//    Tournament createTournament create
//
//    boolean isTournamentComplete(Tournament tournament);
//
//    void setTournamentDetails(Tournament tournament, TournamentRequestDTO tournamentRequestDTO);
//
//    void startTournament(Tournament tournament);
//
//    void completeTournament(Tournament tournament);

    Optional<Tournament> findTournamentById(Long id);

    List<Tournament> findAllTournaments();

    void deleteTournamentById(Long id);

    Tournament createTournament(Tournament tournament, List<Long> playerIds);

}