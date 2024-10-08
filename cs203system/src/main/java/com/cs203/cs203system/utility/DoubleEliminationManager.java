package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Round;
import jakarta.transaction.Transactional;

import java.util.List;

public interface DoubleEliminationManager {

    // Initialize the Double Elimination phase by placing players in the upper bracket and setting up the first round.
    void initializeDoubleElimination(Tournament tournament, List<Player> players);

    // Initialize a new round in the tournament, automatically assigning the correct round number.
    void initializeRound(Tournament tournament, List<Player> players);

    // Create and schedule matches for a given round, ensuring players are paired correctly.
    @Transactional
    void createMatches(Tournament tournament, List<Player> players, Round round);

    public List<Match> createBracketMatches (Tournament tournament, List<Player> players, Round round);

    // Update the standings based on the results of completed matches, moving players between brackets and eliminating as necessary.
    public void playMatches (List<Match> Match, int roundNumber, boolean isFinal);

//    public void receiveResult (List<Match> Match, int roundNumber, boolean isFinal);

    // Check if the Double Elimination phase of the tournament is complete, determining if a final match is needed.
    boolean isDoubleEliminationComplete(Tournament tournament);

    // Determine the winner of the Double Elimination phase based on remaining active players.
    Player determineTournamentWinner(List<Player> players);
    Player determineWinner(Tournament tournament);


    // Process the next round if the tournament is not yet complete.
    void processNextRound(Tournament tournament);
}
