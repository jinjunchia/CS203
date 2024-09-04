package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PlayerRepository extends JpaRepository<Player, Integer> {
}
