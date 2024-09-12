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

    Optional<Tournament> findTournamentById(Integer id);

    List<Tournament> findAllTournaments();

    Tournament updateTournament(Integer id, TournamentUpdateRequest tournament);

    void deleteTournamentById(Integer id);

    String addPlayerToTournament(Integer tournamentId, Integer playerId);

    List<Player> getPlayersForTournament(Tournament tournament);

}