package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
}