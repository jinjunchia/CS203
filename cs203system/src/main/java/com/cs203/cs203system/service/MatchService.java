package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;

import java.util.List;

public interface MatchService {
    Match findMatchById(Long id);

    List<Match> findAllMatches();

    List<Match> findAllMatchesByTournamentId(Long tournamentId);
}
