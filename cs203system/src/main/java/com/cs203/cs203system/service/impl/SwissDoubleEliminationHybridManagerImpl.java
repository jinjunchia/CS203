package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.SwissDoubleEliminationHybridManager;
import com.cs203.cs203system.service.SwissRoundManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwissDoubleEliminationHybridManagerImpl implements SwissDoubleEliminationHybridManager {
    private final SwissRoundManager swissRoundManager;

    private final DoubleEliminationManager doubleEliminationManager;
    private final TournamentRepository tournamentRepository;

    @Autowired
    public SwissDoubleEliminationHybridManagerImpl(SwissRoundManager swissRoundManager, DoubleEliminationManager doubleEliminationManager, TournamentRepository tournamentRepository) {
        this.swissRoundManager = swissRoundManager;
        this.doubleEliminationManager = doubleEliminationManager;
        this.tournamentRepository = tournamentRepository;
    }

    // This simply run the Swiss tournament first
    @Override
    public Tournament initializeHybrid(Tournament tournament) {
        return swissRoundManager.initializeSwiss(tournament);
    }

    // It will move all the results from Swiss to Double elimination until the swiss part has been completed
    // then it will move the top half to Double Elimination (winner bracket)
    // 1) it will check if its still running with SWISS
    @Override
    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = match.getTournament();

        // if isOnSecondFormat is false
//        if (!tournament.getIsOnSecondFormat()) {
//            // if isOnSecondFormat
//            if (tournament.getStatus() == TournamentStatus.COMPLETED) {
//                return swissRoundManager.receiveMatchResult(match);
//            } else {
//                tournament.getWinnersBracket() =
//                return doubleEliminationManager.receiveMatchResult(match);
//            }
//        }

        return null;
    }

    // It will use the Double Elimination method to find the winner
    @Override
    public Player determineWinner(Tournament tournament) {
        return doubleEliminationManager.determineWinner(tournament);
    }
}
