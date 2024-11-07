package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;

import java.util.List;

public interface MatchService {
    Match findMatchById(Long id);

    List<Match> findAllMatches();

    List<Match> findAllMatchesByTournamentId(Long tournamentId);
    void updateAndSaveMatchStats(Long matchId, Integer punchesPlayer1, Integer punchesPlayer2,
                                 Integer dodgesPlayer1, Integer dodgesPlayer2, boolean koByPlayer1, boolean koByPlayer2);
}
