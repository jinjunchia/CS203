package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Tournament;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TournamentService {
    Tournament createTournament(Tournament tournament);

    Optional<Tournament> findTournamentById(Integer id);

    List<Tournament> findAllTournaments();

    Tournament updateTournament(Tournament tournament);

    void deleteTournamentById(Integer id);

    void addTeamToTournament(Integer tournamentId, Integer teamId);

    void removeTeamFromTournament(Integer tournamentId, Integer teamId);
}