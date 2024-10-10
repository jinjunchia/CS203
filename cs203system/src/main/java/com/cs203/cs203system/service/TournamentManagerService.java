package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentManagerService {

    Optional<Tournament> findTournamentById(Long id);

    List<Tournament> findAllTournaments();

    void deleteTournamentById(Long id);

    Tournament createTournament(Tournament tournament);

    Tournament updatePlayersToTournament(Long tournamentId, List<Long> playerIds);

    Tournament startTournament(Long tournamentId);

    Tournament inputResult(Match match);

    Player determineWinner(Long tournamentId);


}