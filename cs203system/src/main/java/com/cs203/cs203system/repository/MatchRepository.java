package com.cs203.cs203system.repository;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository

public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentAndStatus(Tournament tournament, MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.tournament = :tournament AND m.bracket = :bracket AND m.status = :status")
    List<Match> findByTournamentAndBracketAndStatus(@Param("tournament") Tournament tournament,
                                                    @Param("bracket") Match.Bracket bracket,
                                                    @Param("status") MatchStatus status);

}