package com.cs203.cs203system.repository;

import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTournaments_Id(Long tournamentId);

    List<Player> findAllByTournamentsAndStatus(Tournament tournament, PlayerStatus playerStatus);

}