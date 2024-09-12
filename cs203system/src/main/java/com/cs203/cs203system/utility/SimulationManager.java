//package com.cs203.cs203system.utility;
//
//import com.cs203.cs203system.enums.TournamentFormat;
//import com.cs203.cs203system.model.Player;
//import com.cs203.cs203system.model.Tournament;
//import com.cs203.cs203system.repository.PlayerRepository;
//import com.cs203.cs203system.repository.TournamentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Component
//public class SimulationManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);
//
//    private final TournamentRepository tournamentRepository;
//
//    private final PlayerRepository playerRepository;
//
//    private final TournamentManager tournamentManager;
//
//    @Autowired
//    public SimulationManager(TournamentManager tournamentManager, PlayerRepository playerRepository, TournamentRepository tournamentRepository) {
//        this.tournamentManager = tournamentManager;
//        this.playerRepository = playerRepository;
//        this.tournamentRepository = tournamentRepository;
//    }
//
//    // Method to set up and run a dummy simulation
//    public void setupAndRunDummySimulation() {
//        try {
//            // Step 1: Create and save a dummy tournament
//            Tournament tournament = createDummyTournament();
//            tournament.setFormat(TournamentFormat.SWISS);  // Set initial format to SWISS
//            tournamentRepository.save(tournament);
//            logger.info("Dummy tournament created: {}", tournament);
//
//            // Step 2: Create and save 32 dummy players
//            List<Player> players = createDummyPlayers(32, tournament);
//            if (players == null || players.contains(null)) {
//                logger.error("Null players detected in the list of created players.");
//                return;
//            }
//            playerRepository.saveAll(players);
//            logger.info("Dummy players created and saved: {}", players);
//
//            // Step 3: Run the tournament manager with the dummy tournament
//            tournamentManager.startTournament(tournament);
//            logger.info("Tournament simulation started.");
//
//            logger.info("Tournament simulation completed successfully.");
//        } catch (Exception e) {
//            logger.error("Error occurred during simulation: {}", e.getMessage());
//        }
//    }
//
//    // Helper method to create a dummy tournament
//    private Tournament createDummyTournament() {
//        Tournament tournament = new Tournament();
//        tournament.setName("32 Player Tournament");
//        tournament.setStatus(Tournament.Status.ONGOING); // Assuming you have a status field
//        // Set other tournament fields as needed
//        return tournament;
//    }
//
//    // Helper method to create dummy players
//    private List<Player> createDummyPlayers(int count, Tournament tournament) {
//        List<Player> players = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            if (tournament == null) {
//                logger.error("Tournament is null, cannot assign it to players.");
//                return null;
//            }
//            Player player = new Player();
//            player.setName("Player " + i);
//            player.setEloRating(1500); // Default ELO rating or any other initial value
//            player.setTournament(tournament);
//            player.setStatus(Player.Status.QUALIFIED); // Initial status
//
//            if (player.getName() == null || player.getTournament() == null) {
//                logger.error("Invalid player data: {}", player);
//                continue;
//            }
//
//            players.add(player);
//        }
//        return players;
//    }
//}
//
//
