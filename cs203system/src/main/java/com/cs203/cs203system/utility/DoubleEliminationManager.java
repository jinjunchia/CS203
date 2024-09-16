package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.enums.RoundType;
import java.util.List;

public interface DoubleEliminationManager {

    // Initialize the Double Elimination phase with top players
    void initializeDoubleElimination(Tournament tournament, List<Player> players);

    // Create matches for a specific round
    List<Match> createMatches(Tournament tournament, List<Player> players, Round round);

    // Update standings based on match results
    void updateStandings(Tournament tournament);

    // Check if the Double Elimination phase is complete
    boolean isDoubleEliminationComplete(Tournament tournament);

    // Determine the winner of the Double Elimination phase
    Player determineWinner(Tournament tournament);

    // Initialize a new round
    Round initializeRound(Tournament tournament, int roundNumber, RoundType roundType);
}
