package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.springframework.data.util.Pair;

import java.util.List;

public interface SwissRoundManager {

    /**
     * Initializes the Swiss rounds for the given tournament.
     *
     * @param tournament The tournament to initialize rounds for.
     */
    void initializeRounds(Tournament tournament);

    /**
     * Pairs players for the current round based on their standings.
     *
     * @param players List of players participating in the round.
     * @return A list of player pairs for the round.
     */
    List<Pair<Player, Player>> pairPlayers(List<Player> players);

    /**
     * Updates the standings of players after a round.
     *
     * @param tournament The tournament whose standings need to be updated.
     */
    void updateStandings(Tournament tournament);

    /**
     * Checks if the Swiss phase is complete and if it is time to transition to Double Elimination.
     *
     * @param tournament The tournament to check.
     * @return true if ready to transition; false otherwise.
     */
    boolean isSwissPhaseComplete(Tournament tournament);

    List<Player> getTopPlayers(Tournament tournament);

    Player determineSwissWinner(Tournament tournament);
}

