package com.cs203.cs203system.repository;

import com.cs203.cs203system.enums.RoundType;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Integer> {

    // Find all rounds associated with a specific tournament
    List<Round> findByTournament(Tournament tournament);

    Optional<Round> findTopByTournamentAndRoundTypeOrderByRoundNumberDesc(Tournament tournament, RoundType roundType);
}
