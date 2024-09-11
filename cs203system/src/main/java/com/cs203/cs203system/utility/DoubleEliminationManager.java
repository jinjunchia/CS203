package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DoubleEliminationManager {

    private static final Logger logger = LoggerFactory.getLogger(DoubleEliminationManager.class);

    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public DoubleEliminationManager(TournamentRepository tournamentRepository,
                                    MatchRepository matchRepository,
                                    PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    // Seed teams from Swiss into double elimination and run it
    @Transactional
    public void startDoubleElimination(Tournament tournament) {
        if (tournament.getFormat() != TournamentFormat.DOUBLE_ELIMINATION) {
            logger.warn("Tournament format is not set to DOUBLE_ELIMINATION. Current format: {}", tournament.getFormat());
            return;
        }

        logger.info("Starting double elimination phase for tournament: {}", tournament.getName());

        // Fetch top half of teams that qualified from the Swiss rounds
        List<Player> topPlayers = playerRepository.findByTournamentAndStatus(tournament, Player.Status.QUALIFIED);
        logger.debug("Teams qualified for double elimination: {}", topPlayers.stream().map(Player::getName).toList());

        // Randomly seed teams for the double elimination
        Collections.shuffle(topPlayers);
        logger.info("Teams shuffled for double elimination seeding.");

        // Create initial matchups and assign to upper bracket
        for (int i = 0; i < topPlayers.size() - 1; i += 2) {
            Player player1 = topPlayers.get(i);
            Player player2 = topPlayers.get(i + 1);

            createMatch(player1, player2, 1, tournament, Match.Bracket.UPPER);
        }

        runDoubleEliminationRounds(tournament);
    }


    @Transactional
    protected void runDoubleEliminationRounds(Tournament tournament) {
        logger.info("Running double elimination rounds for tournament: {}", tournament.getName());

        int roundNumber = 2;  // start from the second round

        while (true) {
            // Fetch ongoing matches from both brackets
            List<Match> upperMatches = matchRepository.findByTournamentAndBracketAndStatus(
                    tournament, Match.Bracket.UPPER, Match.Status.ONGOING);
            List<Match> lowerMatches = matchRepository.findByTournamentAndBracketAndStatus(
                    tournament, Match.Bracket.LOWER, Match.Status.ONGOING);

            logger.debug("Found {} ongoing upper bracket matches, {} ongoing lower bracket matches for round {}.",
                    upperMatches.size(), lowerMatches.size(), roundNumber);

            // Check if there are ongoing matches
            boolean hasUpperBracketMatches = !upperMatches.isEmpty();
            boolean hasLowerBracketMatches = !lowerMatches.isEmpty();

            // Exit if no matches are left
            if (!hasUpperBracketMatches && !hasLowerBracketMatches) {
                break;
            }

            // Process upper bracket matches
            if (hasUpperBracketMatches) {
                processMatches(upperMatches, tournament, roundNumber, Match.Bracket.UPPER);
            }

            // Process lower bracket matches
            if (hasLowerBracketMatches) {
                processMatches(lowerMatches, tournament, roundNumber, Match.Bracket.LOWER);
            }

            roundNumber++;
        }

        determineOverallWinner(tournament);

        logger.info("Double elimination rounds completed for tournament: {}", tournament.getName());
    }

    private void determineOverallWinner(Tournament tournament) {
        List<Player> remainingUpperPlayers = playerRepository.findByTournamentAndBracket(tournament, Match.Bracket.UPPER);
        List<Player> remainingLowerPlayers = playerRepository.findByTournamentAndBracket(tournament, Match.Bracket.LOWER);

        if (remainingUpperPlayers.size() == 1 && remainingLowerPlayers.isEmpty()) {
            Player winner = remainingUpperPlayers.get(0);
            logger.info("The overall winner of the double elimination tournament is: {}", winner.getName());
        } else if (remainingUpperPlayers.isEmpty() && remainingLowerPlayers.size() == 1) {
            Player winner = remainingLowerPlayers.get(0);
            logger.info("The overall winner of the double elimination tournament is: {}", winner.getName());
        } else {
            logger.warn("Unexpected situation: multiple teams remaining in the brackets.");
        }
    }

    private void processMatches(List<Match> matches, Tournament tournament, int roundNumber, Match.Bracket bracket) {
        Queue<Player> winners = new LinkedList<>();
        Queue<Player> losers = new LinkedList<>();

        for (Match match : matches) {
            Player winner = determineWinner(match);
            Player loser = determineLoser(match);

            match.setStatus(Match.Status.COMPLETED);
            matchRepository.save(match);

            winners.add(winner);

            if (bracket == Match.Bracket.UPPER) {
                loser.setBracket(Match.Bracket.LOWER);
                loser.setLosses(loser.getLosses() + 1);
            } else {
                loser.setLosses(loser.getLosses() + 1);
                if (loser.getLosses() >= 2) {
                    loser.setStatus(Player.Status.ELIMINATED);
                    logger.info("Team eliminated from double elimination: {}", loser.getName());
                } else {
                    losers.add(loser);
                }
            }

            playerRepository.save(loser);
            playerRepository.save(winner);
        }

        // Create new matches from winners and losers queues
        createNewMatches(winners, roundNumber + 1, tournament, bracket);
        createNewMatches(losers, roundNumber + 1, tournament, Match.Bracket.LOWER);

        concludeIfSingleTeamRemains(tournament, roundNumber);
    }

    private void createNewMatches(Queue<Player> players, int roundNumber, Tournament tournament, Match.Bracket bracket) {
        Set<Player> usedPlayers = new HashSet<>(); // To track which teams have been matched

        while (players.size() >= 2) {
            Player player1 = players.poll();
            Player player2 = players.poll();

            if (player1 == null || player2 == null || player1.equals(player2)) {
                logger.error("Invalid match pairing attempted with teams: {} vs {}",
                        player1 != null ? player1.getName() : "null",
                        player2 != null ? player2.getName() : "null");
                continue; // Skip this match creation
            }

            if (usedPlayers.contains(player1) || usedPlayers.contains(player2)) {
                logger.warn("Skipping already matched teams: {} or {}", player1.getName(), player2.getName());
                continue; // Ensure not to rematch already matched teams
            }

            createMatch(player1, player2, roundNumber, tournament, bracket);
            usedPlayers.add(player1);
            usedPlayers.add(player2);
        }

        // Handle carryover teams that were unmatched
        if (!players.isEmpty()) {
            Player carryOverPlayer = players.poll();
            logger.warn("Unmatched team carried over to next round: {}", carryOverPlayer.getName());
            if (bracket == Match.Bracket.UPPER && carryOverPlayer.getLosses() < 1) {
                carryOverPlayer.setBracket(Match.Bracket.UPPER);
            } else {
                carryOverPlayer.setBracket(Match.Bracket.LOWER);
            }
            playerRepository.save(carryOverPlayer);
        }
    }

    private void concludeIfSingleTeamRemains(Tournament tournament, int roundNumber) {
        List<Player> remainingUpperPlayers = playerRepository.findByTournamentAndBracket(tournament, Match.Bracket.UPPER);
        List<Player> remainingLowerPlayers = playerRepository.findByTournamentAndBracket(tournament, Match.Bracket.LOWER);

        if (remainingUpperPlayers.size() == 1 && remainingLowerPlayers.isEmpty()) {
            // Only one team remains in the upper bracket
            Player winner = remainingUpperPlayers.get(0);
            logger.info("The overall winner of the double elimination tournament is: {}", winner.getName());
        } else if (remainingUpperPlayers.isEmpty() && remainingLowerPlayers.size() == 1) {
            // Only one team remains in the lower bracket
            Player winner = remainingLowerPlayers.get(0);
            logger.info("The overall winner of the double elimination tournament is: {}", winner.getName());
        } else if (remainingUpperPlayers.size() == 1 && remainingLowerPlayers.size() == 1) {
            // Create a final match between the last remaining teams
            createMatch(remainingUpperPlayers.get(0), remainingLowerPlayers.get(0), roundNumber + 1, tournament, Match.Bracket.FINAL);
            logger.info("Final match set between upper bracket winner and lower bracket winner.");
        } else {
            logger.warn("Unexpected situation: multiple teams remaining in the brackets.");
        }
    }

    private void createMatch(Player player1, Player player2, int roundNumber, Tournament tournament, Match.Bracket bracket) {
        if (player1.equals(player2)) {
            logger.error("Attempted to match team against itself: {}", player1.getName());
            return; // Prevent creating matches where a team faces itself
        }

        Match match = new Match();
        match.setRoundNumber(roundNumber);
        match.setPlayers(new LinkedHashSet<>(List.of(player1, player2)));
        match.setTournament(tournament);
        match.setBracket(bracket);
        match.setStatus(Match.Status.ONGOING);
        matchRepository.save(match);

        logger.info("Match created for round {}: {} vs {} in {} bracket", roundNumber, player1.getName(), player2.getName(), bracket);
    }

    private Player determineWinner(Match match) {
        List<Player> players = new ArrayList<>(match.getPlayers());
        return players.get(0); // Replace with actual logic for determining winner
    }

    private Player determineLoser(Match match) {
        List<Player> players = new ArrayList<>(match.getPlayers());
        return players.get(1); // Replace with actual logic for determining loser
    }
}
