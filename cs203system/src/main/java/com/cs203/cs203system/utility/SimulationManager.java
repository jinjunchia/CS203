package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TeamRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SimulationManager {

    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentManager tournamentManager;

    // Method to setup and run a dummy simulation
    public void setupAndRunDummySimulation() {
        // Step 1: Create and save a dummy tournament
        Tournament tournament = createDummyTournament();
        tournamentRepository.save(tournament);
        logger.info("Dummy tournament created: {}", tournament);

        // Step 2: Create and save 32 dummy teams
        List<Team> teams = createDummyTeams(32, tournament);
        teamRepository.saveAll(teams);
        logger.info("Dummy teams created and saved: {}", teams);

        // Step 3: Run the tournament manager with the dummy tournament
        tournamentManager.startTournament(tournament);
        logger.info("Tournament simulation started.");

        logger.info("Tournament simulation completed successfully.");
    }

    // Helper method to create a dummy tournament
    private Tournament createDummyTournament() {
        Tournament tournament = new Tournament();
        tournament.setName("32 Team Tournament");
        tournament.setStatus(Tournament.Status.ONGOING); // Assuming you have a status field
        // Set other tournament fields as needed
        return tournament;
    }

    // Helper method to create dummy teams
    private List<Team> createDummyTeams(int count, Tournament tournament) {
        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Team team = new Team();
            team.setName("Team " + i);
            team.setEloRating(1500); // Default ELO rating or any other initial value
            team.setTournament(tournament);
            team.setStatus(Team.Status.QUALIFIED); // Initial status
            teams.add(team);
        }
        return teams;
    }
}

