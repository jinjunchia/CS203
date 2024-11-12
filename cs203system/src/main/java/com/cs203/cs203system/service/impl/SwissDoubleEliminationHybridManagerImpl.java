package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.SwissDoubleEliminationHybridManager;
import com.cs203.cs203system.service.SwissRoundManager;
import com.cs203.cs203system.service.TournamentFormatManager;
import com.cs203.cs203system.utility.SwissRoundUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwissDoubleEliminationHybridManagerImpl implements TournamentFormatManager {

    private final SwissRoundManagerImpl swissRoundManager;
    private final DoubleEliminationManagerImpl doubleEliminationManager;
    private final TournamentRepository tournamentRepository;
    @Autowired
    public SwissDoubleEliminationHybridManagerImpl(SwissRoundManagerImpl swissRoundManager,
                                                   DoubleEliminationManagerImpl doubleEliminationManager,
                                                   TournamentRepository tournamentRepository) {
        this.swissRoundManager = swissRoundManager;
        this.doubleEliminationManager = doubleEliminationManager;
        this.tournamentRepository = tournamentRepository;
    }

    // This simply run the Swiss tournament first
    @Override
    public Tournament initializeTournament(Tournament tournament) {
        return swissRoundManager.initializeTournament(tournament);
    }

    // It will move all the results from Swiss to Double elimination until the swiss part has been completed
    // then it will move the top half to Double Elimination (winner bracket)
    // 1) it will check if its still running with SWISS
    @Override
    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = match.getTournament();

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
                return doubleEliminationManager.initializeTournament(updatedTournament);
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
