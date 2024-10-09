package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

public interface TournamentManager {
//    Tournament initializeTournament(TournamentCreateDto tournamentCreateDto);

    Tournament initializeTournament(TournamentCreateDto tournamentCreateDto);

    void progressTournament(Tournament tournament);

    boolean isTournamentComplete(Tournament tournament);

    Player determineWinner(Tournament tournament);

    void setTournamentDetails(Tournament tournament, TournamentCreateDto tournamentCreateDto);

    void startTournament(Tournament tournament);

    void completeTournament(Tournament tournament);

}