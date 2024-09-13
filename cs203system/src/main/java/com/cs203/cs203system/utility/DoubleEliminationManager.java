package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;

import java.util.List;

public interface DoubleEliminationManager {

    // initialize the Double Elimination phase with top players
    void initializeDoubleElimination(Tournament tournament, List<Player> players);

    // create matches for the current round
    List<Match> createMatches(Tournament tournament, List<Player> players);

    // update standings based on match results
    void updateStandings(Tournament tournament);

    // check if the Double Elimination phase is complete
    boolean isDoubleEliminationComplete(Tournament tournament);

    // determine the winner of the Double Elimination phase
    Player determineWinner(Tournament tournament);
}
