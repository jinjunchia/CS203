package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SimulationManagerImpl implements SimulationManager {

    @Autowired
    private TournamentManager tournamentManager;

    /**
     * Runs a simulation for a tournament with the specified format.
     * Creates 32 players with varying ELO ratings and runs the tournament to completion.
     *
     * @param format The format of the tournament (SWISS, DOUBLE_ELIMINATION, HYBRID)
     */
    @Override
    @Transactional
    public void runSimulation(TournamentFormat format) {
        // Create a tournament instance
        Tournament tournament = new Tournament();
        tournament.setName("Simulated Tournament");
        tournament.setFormat(format);
        tournament.setMinEloRating(800.0);
        tournament.setMaxEloRating(1200.0);

        // create 32 players with different ELO ratings
        List<Player> players = IntStream.range(1, 33).mapToObj(i -> {
            Player player = new Player();
            player.setName("Player " + i);
            player.setEloRating(800.0 + i * 10); // Assign different ELOs for diversity
            return player;
        }).collect(Collectors.toList());

        // Add players to the tournament
        tournament.setPlayers(new HashSet<>(players)); // Add players to the tournament

        // Initialize the tournament
        tournamentManager.initializeTournament(tournament);

        // Progress through the tournament until completion
        while (!tournamentManager.isTournamentComplete(tournament)) {
            tournamentManager.progressTournament(tournament);
        }

        // Determine and print the winner of the tournament
        Player winner = tournamentManager.determineWinner(tournament);
        System.out.println("Tournament Winner: " + (winner != null ? winner.getName() : "No winner"));
    }
}
