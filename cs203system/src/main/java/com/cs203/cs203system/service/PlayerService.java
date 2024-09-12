package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import com.cs203.cs203system.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface PlayerService {
    Player createPlayer(Player player);

    List<Player> findAllPlayers();

    Optional<Player> findPlayerById(Integer id);

    Player updatePlayer(Integer id, PlayerUpdateRequest player);

    void deletePlayer(Integer id);
}