package com.cs203.cs203system.repository;

import com.cs203.cs203system.enums.RoundType;
import com.cs203.cs203system.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    // Find all rounds associated with a specific tournament
    List<Round> findByMatches_Tournament_Id(Long tournamentId);

    Optional<Round> findTopByMatches_Tournament_IdAndRoundTypeOrderByRoundNumberDesc(Long tournamentId, RoundType roundType);

}
