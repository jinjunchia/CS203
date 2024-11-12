package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.TournamentFormatManager;
import com.cs203.cs203system.utility.SwissRoundUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing a hybrid tournament format
 * that combines Swiss and Double Elimination rounds.
 *
 * This service first manages a Swiss-style tournament until completion.
 * Then, it transitions the top players to a Double Elimination format.
 */
@Service
public class SwissDoubleEliminationHybridManagerImpl implements TournamentFormatManager {

    private final SwissRoundManagerImpl swissRoundManager;
    private final DoubleEliminationManagerImpl doubleEliminationManager;
    private final TournamentRepository tournamentRepository;

    /**
     * Constructs a SwissDoubleEliminationHybridManagerImpl with the necessary dependencies.
     *
     * @param swissRoundManager the Swiss round manager to handle Swiss-style tournament logic
     * @param doubleEliminationManager the Double Elimination manager to handle double elimination logic
     * @param tournamentRepository the tournament repository for accessing tournament data
     */
    @Autowired
    public SwissDoubleEliminationHybridManagerImpl(SwissRoundManagerImpl swissRoundManager,
                                                   DoubleEliminationManagerImpl doubleEliminationManager,
                                                   TournamentRepository tournamentRepository) {
        this.swissRoundManager = swissRoundManager;
        this.doubleEliminationManager = doubleEliminationManager;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * Initializes the tournament in the Swiss format.
     *
     * This method sets up the tournament using Swiss-style rounds.
     *
     * @param tournament the tournament to initialize
     * @return the initialized tournament with Swiss format
     */
    @Override
    public Tournament initializeTournament(Tournament tournament) {
        return swissRoundManager.initializeTournament(tournament);
    }

    /**
     * Processes a match result for the hybrid tournament.
     *
     * This method processes match results in two phases:
     * 1. Swiss phase: While the tournament is in the Swiss phase, it processes the match
     *    using the SwissRoundManager. If the Swiss phase completes, it transitions the
     *    top half of players to the Double Elimination phase.
     * 2. Double Elimination phase: After the Swiss phase, it continues processing matches
     *    using the DoubleEliminationManager until the tournament is fully completed.
     *
     * @param match the match result to process
     * @return the updated tournament after processing the match result
     */
    @Override
    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = match.getTournament();

        // If the tournament is completed in the double elimination phase, do nothing
        if (tournament.getStatus() == TournamentStatus.COMPLETED && tournament.getIsOnSecondFormat()) {
            return tournament;
        }

        // Process match in Swiss phase
        if (tournament.getStatus() == TournamentStatus.ONGOING && !tournament.getIsOnSecondFormat()) {
            Tournament updatedTournament = swissRoundManager.receiveMatchResult(match);

            // Transition to Double Elimination phase if Swiss phase is completed
            if (updatedTournament.getStatus() == TournamentStatus.COMPLETED) {
                updatedTournament.setStatus(TournamentStatus.ONGOING); // Mark as ongoing since DE phase starts
                updatedTournament.setPlayers(SwissRoundUtils.getTopPlayers(updatedTournament, updatedTournament.getPlayers().size() / 2));
                tournament.setIsOnSecondFormat(true);
                return doubleEliminationManager.initializeTournament(updatedTournament);
            }
            return updatedTournament;
        }

        // Process match in Double Elimination phase
        return doubleEliminationManager.receiveMatchResult(match);
    }

    /**
     * Determines the winner of the tournament.
     *
     * Uses the Double Elimination manager to determine the final winner.
     *
     * @param tournament the tournament for which the winner is to be determined
     * @return the player who is the winner of the tournament
     */
    @Override
    public Player determineWinner(Tournament tournament) {
        return doubleEliminationManager.determineWinner(tournament);
    }
}

