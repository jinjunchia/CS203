package com.cs203.cs203system.utilities2;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;

public interface DoubleEliminationManager {

    Tournament initializeDoubleElimination(Tournament tournament);

    Tournament receiveMatchResult(Match match);
}
