package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournamentId(Long tournamentId);

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :startDate AND :endDate")
    List<Match> findMatchesWithinDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("select m from Match m where m.player2.id = ?1 or m.player1.id = ?1")
    List<Match> findByPlayerId(Long player1Id);


}