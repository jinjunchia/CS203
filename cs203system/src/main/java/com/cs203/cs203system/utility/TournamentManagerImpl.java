package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class TournamentManagerImpl implements TournamentManager {
    private static final Logger logger = LoggerFactory.getLogger(TournamentManagerImpl.class);

    @Autowired
    private SwissRoundManager swissRoundManager;

    @Autowired
    private DoubleEliminationManager doubleEliminationManager;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    public List<Player> getPlayersForTournament(Tournament tournament) {
        return playerRepository.findByTournamentId(tournament.getId());
    }

    @Override
    @Transactional
    public void initializeTournament(Tournament tournament) {
        setTournamentDetails(tournament);
        startTournament(tournament);

        List<Player> players = getPlayersForTournament(tournament);
        switch (tournament.getFormat()) {
            case SWISS:
                logger.debug("Initializing Swiss tournament...");
                swissRoundManager.initializeRounds(tournament);
                break;
            case DOUBLE_ELIMINATION:
                logger.debug("Initializing Double Elimination tournament...");
                doubleEliminationManager.initializeDoubleElimination(tournament, players);
                break;
            case HYBRID:
                logger.debug("Initializing Hybrid tournament with Swiss rounds...");
                swissRoundManager.initializeRounds(tournament);
                break;
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }

        tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void progressTournament(Tournament tournament) {
        logger.debug("Progressing tournament: {} with format: {}", tournament.getName(), tournament.getFormat());

        // Check if the tournament is already complete
        if (isTournamentComplete(tournament)) {
            logger.debug("Tournament already complete. Skipping further progression.");
            return; // Exit if the tournament is already complete
        }

        // Handle progression based on the tournament format
        switch (tournament.getFormat()) {
            case SWISS:
                logger.debug("Progressing Swiss format...");
                if (!swissRoundManager.isSwissPhaseComplete(tournament)) {
                    // Check if any rounds can still be progressed or matches played
                    swissRoundManager.updateStandings(tournament); // Update standings if not complete
                } else if (tournament.getFormat() == TournamentFormat.HYBRID && !tournament.isDoubleEliminationStarted()) {
                    // Transition to Double Elimination
                    logger.debug("Transitioning to Double Elimination format...");
                    List<Player> topPlayers = swissRoundManager.getTopPlayers(tournament);
                    doubleEliminationManager.initializeDoubleElimination(tournament, topPlayers);
                    tournament.setDoubleEliminationStarted(true);
                    tournamentRepository.save(tournament);
                }
                break;

            case DOUBLE_ELIMINATION:
                logger.debug("Progressing Double Elimination format...");
                if (!doubleEliminationManager.isDoubleEliminationComplete(tournament)) {
                    doubleEliminationManager.updateStandings(tournament); // Update standings
                } else {
                    tournament.setStatus(TournamentStatus.COMPLETED);
                    logger.debug("Double Elimination complete. Tournament marked as completed.");
                    tournamentRepository.save(tournament);
                }
                break;

            case HYBRID:
                logger.debug("Handling Hybrid progression...");
                handleHybridProgression(tournament); // Utilize existing logic for hybrid handling
                break;

            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }

        tournamentRepository.save(tournament);
    }

    private void handleHybridProgression(Tournament tournament) {
        logger.debug("Handling Hybrid progression...");

        if (!swissRoundManager.isSwissPhaseComplete(tournament)) {
            logger.debug("Continuing Swiss rounds...");
            swissRoundManager.updateStandings(tournament);
        } else if (!tournament.isDoubleEliminationStarted()) {
            logger.debug("Transitioning to Double Elimination...");
            List<Player> topPlayers = swissRoundManager.getTopPlayers(tournament);
            doubleEliminationManager.initializeDoubleElimination(tournament, topPlayers);
            tournament.setDoubleEliminationStarted(true);
            tournamentRepository.save(tournament);
        } else {
            // Ensure Double Elimination is complete before proceeding
            if (!doubleEliminationManager.isDoubleEliminationComplete(tournament)) {
                logger.debug("Continuing Double Elimination rounds...");
                doubleEliminationManager.updateStandings(tournament);
            } else {
                logger.debug("Double Elimination is complete. No further progression needed.");
                tournament.setStatus(TournamentStatus.COMPLETED);
                tournamentRepository.save(tournament); // Mark tournament as complete
                return;
            }
        }
    }

    @Override
    public boolean isTournamentComplete(Tournament tournament) {
        switch (tournament.getFormat()) {
            case SWISS:
                return swissRoundManager.isSwissPhaseComplete(tournament);
            case DOUBLE_ELIMINATION:
                return doubleEliminationManager.isDoubleEliminationComplete(tournament);
            case HYBRID:
                // Ensure this checks both Swiss and Double Elimination completion properly
                return swissRoundManager.isSwissPhaseComplete(tournament) &&
                        doubleEliminationManager.isDoubleEliminationComplete(tournament);
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
    }

    @Override
    @Transactional
    public Player determineWinner(Tournament tournament) {
        if (isTournamentComplete(tournament)) {
            completeTournament(tournament);
            switch (tournament.getFormat()) {
                case SWISS:
                    logger.debug("Determining Swiss winner...");
                    return swissRoundManager.determineSwissWinner(tournament);
                case DOUBLE_ELIMINATION:
                    logger.debug("Determining Double Elimination winner...");
                    return doubleEliminationManager.determineWinner(tournament);
                case HYBRID:
                    logger.debug("Determining Hybrid winner from Double Elimination phase...");
                    return doubleEliminationManager.determineWinner(tournament); // In Hybrid, the winner is from Double Elimination phase
                default:
                    throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
            }
        }
        logger.debug("Tournament not complete; no winner determined.");
        return null;
    }

    @Override
    public void setTournamentDetails(Tournament tournament) {
        LocalDate startDate = LocalDate.now().plusDays(random.nextInt(10)); // Start in 0-9 days
        LocalDate endDate = startDate.plusDays(5 + random.nextInt(5)); // Duration of 5-9 days
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);

        List<String> locations = Arrays.asList("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");
        tournament.setLocation(locations.get(random.nextInt(locations.size())));

        tournament.setStatus(TournamentStatus.SCHEDULED);
//        TournamentFormat[] formats = TournamentFormat.values();
//        tournament.setFormat(formats[random.nextInt(formats.length)]);
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);

        tournamentRepository.save(tournament);
    }

    @Override
    public void startTournament(Tournament tournament) {
        tournament.setStatus(TournamentStatus.ONGOING);
        tournamentRepository.save(tournament);
    }

    @Override
    public void completeTournament(Tournament tournament) {
        tournament.setStatus(TournamentStatus.COMPLETED);
        tournamentRepository.save(tournament);
    }
}
