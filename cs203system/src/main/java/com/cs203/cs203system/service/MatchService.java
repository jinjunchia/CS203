package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface MatchService {
    //business logic
    Match createMatch(Match match);

    Optional<Match> findMatchById(Long id);

    List<Match> findAllMatches();

    Match updateMatch(Match match);

    void deleteMatch(Long id);

    void updateMatchResult(Long matchId, String result);

    void updateMatchStatus(Long matchId, String status);
}
