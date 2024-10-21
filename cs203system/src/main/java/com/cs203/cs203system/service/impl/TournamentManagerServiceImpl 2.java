package com.cs203.cs203system.service.impl;

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
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.SwissDoubleEliminationHybridManager;
import com.cs203.cs203system.service.SwissRoundManager;
import com.cs203.cs203system.service.TournamentManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TournamentManagerServiceImpl implements TournamentManagerService {
    private final DoubleEliminationManager doubleEliminationManager;

    private final TournamentRepository tournamentRepository;

    private final PlayerRepository playerRepository;

    private final MatchRepository matchRepository;

    private final SwissRoundManager swissRoundManager;

    private final SwissDoubleEliminationHybridManager swissDoubleEliminationHybridManager;

    /**
     * Constructor for TournamentManagerServiceImpl.
     *
     * @param doubleEliminationManager            the double elimination manager for handling double elimination workflows.
     * @param tournamentRepository                the repository used to manage Tournament data.
     * @param playerRepository                    the repository used to manage Player data.
     * @param matchRepository                     the repository used to manage Match data.
     * @param swissRoundManager                   the manager used to handle Swiss-style tournament rounds.
     * @param swissDoubleEliminationHybridManager the manager used to handle workflows for hybrid tournaments involving Swiss and double elimination formats.
     */
    @Autowired
    public TournamentManagerServiceImpl(DoubleEliminationManager doubleEliminationManager, TournamentRepository tournamentRepository, PlayerRepository playerRepository, MatchRepository matchRepository, SwissRoundManager swissRoundManager, SwissDoubleEliminationHybridManager swissDoubleEliminationHybridManager) {
        this.doubleEliminationManager = doubleEliminationManager;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.swissRoundManager = swissRoundManager;
        this.swissDoubleEliminationHybridManager = swissDoubleEliminationHybridManager;
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
     * @throws RuntimeException  if the tournament is not in a SCHEDULED status.
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
        return tournament; // Simply return the found tournament Optional
    }

    /**
     * Creates a new tournament with a SCHEDULED status.
     * Does not start the tournament or add players.
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
     * @param playerIds    the list of player IDs to add to the tournament.
     * @return the updated tournament.
     * @throws NotFoundException if the tournament or any player does not exist.
     * @throws RuntimeException  if the tournament is not in a SCHEDULED status or if no players are provided.
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
     * @throws RuntimeException  if the tournament does not meet the conditions to start.
     */
    @Override
    public Tournament startTournament(Long tournamentId) {
        Tournament tournament = findTournamentById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id of " + tournamentId + " does not exist"));

        if (!tournament.getStatus().equals(TournamentStatus.SCHEDULED)) {
            throw new RuntimeException("Tournament needs to be scheduled");
        } else if (tournament.getPlayers().size() < 2) {
            throw new RuntimeException("Tournament needs at least 2 players");
        } else if (tournament.getPlayers().size() % 2 != 0) {
            throw new RuntimeException("Tournament needs an even number of players");
        } else if (!isPowerOfTwo(tournament.getPlayers().size())
                && tournament.getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            throw new RuntimeException("Double Elimination must have total number of players to power 2");
        }

        tournament.setStatus(TournamentStatus.ONGOING);
        try {
            switch (tournament.getFormat()) {
                case SWISS:
                    return swissRoundManager.initializeSwiss(tournament);
                case DOUBLE_ELIMINATION:
                    return doubleEliminationManager.initializeDoubleElimination(tournament);
                case HYBRID:
                    return swissDoubleEliminationHybridManager.initializeHybrid(tournament);
                default:
                    throw new IllegalArgumentException("Unsupported tournament format:" + tournament.getFormat());
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Unsupported tournament format:");
        }

    }

    /**
     * Inputs the result of a match and updates the tournament status accordingly.
     *
     * @param matchInRequest the match with updated results.
     * @return the updated tournament.
     * @throws NotFoundException if the match does not exist.
     * @throws RuntimeException  if the match results are invalid.
     */
    @Transactional
    @Override
    public Tournament inputResult(Match matchInRequest) {
        Match matchInDatabase = matchRepository
                .findById(matchInRequest.getId())
                .orElseThrow(() -> new NotFoundException("Match of id " + matchInRequest.getId() + " is not found"));




        //validation of scores and match statuses
        if (matchInRequest.getPlayer1Score() < 0 || matchInRequest.getPlayer2Score() < 0) {
            throw new RuntimeException("Match score cannot be negative");

        } else if (matchInRequest.getPlayer1Score() + matchInRequest.getPlayer2Score() == 0) {
            throw new RuntimeException("The total match score must be more than 0");

        } else if (matchInRequest.getPlayer1Score().equals(matchInRequest.getPlayer2Score())
                && matchInDatabase.getTournament().getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            throw new RuntimeException("Draws are not allowed for Double Elimination");

            // if match status = SCHEDULED or WAITING (IN THE RESPONSE BODY) -> match has not started
        } else if (matchInRequest.getStatus() == MatchStatus.SCHEDULED || matchInRequest.getStatus() == MatchStatus.WAITING) {
            throw new RuntimeException("Please input a valid match status");

            // if match status = COMPLETED (IN THE DATABASE) -> match has completed and results have been inserted
        } else if (matchInDatabase.getStatus() == MatchStatus.COMPLETED) {
            throw new RuntimeException("Match has completed");

            // if match status = PENDING (IN THE RESPONSE BODY) -> match has completed but results have yet to be inserted
            // therefore, we need to insert results into repository and set the match status from PENDING to COMPLETED
            // Note: Only the system has the final authority to change the status to completed
        } else if (matchInRequest.getStatus() == MatchStatus.PENDING) {
            //inputting of the match result
            matchInDatabase.setPlayer1Score(matchInRequest.getPlayer1Score());
            matchInDatabase.setPlayer2Score(matchInRequest.getPlayer2Score());
            matchInDatabase.setStatus(MatchStatus.COMPLETED);
            matchInDatabase = matchRepository.save(matchInDatabase);

            switch (matchInDatabase.getTournament().getFormat()) {
                case SWISS:
                    return swissRoundManager.receiveMatchResult(matchInDatabase);
                case DOUBLE_ELIMINATION:
                    return doubleEliminationManager.receiveMatchResult(matchInDatabase);
                case HYBRID:
                    return swissDoubleEliminationHybridManager.receiveMatchResult(matchInDatabase);
                default:
                    throw new IllegalArgumentException("Unsupported tournament format: " + matchInRequest.getTournament().getFormat());
            }
        }
        return null;
    }


    /**
     * Determines the winner of a completed tournament.
     *
     * @param tournamentId the ID of the tournament.
     * @return the winner player.
     * @throws NotFoundException if the tournament does not exist.
     * @throws RuntimeException  if the tournament is not completed.
     */
    public Player determineWinner(Long tournamentId) {
        Tournament tournament = tournamentRepository
                .findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament id " + tournamentId + " does not exist"));
        if (!tournament.getStatus().equals(TournamentStatus.COMPLETED)) {
            throw new RuntimeException("Winner cannot be determined in a tournament that has not completed.");
        }

        switch (tournament.getFormat()) {
            case SWISS:
                return swissRoundManager.determineWinner(tournament);
            case DOUBLE_ELIMINATION:
                return doubleEliminationManager.determineWinner(tournament);
            case HYBRID:
                return swissDoubleEliminationHybridManager.determineWinner(tournament);
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
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
}
