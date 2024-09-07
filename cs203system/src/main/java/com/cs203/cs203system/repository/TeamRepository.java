package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cs203.cs203system.model.Tournament;

import java.util.*;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    List<Team> findByTournament(Tournament tournament);
    List<Team> findByTournamentOrderByEloRatingDesc(Tournament tournament);
}