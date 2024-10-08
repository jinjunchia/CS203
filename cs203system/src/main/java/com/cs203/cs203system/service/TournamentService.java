package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.TournamentUpdateRequest;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TournamentService {
    Tournament createTournament(Tournament tournament);

    Optional<Tournament> findTournamentById(Long id);

    List<Tournament> findAllTournaments();

    Tournament updateTournament(Long id, TournamentUpdateRequest tournament);

    void deleteTournamentById(Long id);

    String addPlayerToTournament(Long tournamentId, List<Long> playerId);

//    String addPlayerToTournament(List<Long> playerId);

    List<Player> getPlayersForTournament(Tournament tournament);

}