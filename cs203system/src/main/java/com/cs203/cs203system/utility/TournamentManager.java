package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class TournamentManager {
    private static final Logger logger = LoggerFactory.getLogger(TournamentManager.class);


    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final SwissRoundManager swissRoundManager;
    private final DoubleEliminationManager doubleEliminationManager;

    @Autowired
    public TournamentManager(TournamentRepository tournamentRepository, MatchRepository matchRepository,
                             PlayerRepository playerRepository, SwissRoundManager swissRoundManager,
                             DoubleEliminationManager doubleEliminationManager) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.swissRoundManager = swissRoundManager;
        this.doubleEliminationManager = doubleEliminationManager;
    }

    // Starts the entire tournament process
    @Transactional
    public void startTournament(Tournament tournament) {
        logger.info("Starting the tournament: {}", tournament.getName());

        // run Swiss Rounds
        logger.info("Running Swiss Rounds for the tournament: {}", tournament.getName());
        swissRoundManager.startSwissRounds(tournament);

        // get the top half teams that advanced from Swiss rounds
        List<Player> advancingPlayers = playerRepository.findByTournamentAndStatus(tournament, Player.Status.QUALIFIED);
        logger.info("Teams advancing to Double Elimination: {}", advancingPlayers.size());

        // proceed to Double Elimination with the advanced teams
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
        logger.info("Starting Double Elimination for the tournament: {}", tournament.getName());
        doubleEliminationManager.startDoubleElimination(tournament);

        logger.info("Tournament {} completed.", tournament.getName());
    }
}


