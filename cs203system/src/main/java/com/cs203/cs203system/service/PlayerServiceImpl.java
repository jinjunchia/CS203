package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Optional<Player> findPlayerById(Integer id) {
        return playerRepository.findById(id);
    }

    @Override
    @Transactional
    public Player updatePlayer(Integer id, PlayerUpdateRequest updateRequest) {

        Optional<Player> existingPlayer = this.findPlayerById(id);

        if (existingPlayer.isEmpty()) {
            return null;
        }

        Player player = existingPlayer.get();

        updateRequest.getName().ifPresent(player::setName);
        return playerRepository.save(player);
    }

    @Override
    public void deletePlayer(Integer id) {
        playerRepository.deleteById(id);
    }
}