package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.dtos.players.PlayerUpdateRequest;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface PlayerService {
    PlayerWithOutStatsDto createPlayer(CreateUserRequest createUserRequest);

    List<PlayerWithOutStatsDto> findAllPlayers();

    Optional<PlayerWithOutStatsDto> findPlayerById(Long id);

    Player updatePlayer(Long id, PlayerUpdateRequest player);

    void deletePlayer(Long id);
}