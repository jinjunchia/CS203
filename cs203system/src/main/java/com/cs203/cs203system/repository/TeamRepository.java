package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}