package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.SwissDoubleEliminationHybridManager;
import com.cs203.cs203system.service.SwissRoundManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwissDoubleEliminationHybridManagerImpl implements SwissDoubleEliminationHybridManager {
    private final SwissRoundManager swissRoundManager;

    private final DoubleEliminationManager doubleEliminationManager;

    @Autowired
    public SwissDoubleEliminationHybridManagerImpl(SwissRoundManager swissRoundManager, DoubleEliminationManager doubleEliminationManager) {
        this.swissRoundManager = swissRoundManager;
        this.doubleEliminationManager = doubleEliminationManager;
    }

    // This simply run the Swiss tournament first
    @Override
    public Tournament initializeHybrid(Tournament tournament) {
        return swissRoundManager.initializeSwiss(tournament);
    }

    // It will move all the results to Swiss until the swiss part has been completed
    // then it will move it to Double Elimination
    @Override
    public Tournament receiveMatchResult(Match match) {

        return null;
    }

    // It will use the Double Elimination method to find the winner
    @Override
    public Player determineWinner(Tournament tournament) {
        return doubleEliminationManager.determineWinner(tournament);
    }
}
