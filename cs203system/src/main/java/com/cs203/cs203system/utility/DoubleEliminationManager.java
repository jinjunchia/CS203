package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.enums.RoundType;
import jakarta.transaction.Transactional;

import java.util.List;

public interface DoubleEliminationManager {

    // Initialize the Double Elimination phase by placing players in the upper bracket and setting up the first round.
    void initializeDoubleElimination(Tournament tournament, List<Player> players);

    // Initialize a new round in the tournament, automatically assigning the correct round number based on the type.
    @Transactional
    Round initializeNewRound(Tournament tournament, RoundType roundType);

    // Initialize a specific round with a given round number and type.
    @Transactional
    Round initializeRound(Tournament tournament, int roundNumber, RoundType roundType);

    // Create and schedule matches for a given round, ensuring players are paired correctly.
    @Transactional
    List<Match> createMatches(Tournament tournament, List<Player> players, Round round);

    // Update the standings based on the results of completed matches, moving players between brackets and eliminating as necessary.
    void updateStandings(Tournament tournament);

    // Check if the Double Elimination phase of the tournament is complete, determining if a final match is needed.
    boolean isDoubleEliminationComplete(Tournament tournament);

    // Determine the winner of the Double Elimination phase based on remaining active players.
    Player determineWinner(Tournament tournament);
}
