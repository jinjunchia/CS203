package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository

public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentAndStatus(Tournament tournament, Match.Status status);
    @Query("SELECT m FROM Match m WHERE m.tournament = :tournament AND m.bracket = :bracket AND m.status = :status")
    List<Match> findByTournamentAndBracketAndStatus(@Param("tournament") Tournament tournament,
                                                    @Param("bracket") Match.Bracket bracket,
                                                    @Param("status") Match.Status status);
    @Query("SELECT m FROM Match m WHERE m.bracket = :bracket AND m.status = :status")
    List<Match> findByBracketAndStatus(@Param("bracket") Match.Bracket bracket,
                                       @Param("status") Match.Status status);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Match m " +
            "WHERE :team1 MEMBER OF m.teams AND :team2 MEMBER OF m.teams AND m.bracket = :bracket")
    boolean existsByTeamsContainingAndBracket(@Param("team1") Player player1,
                                              @Param("team2") Player player2,
                                              @Param("bracket") Match.Bracket bracket);
    boolean existsByTeamsContainingAndBracketAndTournamentAndStatus(Player player, Match.Bracket bracket, Tournament tournament, Match.Status status);

}