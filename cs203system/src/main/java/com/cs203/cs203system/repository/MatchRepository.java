package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface MatchRepository extends JpaRepository<Match, Integer> {
}