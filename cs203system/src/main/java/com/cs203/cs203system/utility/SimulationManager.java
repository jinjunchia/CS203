package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.TournamentFormat;

public interface SimulationManager {
    // Run a simulation of the tournament with the given format
    void runSimulation(TournamentFormat format);
}