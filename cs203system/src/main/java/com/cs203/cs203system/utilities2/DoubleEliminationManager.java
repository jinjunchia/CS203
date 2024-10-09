package com.cs203.cs203system.utilities2;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.model.Tournament;
import jakarta.transaction.Transactional;
import org.springframework.data.util.Pair;

import java.util.List;

public interface DoubleEliminationManager {

    // Initialize the Double Elimination phase by placing players in the upper bracket and setting up the first round.
    void initializeDoubleElimination(Tournament tournament, List<Player> players);

    // Initialize a new round in the tournament, automatically assigning the correct round number.
    Round initializeRound(Tournament tournament, List<Player> players);

    //Sending matches
    public List<Match> createBracketMatches(Tournament tournament, List<Player> players, Round round);

    // Update the standings based on the results of completed matches, moving players between brackets and eliminating as necessary.
    public List<Match> receiveResult(Tournament tournament, int roundNumber, boolean isFinal);


    // Check if the Double Elimination phase of the tournament is complete, determining if a final match is needed.
    boolean isDoubleEliminationComplete(Tournament tournament);

    // Determine the winner of the Double Elimination phase based on remaining active players.
    Player determineTournamentWinner(List<Player> players);

    Player determineWinner(Tournament tournament);

    // Process the next round if the tournament is not yet complete.
    void processNextRound(Tournament tournament);

    List<Pair<Player, Player>> pairPlayers(List<Player> players);

    int getNextRoundNumber(Tournament tournament);

    Tournament createBracketMatches(Tournament tournament, Round round);
}
