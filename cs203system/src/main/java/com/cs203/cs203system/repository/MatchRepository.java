package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository

public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentAndStatus(Tournament tournament, Match.Status status);
    @Query("SELECT COALESCE(MAX(m.roundNumber), 0) FROM Match m WHERE m.tournament = :tournament")
    int findMaxRoundByTournament(@Param("tournament") Tournament tournament);
}