package com.cs203.cs203system.service;


import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlayerRankingService {
    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public List<Player> getRankedPlayers(){
        //fetch all players
        List<Player> players = playerRepository.findAll();
        players.sort(Comparator.comparingDouble(Player::getEloRating)
                .thenComparingInt(Player::getWins)
                .thenComparingInt(Player::getLosses)
                .reversed());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRanking(i + 1);  // Ranking is 1-based
        }

        return players;
    }
}
