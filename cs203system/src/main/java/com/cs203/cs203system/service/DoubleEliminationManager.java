package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

public interface DoubleEliminationManager {

    Tournament initializeDoubleElimination(Tournament tournament);

    Tournament receiveMatchResult(Match match);

    Player determineWinner(Tournament tournament);
}
