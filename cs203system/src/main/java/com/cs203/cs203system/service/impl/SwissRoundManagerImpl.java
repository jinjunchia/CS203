package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.EloService;
import com.cs203.cs203system.service.SwissRoundManager;
import com.cs203.cs203system.service.TournamentFormatManager;
import com.cs203.cs203system.utility.SwissRoundUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service implementation to manage Swiss-style tournaments.
 * Responsible for initializing rounds, handling match results,
 * updating standings, and determining the winner.
 */
@Service
public class SwissRoundManagerImpl implements TournamentFormatManager {

    private static final double WIN_POINTS = 1.0;
    private static final double LOSE_POINTS = 0.0;
    private static final double DRAW_POINTS = 0.5;

    private final TournamentRepository tournamentRepository;

    private static final HashMap<String, Double> scoreMap = new HashMap<>();

    private final PlayerRepository playerRepository;
    private final EloService eloService;

    /**
     * Constructs the SwissRoundManagerImpl with the necessary repositories and services.
     *
     * @param tournamentRepository Repository to manage tournament data.
     * @param playerRepository     Repository to manage player data.
     * @param eloService           Service to manage player ELO ratings.
     */
    @Autowired
    public SwissRoundManagerImpl(TournamentRepository tournamentRepository, PlayerRepository playerRepository, EloService eloService) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.eloService = eloService;
    }

    /**
     * Initializes a Swiss-style tournament.
     * Assigns players to matches in the first round and sets up the total number of rounds.
     *
     * @param tournament The tournament to be initialized.
     * @return The updated tournament with the first round initialized.
     */
    @Override
    @Transactional
    public Tournament initializeTournament(Tournament tournament){
        int numberOfRounds = calculateTotalRounds(tournament);
        tournament.setTotalSwissRounds(numberOfRounds);
        tournament.setCurrentRoundNumber(1);

        initializePlayersPoints(tournament.getPlayers());
        Collections.shuffle(tournament.getPlayers());

        List<Match> initialMatches =  createInitialMatches(tournament);
        tournament.getMatches().addAll(initialMatches);

        return tournamentRepository.save(tournament);
    }

    /**
     * Calculates the total number of rounds needed for the tournament.
     *
     * @param tournament The tournament whose rounds are calculated.
     * @return The total number of rounds.
     */
    private int calculateTotalRounds(Tournament tournament) {
        return (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2));
    }

    /**
     * Initializes each player's points to zero at the start of the tournament.
     *
     * @param players List of players in the tournament.
     */
    private void initializePlayersPoints(List<Player> players) {
        players.forEach(player -> player.setPoints(0.0));
    }

    /**
     * Creates the initial matches for the tournament's first round.
     *
     * @param tournament The tournament where matches will be created.
     * @return A list of created matches for the first round.
     */
    private List<Match> createInitialMatches(Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        for (int i = 1; i < tournament.getPlayers().size(); i += 2) {
            Match match = createMatch(tournament, tournament.getPlayers().get(i - 1), tournament.getPlayers().get(i), MatchStatus.SCHEDULED);
            matches.add(match);
        }
        return matches;
    }

    /**
     * Builds and returns a match object for the specified players.
     *
     * @param tournament The tournament the match belongs to.
     * @param player1    The first player in the match.
     * @param player2    The second player in the match (can be null for a bye match).
     * @param status     The status of the match.
     * @return The constructed match object.
     */
    private Match createMatch(Tournament tournament, Player player1, Player player2, MatchStatus status) {
        return Match.builder()
                .tournament(tournament)
                .matchDate(LocalDateTime.now())
                .player1(player1)
                .player2(player2)
                .status(status)
                .bracket(MatchBracket.SWISS)
                .round(tournament.getCurrentRoundNumber())
                .build();
    }

    /**
     * Receives a match result and updates the standings accordingly.
     * If all matches are completed, proceeds to the next round.
     *
     * @param match The match containing the results to be processed.
     * @return The updated tournament with updated match results and standings.
     */
    @Override
    @Transactional
    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = match.getTournament();

        updatePlayerScores(match);
        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);

        if (!allMatchesCompleted(tournament)) {
            return tournamentRepository.save(tournament);
        }

        if (isFinalRound(tournament)) {
            return handleFinalRound(tournament);
        }

        tournament.setCurrentRoundNumber(tournament.getCurrentRoundNumber() + 1);
        List<Match> newMatches = createMatchesForNextRound(tournament);
        tournament.getMatches().addAll(newMatches);

        return tournamentRepository.save(tournament);
    }

    /**
     * Updates player scores based on match results.
     * Adds points to the winner and loser, or both players in case of a draw.
     *
     * @param match The match from which results are used to update player scores.
     */
    //update player score
    private void updatePlayerScores(Match match){
        if(!match.isDraw()){
            Player winner = match.getWinner();
            Player loser = match.getLoser();
            winner.setPoints(winner.getPoints() + WIN_POINTS);
            loser.setPoints(loser.getPoints() + LOSE_POINTS);
            playerRepository.saveAll(List.of(winner,loser));
        }else{
            //draws
            Player player1 = match.getPlayer1();
            Player player2 = match.getPlayer2();
            player1.setPoints(player1.getPoints() + DRAW_POINTS);
            player2.setPoints(player2.getPoints() + DRAW_POINTS);
            playerRepository.saveAll(List.of(player1,player2));
        }
    }
    /**
     * Checks if all matches in the tournament are completed or marked as a bye.
     *
     * @param tournament The tournament to check.
     * @return True if all matches are completed or a bye; otherwise, false.
     */
    //check all match complete
    private boolean allMatchesCompleted(Tournament tournament) {
        return tournament.getMatches().stream()
                .allMatch(m -> m.getStatus().equals(MatchStatus.COMPLETED) || m.getStatus().equals(MatchStatus.BYE));
    }
    /**
     * Checks if the tournament has reached the final round.
     *
     * @param tournament The tournament to check.
     * @return True if it is the final round; otherwise, false.
     */
    //isFinalRound
    private boolean isFinalRound(Tournament tournament){
        return tournament.getCurrentRoundNumber() >= tournament.getTotalSwissRounds();
    }

    /**
     * Handles the logic for the final round of the tournament.
     * If there are multiple winners, a grand final match is created; otherwise, the tournament is marked as completed.
     *
     * @param tournament The tournament to update.
     * @return The updated tournament after handling the final round.
     */
    //handle final round
    private Tournament handleFinalRound(Tournament tournament) {
        List<Player> winners = SwissRoundUtils.findWinners(tournament);

        if (winners.size() <= 1 || tournament.getFormat().equals(TournamentFormat.HYBRID)) {
            tournament.setStatus(TournamentStatus.COMPLETED);
            if (tournament.getFormat().equals(TournamentFormat.SWISS)) {
                tournament.setEndDate(LocalDate.now());
            }
            return tournamentRepository.save(tournament);
        }

        Match finalMatch = createMatch(tournament, winners.get(0), winners.get(1), MatchStatus.SCHEDULED);
        finalMatch.setBracket(MatchBracket.GRAND_FINAL);
        tournament.getMatches().add(finalMatch);

        return tournamentRepository.save(tournament);
    }

    /**
     * Creates matches for the next round by pairing players based on their points.
     * Ensures that players do not face the same opponent twice.
     *
     * @param tournament The tournament to update.
     * @return A list of new matches created for the next round.
     */
    //createMatchforNextRound
    private List<Match> createMatchesForNextRound(Tournament tournament) {
        tournament.getPlayers().sort(Comparator.comparingDouble(Player::getPoints).reversed());

        List<Match> newMatches = new ArrayList<>();
        Set<Player> pairedPlayers = new HashSet<>();
        Map<Player, Set<Player>> matchHistory = SwissRoundUtils.createMatchHistory(tournament);

        for (int i = 0; i < tournament.getPlayers().size(); i++) {
            Player player = tournament.getPlayers().get(i);
            if (pairedPlayers.contains(player)) continue;

            Optional<Player> opponent = findOpponent(player, tournament, pairedPlayers, matchHistory, i);
            if (opponent.isPresent()) {
                Match match = createMatch(tournament, player, opponent.get(), MatchStatus.SCHEDULED);
                newMatches.add(match);
                pairedPlayers.add(player);
                pairedPlayers.add(opponent.get());
            } else {
                Match byeMatch = createMatch(tournament, player, null, MatchStatus.BYE);
                newMatches.add(byeMatch);
                pairedPlayers.add(player);
            }
        }
        return newMatches;
    }

    /**
     * Finds the best opponent for a player who has not yet been paired in the round.
     *
     * @param player        The player seeking an opponent.
     * @param tournament    The tournament in progress.
     * @param pairedPlayers Set of players already paired in the round.
     * @param matchHistory  Record of past matches to avoid repeat pairings.
     * @param startIdx      Index to start searching for an opponent.
     * @return An Optional containing the found opponent, or empty if no suitable opponent is found.
     */
    //find opponent
    private Optional<Player> findOpponent(Player player, Tournament tournament, Set<Player> pairedPlayers, Map<Player, Set<Player>> matchHistory, int startIdx) {
        for (int j = startIdx + 1; j < tournament.getPlayers().size(); j++) {
            Player potentialOpponent = tournament.getPlayers().get(j);

            Set<Player> playerHistory = matchHistory.getOrDefault(player, new HashSet<>()); // nullpointer except

            if (!pairedPlayers.contains(potentialOpponent) && !playerHistory.contains(potentialOpponent)) {
                return Optional.of(potentialOpponent);
            }
        }
        return Optional.empty();
    }
    /**
     * Determines the winner of the tournament.
     *
     * @param tournament The tournament for which the winner is determined.
     * @return The player with the highest points in the tournament.
     */
    @Override
    public Player determineWinner(Tournament tournament) {
        List<Player> winner = SwissRoundUtils.findWinners(tournament);
        if (winner.isEmpty()) {
            throw new IllegalArgumentException("No winner found");
        }
        return winner.get(0);
    }
}
