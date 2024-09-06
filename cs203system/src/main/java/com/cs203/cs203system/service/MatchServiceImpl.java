package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class MatchServiceImpl implements MatchService{
    @Autowired
    private MatchRepository matchRepository;

    @Override
    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public Optional<Match> findMatchById(Integer id) {
        return matchRepository.findById(id);
    }

    @Override
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Override
    public Match updateMatch(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public void deleteMatch(Integer id) {
        matchRepository.deleteById(id);
    }

    @Override
    public void updateMatchResult(Integer id, String result) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        match.setResult(result);
        matchRepository.save(match);
    }

    @Override
    public void updateMatchStatus(Integer matchId, String status) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        match.setStatus(status);
        matchRepository.save(match);
    }

}
