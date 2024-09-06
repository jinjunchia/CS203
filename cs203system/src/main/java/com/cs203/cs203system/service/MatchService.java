package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MatchService {
    Match createMatch(Match match);

    Optional<Match> findMatchById(Integer id);

    List<Match> findAllMatches();

    Match updateMatch(Match match);

    void deleteMatch(Integer id);

    void updateMatchResult(Integer matchId, String result);

    void updateMatchStatus(Integer matchId, String status);
}
