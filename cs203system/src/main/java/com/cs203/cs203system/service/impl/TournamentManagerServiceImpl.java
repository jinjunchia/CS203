package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.Notification.Notification;
import com.cs203.cs203system.Notification.NotificationStatus;
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
import com.cs203.cs203system.service.TournamentFormatManager;
import com.cs203.cs203system.service.TournamentManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing tournaments.
 *
 * This service handles operations for creating, updating, starting, and managing
 * the results of tournaments, including different formats such as Swiss, Double
 * Elimination, and Hybrid. It also manages player participation and notifications.
 */
@Service
public class TournamentManagerServiceImpl implements TournamentManagerService {

    private final Map<TournamentFormat, TournamentFormatManager> formatManagers;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    /**
     * Constructs a TournamentManagerServiceImpl with the necessary dependencies.
     *
     * @param swissRoundManagerImpl the Swiss round manager for handling Swiss format tournaments
     * @param doubleEliminationManagerImpl the Double Elimination manager for double elimination tournaments
     * @param hybridManagerImpl the Hybrid format manager for hybrid tournaments
     * @param tournamentRepository the repository for tournament data
     * @param playerRepository the repository for player data
     * @param matchRepository the repository for match data
     * @param notificationService the service for sending notifications
     */
    @Autowired
    public TournamentManagerServiceImpl(
            SwissRoundManagerImpl swissRoundManagerImpl,
            DoubleEliminationManagerImpl doubleEliminationManagerImpl,
            SwissDoubleEliminationHybridManagerImpl hybridManagerImpl,
            TournamentRepository tournamentRepository,
            PlayerRepository playerRepository,
            MatchRepository matchRepository,
            NotificationService notificationService) {

        this.formatManagers = new HashMap<>();
        this.formatManagers.put(TournamentFormat.SWISS, swissRoundManagerImpl);
        this.formatManagers.put(TournamentFormat.DOUBLE_ELIMINATION, doubleEliminationManagerImpl);
        this.formatManagers.put(TournamentFormat.HYBRID, hybridManagerImpl);

        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves all tournaments.
     *
     * @return a list of all tournaments.
     */
    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    /**
     * Deletes a tournament by its ID if it is in a SCHEDULED status.
     *
     * @param id the ID of the tournament to delete.
     * @throws NotFoundException if the tournament with the given ID does not exist.
     * @throws RuntimeException if the tournament is not in a SCHEDULED status.
     */
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

    /**
     * Finds a tournament by its ID.
     *
     * @param id the ID of the tournament to find.
     * @return an Optional containing the tournament if found.
     * @throws NotFoundException if the tournament with the given ID does not exist.
     */
    @Override
    public Optional<Tournament> findTournamentById(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        return tournament;
    }

    /**
     * Creates a new tournament with a SCHEDULED status.
     *
     * @param tournament the tournament to be created.
     * @return the created tournament.
     */
    @Override
    @Transactional
    public Tournament createTournament(Tournament tournament) {
        tournament.setId(null);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament = tournamentRepository.save(tournament);
        return tournament;
    }

    /**
     * Adds players to a scheduled tournament.
     *
     * @param tournamentId the ID of the tournament.
     * @param playerIds the list of player IDs to add to the tournament.
     * @return the updated tournament.
     * @throws NotFoundException if the tournament or any player does not exist.
     * @throws RuntimeException if the tournament is not in a SCHEDULED status or if no players are provided.
     */
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
                .filter(player -> player.getEloRating() >= tournament.getMinEloRating()
                        && player.getEloRating() <= tournament.getMaxEloRating())
                .collect(Collectors.toList());
        tournament.setPlayers(players);

        return tournamentRepository.save(tournament);
    }

    /**
     * Starts the tournament if it meets the necessary conditions.
     *
     * @param tournamentId the ID of the tournament to start.
     * @return the updated tournament.
     * @throws NotFoundException if the tournament does not exist.
     * @throws RuntimeException if the tournament does not meet the conditions to start.
     */
    @Override
    public Tournament startTournament(Long tournamentId) {
        Tournament tournament = findTournamentById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id of " + tournamentId + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.SCHEDULED)) {
            throw new RuntimeException("Tournament needs to be scheduled");
        } else if (tournament.getPlayers().size() < 2) {
            throw new RuntimeException("Tournament needs at least 2 players");
        } else if (tournament.getFormat() == TournamentFormat.DOUBLE_ELIMINATION && !isPowerOfTwo(tournament.getPlayers().size())) {
            throw new RuntimeException("Double Elimination must have a total number of players equal to a power of 2");
        } else if (tournament.getPlayers().size() % 2 != 0) {
            throw new RuntimeException("Tournament needs an even number of players");
        } else if (tournament.getFormat() == TournamentFormat.HYBRID && !isPowerOfTwo(tournament.getPlayers().size())) {
            throw new RuntimeException("Hybrid must have a total number of players equal to a power of 2");
        }

        tournament.setStatus(TournamentStatus.ONGOING);

        sendNotification(tournament.getPlayers().stream().map(Player::getId).collect(Collectors.toList()),
                NotificationStatus.START, "Tournament is starting!");

        TournamentFormatManager manager = formatManagers.get(tournament.getFormat());
        if (manager == null) {
            throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
        return manager.initializeTournament(tournament);
    }

    /**
     * Inputs the result of a match and updates the tournament status accordingly.
     *
     * @param matchInRequest the match with updated results.
     * @return the updated tournament.
     * @throws NotFoundException if the match does not exist.
     * @throws RuntimeException if the match results are invalid.
     */
    @Transactional
    @Override
    public Tournament inputResult(Match matchInRequest) {
        Match matchInDatabase = matchRepository
                .findById(matchInRequest.getId())
                .orElseThrow(() -> new NotFoundException("Match of id " + matchInRequest.getId() + " is not found"));

        // Validation of scores and match statuses
        if (matchInRequest.getPlayer1Score() < 0 || matchInRequest.getPlayer2Score() < 0) {
            throw new RuntimeException("Match score cannot be negative");
        } else if (matchInRequest.getPlayer1Score() + matchInRequest.getPlayer2Score() == 0) {
            throw new RuntimeException("The total match score must be more than 0");
        } else if (matchInRequest.getPlayer1Score().equals(matchInRequest.getPlayer2Score())
                && matchInDatabase.getTournament().getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            throw new RuntimeException("Draws are not allowed for Double Elimination");
        } else if (matchInRequest.getStatus() == MatchStatus.SCHEDULED || matchInRequest.getStatus() == MatchStatus.WAITING) {
            throw new RuntimeException("Please input a valid match status");
        } else if (matchInDatabase.getStatus() == MatchStatus.COMPLETED) {
            throw new RuntimeException("Match has already been completed");
        } else if (matchInRequest.getStatus() == MatchStatus.PENDING) {
            matchInDatabase.setPlayer1Score(matchInRequest.getPlayer1Score());
            matchInDatabase.setPlayer2Score(matchInRequest.getPlayer2Score());
            matchInDatabase.setPunchesPlayer1(matchInRequest.getPunchesPlayer1());
            matchInDatabase.setPunchesPlayer2(matchInRequest.getPunchesPlayer2());
            matchInDatabase.setDodgesPlayer1(matchInRequest.getDodgesPlayer1());
            matchInDatabase.setDodgesPlayer2(matchInRequest.getDodgesPlayer2());
            matchInDatabase.setKoByPlayer1(matchInRequest.isKoByPlayer1());
            matchInDatabase.setKoByPlayer2(matchInRequest.isKoByPlayer2());
            matchInDatabase.setStatus(MatchStatus.COMPLETED);
            matchInDatabase = matchRepository.save(matchInDatabase);

            TournamentFormatManager manager = formatManagers.get(matchInDatabase.getTournament().getFormat());
            if (manager == null) {
                throw new IllegalArgumentException("Unsupported tournament format: " + matchInDatabase.getTournament().getFormat());
            }
            return manager.receiveMatchResult(matchInDatabase);
        }
        return matchInDatabase.getTournament();
    }

    /**
     * Determines the winner of a completed tournament.
     *
     * @param tournamentId the ID of the tournament.
     * @return the winning player.
     * @throws NotFoundException if the tournament does not exist.
     * @throws RuntimeException if the tournament is not completed.
     */
    public Player determineWinner(Long tournamentId) {
        Tournament tournament = tournamentRepository
                .findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id " + tournamentId + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.COMPLETED)) {
            throw new RuntimeException("Winner cannot be determined in a tournament that has not completed.");
        }

        TournamentFormatManager manager = formatManagers.get(tournament.getFormat());
        if (manager == null) {
            throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
        Player winner = manager.determineWinner(tournament);

        if (winner != null) {
            sendNotification(Collections.singletonList(winner.getId()), NotificationStatus.ENDED, winner.getName() + " is the winner!");
            return winner;
        }
        return null;
    }

    /**
     * Determines if a given number is a power of two.
     *
     * @param n the number to check.
     * @return true if the number is a power of two, false otherwise.
     */
    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Sends a notification to a list of players.
     *
     * @param playerIDs the list of player IDs to notify.
     * @param notificationStatus the status of the notification.
     * @param message the message to send.
     */
    public void sendNotification(List<Long> playerIDs, NotificationStatus notificationStatus, String message) {
        notificationService.sendNotification(
                playerIDs,
                Notification.builder()
                        .status(notificationStatus)
                        .message(message)
                        .build()
        );
    }

    /**
     * Updates an existing tournament by its ID.
     *
     * @param id                the ID of the tournament to be updated
     * @param updatedTournament the updated tournament details
     * @return the updated Tournament object after saving changes
     * @throws NotFoundException if the tournament with the given ID does not exist
     */
    public Tournament updateTournament(Long id, Tournament updatedTournament) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tournament id " + updatedTournament.getId() + " does not exist"));

        tournament.setName(updatedTournament.getName());
        tournament.setStartDate(updatedTournament.getStartDate());
        tournament.setLocation(updatedTournament.getLocation());
        tournament.setMinEloRating(updatedTournament.getMinEloRating());
        tournament.setMaxEloRating(updatedTournament.getMaxEloRating());
        tournament.setDescription(updatedTournament.getDescription());

        return tournamentRepository.save(tournament);
    }
}

