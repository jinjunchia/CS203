package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

public interface TournamentManager {
    void initializeTournament(Tournament tournament);

    void progressTournament(Tournament tournament);

    boolean isTournamentComplete(Tournament tournament);

    Player determineWinner(Tournament tournament);

    void setTournamentDetails(Tournament tournament);

    void startTournament(Tournament tournament);

    void completeTournament(Tournament tournament);

}