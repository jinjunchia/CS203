package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {
}