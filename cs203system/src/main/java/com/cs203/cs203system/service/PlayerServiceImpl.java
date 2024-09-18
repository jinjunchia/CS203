package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cs203.cs203system.enums.UserType;

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
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Optional<Player> findPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player createPlayer(String username, String email, double eloRating) {
        Player player = new Player();
        player.setUsername(username);
        player.setEmail(email);
        player.setEloRating(eloRating);
        player.setUserType(UserType.PLAYER); // Correctly setting the user type
        return playerRepository.save(player);
    }

    @Override
    @Transactional
    public Player updatePlayer(Long id, PlayerUpdateRequest playerRequest){
        return playerRepository.findById(id)
                .map(player -> {
                    playerRequest.getName().ifPresent(player::setName);
                    //update the fields to the repo
                    return playerRepository.save(player);
                })
                .orElseThrow(() -> new RuntimeException("Player not found with Id "+ id));
    }


    @Override
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}