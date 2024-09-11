package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    // Finds all teams participating in the given tournament
    List<Player> findByTournament(Tournament tournament);

    // Finds all teams in a tournament ordered by their Elo rating in descending order
    List<Player> findByTournamentOrderByEloRatingDesc(Tournament tournament);

    // Finds all teams in a tournament ordered by their points in descending order
    List<Player> findByTournamentOrderByPointsDesc(Tournament tournament);

    // Finds teams by tournament and their status (e.g., QUALIFIED, ELIMINATED)
    List<Player> findByTournamentAndStatus(Tournament tournament, Player.Status status);

    // Finds teams by tournament and bracket type for more specific queries
    @Query("SELECT DISTINCT t FROM Player t JOIN t.matches m WHERE m.tournament = :tournament AND m.bracket = :bracket")
    List<Player> findByTournamentAndBracket(@Param("tournament") Tournament tournament, @Param("bracket") Match.Bracket bracket);
}