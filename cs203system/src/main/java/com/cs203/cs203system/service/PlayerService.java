package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerService {
    Player createPlayer(Player player);

    List<Player> findAllPlayers();

    Optional<Player> findPlayerById(Integer id);

    Player updatePlayer(Player player);

    void deletePlayer(Integer id);
}