package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
@Service
public interface MatchService {
    //business logic
    Match createMatch(Match match);

    Optional<Match> findMatchById(Integer id);

    List<Match> findAllMatches();

    Match updateMatch(Match match);

    void deleteMatch(Integer id);

    void updateMatchResult(Integer matchId, String result);

    void updateMatchStatus(Integer matchId, String status);
}
