//package com.cs203.cs203system.utility;
//
//import com.cs203.cs203system.dtos.TournamentCreateDto;
//import com.cs203.cs203system.enums.TournamentFormat;
//import com.cs203.cs203system.enums.TournamentStatus;
//import com.cs203.cs203system.model.Player;
//import com.cs203.cs203system.model.Tournament;
//import com.cs203.cs203system.repository.PlayerRepository;
//import com.cs203.cs203system.repository.TournamentRepository;
//import com.cs203.cs203system.service.TournamentService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//@Service
//public class TournamentManagerImpl implements TournamentManager {
//    private static final Logger logger = LoggerFactory.getLogger(TournamentManagerImpl.class);
//
//    @Autowired
//    private SwissRoundManager swissRoundManager;
//
//    @Autowired
//    private DoubleEliminationManager doubleEliminationManager;
//
//    @Autowired
//    private TournamentRepository tournamentRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private TournamentService tournamentService;
//
//    private Tournament tournament;
//
//
//    private final Random random = new Random();
//
//    public List<Player> getPlayersForTournament(Tournament tournament) {
//        return playerRepository.findByTournaments_Id(tournament.getId());
//    }
//
//    @Override
//    @Transactional
//    public Tournament initializeTournament(Tournament tournament) {
//        setTournamentDetails(tournament);
//        startTournament(tournament);
//
//        List<Player> players = getPlayersForTournament(tournament);
//        Tournament output = tournamentRepository.save(tournament);
//        switch (tournament.getFormat()) {
//            case SWISS:
//                logger.debug("Initializing Swiss tournament...");
//                swissRoundManager.initializeRounds(tournament);
//                break;
//            case DOUBLE_ELIMINATION:
//                logger.debug("Initializing Double Elimination tournament...");
//                doubleEliminationManager.initializeDoubleElimination(tournament, players);
//                break;
//            case HYBRID:
//                logger.debug("Initializing Hybrid tournament with Swiss rounds...");
//                swissRoundManager.initializeRounds(tournament);
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
//        }
//
//        return output;
//    }
//
//    @Override
//    @Transactional
//    public Tournament initializeTournament(TournamentCreateDto tournamentCreateDto) {
//        Tournament tournament = new Tournament();
//        tournamentService.addPlayerToTournament(tournament.getId(),tournamentCreateDto.getPlayerIds());
//        setTournamentDetails(tournament);
//        startTournament(tournament);
//
//        List<Player> players = getPlayersForTournament(tournament);
//        Tournament output = tournamentRepository.save(tournament);
//        switch (tournament.getFormat()) {
//            case SWISS:
//                logger.debug("Initializing Swiss tournament...");
//                swissRoundManager.initializeRounds(tournament);
//                break;
//            case DOUBLE_ELIMINATION:
//                logger.debug("Initializing Double Elimination tournament...");
//                doubleEliminationManager.initializeDoubleElimination(tournament, players);
//                break;
//            case HYBRID:
//                logger.debug("Initializing Hybrid tournament with Swiss rounds...");
//                swissRoundManager.initializeRounds(tournament);
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
//        }
//
//        return output;
//    }
//
//
//    @Override
//    @Transactional
//    public void progressTournament(Tournament tournament) {
//        logger.debug("Progressing tournament: {} with format: {}", tournament.getName(), tournament.getFormat());
//
//        // Check if the tournament is already complete
//        if (isTournamentComplete(tournament)) {
//            logger.debug("Tournament already complete. Skipping further progression.");
//            return; // Exit if the tournament is already complete
//        }
//
//        // Handle progression based on the tournament format
//        switch (tournament.getFormat()) {
//            case SWISS:
//                logger.debug("Progressing Swiss format...");
//                if (!swissRoundManager.isSwissPhaseComplete(tournament)) {
//                    swissRoundManager.updateStandings(tournament); // Update standings if not complete
//                } else {
//                    // Transitioning to the next phase or marking the tournament as completed
//                    logger.debug("Swiss phase complete. Marking the tournament as complete.");
//                    tournament.setStatus(TournamentStatus.COMPLETED);
//                    tournamentRepository.save(tournament);
//                }
//                break;
//
//            case DOUBLE_ELIMINATION:
//                logger.debug("Progressing Double Elimination format...");
//                if (!doubleEliminationManager.isDoubleEliminationComplete(tournament)) {
//                    // Progress the double elimination
//                    logger.debug("Progressing Double Elimination...");
//                    // Uncomment the method to update standings for Double Elimination
//                    // doubleEliminationManager.updateStandings(tournament);
//                } else {
//                    logger.debug("Double Elimination complete. Marking the tournament as complete.");
//                    tournament.setStatus(TournamentStatus.COMPLETED);
//                    tournamentRepository.save(tournament);
//                }
//                break;
//
//            case HYBRID:
//                logger.debug("Handling Hybrid progression...");
//                handleHybridProgression(tournament); // Utilize existing logic for hybrid handling
//                break;
//
//            default:
//                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
//        }
//
//        tournamentRepository.save(tournament);
//    }
//
//    @Transactional
//    public void handleHybridProgression(Tournament tournament) {
//        logger.debug("Handling Hybrid progression...");
//
//        // Step 1: Progress Swiss rounds if not completed
//        if (!swissRoundManager.isSwissPhaseComplete(tournament)) {
//            logger.debug("Continuing Swiss rounds...");
//            swissRoundManager.updateStandings(tournament);
//        }
//        // Step 2: If Swiss is complete and Double Elimination has not started, transition
//        else if (!tournament.isDoubleEliminationStarted()) {
//
//            logger.debug("Swiss rounds are complete. Determining Swiss winner...");
//
//            // Determine the Swiss winner and log the result
//            Player swissWinner = swissRoundManager.determineSwissWinner(tournament);
//            if (swissWinner != null) {
//                logger.debug("Swiss winner is: {}", swissWinner.getName());
//            }
//            logger.debug("Swiss rounds are complete. Transitioning to Double Elimination...");
//            List<Player> topPlayers = swissRoundManager.getTopPlayers(tournament);
//            doubleEliminationManager.initializeDoubleElimination(tournament, topPlayers);
//
//            // Mark that Double Elimination has started to avoid re-transitioning
//            tournament.setDoubleEliminationStarted(true);
//            tournamentRepository.save(tournament);
//        }
//        // Step 3: If Double Elimination has started, progress through it
//        else {
//            if (!doubleEliminationManager.isDoubleEliminationComplete(tournament)) {
//                logger.debug("Progressing Double Elimination rounds...");
//                // Add logic here to progress the Double Elimination if needed.
//                // Uncomment this when you implement standings updates for Double Elimination
//                // doubleEliminationManager.updateStandings(tournament);
//            } else {
//                logger.debug("Double Elimination is complete. Marking tournament as complete.");
//                tournament.setStatus(TournamentStatus.COMPLETED);
//                tournamentRepository.save(tournament); // Mark tournament as complete
//            }
//        }
//    }
//
//    @Override
//    public boolean isTournamentComplete(Tournament tournament) {
//        switch (tournament.getFormat()) {
//            case SWISS:
//                return swissRoundManager.isSwissPhaseComplete(tournament);
//            case DOUBLE_ELIMINATION:
//                return doubleEliminationManager.isDoubleEliminationComplete(tournament);
//            case HYBRID:
//                // Ensure this checks both Swiss and Double Elimination completion properly
//                return swissRoundManager.isSwissPhaseComplete(tournament) &&
//                        doubleEliminationManager.isDoubleEliminationComplete(tournament);
//            default:
//                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
//        }
//    }
//
//    @Override
//    @Transactional
//    public Player determineWinner(Tournament tournament) {
//        if (isTournamentComplete(tournament)) {
//            completeTournament(tournament);
//            switch (tournament.getFormat()) {
//                case SWISS:
//                    logger.debug("Determining Swiss winner...");
//                    return swissRoundManager.determineSwissWinner(tournament);
//                case DOUBLE_ELIMINATION:
//                    logger.debug("Determining Double Elimination winner...");
//                    return doubleEliminationManager.determineWinner(tournament);
//                case HYBRID:
//                    logger.debug("Determining Hybrid winner from Double Elimination phase...");
//                    return doubleEliminationManager.determineWinner(tournament); // In Hybrid, the winner is from Double Elimination phase
//                default:
//                    throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
//            }
//        }
//        logger.debug("Tournament not complete; no winner determined.");
//        return null;
//    }
//
//    //todo:
//    @Override
//    public void setTournamentDetails(Tournament tournament) {
//        LocalDate startDate = LocalDate.now().plusDays(random.nextInt(10)); // Start in 0-9 days
//        LocalDate endDate = startDate.plusDays(5 + random.nextInt(5)); // Duration of 5-9 days
//        tournament.setStartDate(startDate);
//        tournament.setEndDate(endDate);
//
//        List<String> locations = Arrays.asList("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");
//        tournament.setLocation(locations.get(random.nextInt(locations.size())));
//
//        tournament.setStatus(TournamentStatus.SCHEDULED);
////        TournamentFormat[] formats = TournamentFormat.values();
////        tournament.setFormat(formats[random.nextInt(formats.length)]);
//        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
//
//        tournamentRepository.save(tournament);
//    }
//
//    @Override
//    public void startTournament(Tournament tournament) {
//        tournament.setStatus(TournamentStatus.ONGOING);
//        tournamentRepository.save(tournament);
//    }
//
//    @Override
//    public void completeTournament(Tournament tournament) {
//        tournament.setStatus(TournamentStatus.COMPLETED);
//        tournamentRepository.save(tournament);
//        //resetTournamentDataForPlayers
//    }
//}


package com.cs203.cs203system.service;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.utilities2.DoubleEliminationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentManagerServiceImpl implements TournamentManagerService {
    private static final Logger logger = LoggerFactory.getLogger(TournamentManagerServiceImpl.class);

    private final DoubleEliminationManager doubleEliminationManager;

    private final TournamentRepository tournamentRepository;

    private final PlayerRepository playerRepository;

    private final MatchRepository matchRepository;

    @Autowired
    public TournamentManagerServiceImpl(DoubleEliminationManager doubleEliminationManager, TournamentRepository tournamentRepository, PlayerRepository playerRepository, MatchRepository matchRepository) {
        this.doubleEliminationManager = doubleEliminationManager;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<Tournament> findTournamentById(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        return tournament; // Simply return the found tournament Optional
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTournamentById(Long id) {
        Tournament tournament = tournamentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Tournament id of " + id + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.SCHEDULED)) {
            throw new RuntimeException("Status of the tournament can only be deleted if it is scheduled");
        }
        tournamentRepository.deleteById(id);
    }


    // It creates the tournament in the database, however it does not start the tournament.
    // This gives the admin the control when to start the tournament.
    // Means that the tournament is SCHEDULED.
    // It will not add any players to the tournament. This will be reserved for the addPlayersToTournament()
    @Override
    @Transactional
    public Tournament createTournament(Tournament tournament) {
        tournament.setId(null);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament = tournamentRepository.save(tournament);
        return tournament;
    }


    // This method will add players to the tournament based on the admin choice and timing
    // It will check if the tournament is SCHEDULED. If not, it will reject.
    // It will check if the player is in the database. If not, it will reject
    @Override
    @Transactional
    public Tournament updatePlayersToTournament(Long tournamentId, List<Long> playerIds) {
        Tournament tournament = findTournamentById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id of " + tournamentId + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.SCHEDULED)) {
            throw new RuntimeException("Tournament is already ongoing or completed");
        } else if (playerIds.isEmpty()) {
            throw new RuntimeException("Please add at least 1 player");
        }

        List<Player> players = playerIds
                .stream()
                .map(playerId -> playerRepository
                        .findById(playerId)
                        .orElseThrow(() -> new NotFoundException("Player " + playerId + " not found")))
                .collect(Collectors.toList());
        tournament.setPlayers(players);

        return tournamentRepository.save(tournament);
    }

    // This will start the tournament. It will do the necessary checks to ensure each tournament based on the format of the tournament.
    // Validate if there are winner players and if the players are valid
    // If there is a problem (e.g. A tournament that has less than 2 players will not start/A Double elimination that does not have a even number
    // of players cannot start).
    // Will not do any updating of the details except start the game (change from scheduled to ongoing)
    @Override
    public Tournament startTournament(Long tournamentId) {
        Tournament tournament = findTournamentById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id of " + tournamentId + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.SCHEDULED)) {
            throw new RuntimeException("Tournament needed to be scheduled");
        } else if (tournament.getPlayers().size() < 2) {
            throw new RuntimeException("Tournament needs at least 2 players");
        } else if (!isPowerOfTwo(tournament.getPlayers().size())
                && tournament.getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            throw new RuntimeException("Double Elimination must have total number of players to power 2");
        }

        tournament.setStatus(TournamentStatus.ONGOING);

        switch (tournament.getFormat()) {
            case SWISS:
                logger.debug("Initializing Swiss tournament...");
//                swissRoundManager.initializeRounds(tournament);
                break;
            case DOUBLE_ELIMINATION:
                logger.debug("Initializing Double Elimination tournament...");
                return doubleEliminationManager.initializeDoubleElimination(tournament);
            case HYBRID:
                logger.debug("Initializing Hybrid tournament with Swiss rounds...");
//                swissRoundManager.initializeRounds(tournament);
                break;
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
        return null;
    }

    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    @Transactional
    @Override
    public Tournament inputResult(Match match) {
        Match updatedMatch = matchRepository
                .findById(match.getId())
                .orElseThrow(() -> new NotFoundException("Match of id " + match.getId() + " is not found"));


        if (match.getPlayer1Score() < 0 || match.getPlayer2Score() < 0) {
            throw new RuntimeException("Match score cannot be negative");
        } else if (match.getPlayer1Score() + match.getPlayer2Score() == 0) {
            throw new RuntimeException("The total match score must be more than 0");
        } else if (match.getPlayer1Score().equals(match.getPlayer2Score())
                && match.getTournament().getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            throw new RuntimeException("Draws are not allowed for Double Elimination");
        }

        updatedMatch.setPlayer1Score(match.getPlayer1Score());
        updatedMatch.setPlayer2Score(match.getPlayer2Score());
        updatedMatch = matchRepository.save(updatedMatch);

        switch (match.getTournament().getFormat()) {
            case SWISS:
//                swissRoundManager.initializeRounds(tournament);
                break;
            case DOUBLE_ELIMINATION:
                return doubleEliminationManager.receiveMatchResult(updatedMatch);
            case HYBRID:
//                swissRoundManager.initializeRounds(tournament);
                break;
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + match.getTournament().getFormat());
        }
        return null;
    }

    @Override
    public Tournament stopTournament(Tournament tournament) {
        return null;
    }


}
