package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.SwissDoubleEliminationHybridManager;
import com.cs203.cs203system.service.SwissRoundManager;
import com.cs203.cs203system.utility.SwissRoundUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

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
        //Base case to ensure that tournament does not run anymore
//        if (!tournament.getIsOnSecondFormat())
//        {
//            returnTournament = swissRoundManager.receiveMatchResult(match);
//        }
//        if (!tournament.getIsOnSecondFormat() && tournament.getStatus() == TournamentStatus.COMPLETED) {
//            tournament.setIsOnSecondFormat(Boolean.TRUE);
//            tournament.setStatus(TournamentStatus.ONGOING);
//            //Get players out
//
//            List<Player> topPlayers = SwissRoundUtils.getTopPlayers(tournament, tournament.getPlayers().size() / 2);
//            //Put them into double elimination
//            tournament.setPlayers(topPlayers);
//            doubleEliminationManager.initializeDoubleElimination(tournament);
//            return doubleEliminationManager.receiveMatchResult(match);
//        }
//        if (!tournament.getIsOnSecondFormat() && tournament.getStatus() == TournamentStatus.ONGOING) {
//            return doubleEliminationManager.receiveMatchResult(match);
//        }
//        return returnTournament;

        // If Completed and True (On Second format): means the entire tournament has been completed, it will do NOTHING
        if (tournament.getStatus() == TournamentStatus.COMPLETED && tournament.getIsOnSecondFormat()) {
            return tournament;
        }

        // If Ongoing and False (On first format)
        if (tournament.getStatus() == TournamentStatus.ONGOING && !tournament.getIsOnSecondFormat()) {
            // Give to swiss and return a tournament
            Tournament updatedTournament = swissRoundManager.receiveMatchResult(match);

            // Check if Swiss has changed the state of the tournament from ongoing to completed?
            // If yes, then get top players and DE needs to start
            if (updatedTournament.getStatus() == TournamentStatus.COMPLETED) {
                updatedTournament.setStatus(TournamentStatus.ONGOING); // Need to change it back as tournament is still ongoing
                updatedTournament.setPlayers(SwissRoundUtils.getTopPlayers(updatedTournament, updatedTournament.getPlayers().size() / 2));
                tournament.setIsOnSecondFormat(true);
                return doubleEliminationManager.initializeDoubleElimination(updatedTournament);

            }

            return updatedTournament;
        }

        // If Ongoing and True (On second format): means we will continue with DE
        // Note that while the code might seem that it will reach this case with it can also be Completed and False,
        // this would be impossible as the moment that happens the next if statement above will catch it
        return doubleEliminationManager.receiveMatchResult(match);
    }

    // It will use the Double Elimination method to find the winner
    @Override
    public Player determineWinner(Tournament tournament) {
        return doubleEliminationManager.determineWinner(tournament);
    }
}
