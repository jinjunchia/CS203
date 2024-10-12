package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import org.springframework.stereotype.Service;

@Service
public interface EloService {

    /**
     * updates the ELO ratings of two players based on the outcome of a match.
     * @param player1 The first player involved in the match.
     * @param player2 The second player involved in the match.
     * @param match The match that was played.
     */
    void updateEloRatings(Player player1, Player player2, Match match);


    // additional method signatures for other ELO-related operations, if needed
}