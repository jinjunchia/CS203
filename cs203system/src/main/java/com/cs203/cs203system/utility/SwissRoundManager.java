package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class SwissRoundManager {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private static final Logger logger = LoggerFactory.getLogger(SwissRoundManager.class);


    @Autowired
    public SwissRoundManager(MatchRepository matchRepository, PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    // this method starts the Swiss rounds by calculating the number of rounds based on the number of players
    @Transactional
    public void startSwissRounds(Tournament tournament) {
        logger.info("Starting Swiss rounds for tournament: {}", tournament.getName());
        List<Player> players = playerRepository.findByTournament(tournament);
        logger.debug("Players participating in Swiss rounds: {}", players.stream().map(Player::getName).toList());

        int totalRounds = (int) Math.ceil(Math.log(players.size()) / Math.log(2));  // Calculate rounds as log2(n)
        logger.info("Total Swiss rounds calculated: {}", totalRounds);

        for (int roundNumber = 1; roundNumber <= totalRounds; roundNumber++) {
            logger.info("Starting round {} of Swiss rounds for tournament: {}", roundNumber, tournament.getName());
            runSwissRound(tournament, roundNumber, roundNumber == 1);
        }

        eliminateBottomHalfPlayers(tournament);  // eliminate the bottom half of players
        logger.info("Swiss rounds completed for tournament: {}. Bottom half players eliminated.", tournament.getName());

    }

    // runs a single round of Swiss tournament
    @Transactional
    public void runSwissRound(Tournament tournament, int roundNumber, boolean isFirstRound) {
        logger.info("Running round {} of Swiss rounds for tournament: {}", roundNumber, tournament.getName());

        List<Player> players = playerRepository.findByTournament(tournament);
        logger.debug("Players for round {}: {}", roundNumber, players.stream().map(Player::getName).toList());

        List<Match> matches = isFirstRound
                ? createRandomPairings(players, roundNumber, tournament)
                : createPerformanceBasedPairings(players, roundNumber, tournament);

        for (Match match : matches) {
            Player winner = determineWinner(match);
            Player loser = determineLoser(match);
            boolean isDraw = false; // Assuming no draws unless you have a specific condition for it

            updatePoints(winner, loser, isDraw); // Update points here

            match.setStatus(MatchStatus.COMPLETED);
            matchRepository.save(match);
        }

        logger.info("Matches for round {} of Swiss rounds saved. Total matches: {}", roundNumber, matches.size());
    }

    // creates random pairings for the first round
    private List<Match> createRandomPairings(List<Player> players, int roundNumber, Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        Collections.shuffle(players);
        logger.debug("Players shuffled for random pairings: {}", players.stream().map(Player::getName).toList());


        for (int i = 0; i < players.size() - 1; i += 2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);

            Match match = new Match();
            match.setRoundNumber(roundNumber);
            match.setPlayers(new LinkedHashSet<>(List.of(player1, player2)));
            match.setTournament(tournament);
            match.setStatus(MatchStatus.ONGOING);

            matches.add(match);
            logger.info("Random pairing created: {} vs {}", player1.getName(), player2.getName());
        }

        // Handle bye for odd number of players
        if (players.size() % 2 != 0) {
            Player playerWithBye = players.get(players.size() - 1);
            logger.info("Assigning bye for odd player count to player: {} in round {}", playerWithBye.getName(), roundNumber);
            assignFreeWin(playerWithBye, tournament, roundNumber);
        }
        logger.info("Total random pairings created for round {}: {}", roundNumber, matches.size());
        return matches;
    }


    //there is some logic not implemented yet will need to look further when we have to
    // assigns a free win to the player with a bye not sure if we have the edge case of odd number in the player this is just in case we do
    // we need to discuss this in the future
    private void assignFreeWin(Player playerWithBye, Tournament tournament, int roundNumber) {
        logger.info("Assigning bye win to player: {} in round {} of tournament: {}", playerWithBye.getName(), roundNumber, tournament.getName());
        Match byeMatch = new Match();
        byeMatch.setRoundNumber(roundNumber);
        byeMatch.setTournament(tournament);
        byeMatch.setPlayers(new LinkedHashSet<>(List.of(playerWithBye)));
        byeMatch.setResult("WIN by bye");
        byeMatch.setStatus(MatchStatus.COMPLETED);

        playerWithBye.setWins(playerWithBye.getWins() + 1);
        playerWithBye.addPoints(1);

        matchRepository.save(byeMatch);
        playerRepository.save(playerWithBye);
    }

    // creates performance-based pairings for subsequent rounds
    private List<Match> createPerformanceBasedPairings(List<Player> players, int roundNumber, Tournament tournament) {
        logger.info("Creating performance-based pairings for round {} in tournament: {}", roundNumber, tournament.getName());

        // Sort players by points, descending. If points are equal, a secondary criterion like Elo or random can be used.
        players.sort((player1, player2) -> Double.compare(player2.getPoints(), player1.getPoints()));
        logger.debug("Players sorted by points: {}", players.stream().map(Player::getName).toList());

        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < players.size() - 1; i += 2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);

            Match match = new Match();
            match.setRoundNumber(roundNumber);
            match.setPlayers(new LinkedHashSet<>(List.of(player1, player2)));
            match.setTournament(tournament);
            match.setStatus(MatchStatus.ONGOING);
            logger.info("Pairing created: {} vs {}", player1.getName(), player2.getName());

            matches.add(match);
        }

        // Handle bye for odd number of players
        if (players.size() % 2 != 0) {
            Player playerWithBye = players.get(players.size() - 1);
            logger.info("Assigning bye for odd player count to player: {} in round {}", playerWithBye.getName(), roundNumber);
            assignFreeWin(playerWithBye, tournament, roundNumber);
        }
        logger.info("Total performance-based pairings created for round {}: {}", roundNumber, matches.size());
        return matches;
    }


    // eliminates the bottom half of the players based on their points
    @Transactional
    protected void eliminateBottomHalfPlayers(Tournament tournament) {
        logger.info("Eliminating bottom half of players based on points for tournament: {}", tournament.getName());


        List<Player> rankedPlayers = playerRepository.findByTournamentOrderByEloRatingDesc(tournament);
        logger.debug("Ranked players: {}", rankedPlayers.stream().map(Player::getName).toList());

        int halfSize = rankedPlayers.size() / 2;  // Calculate half of the total players

        // Keep the top half and eliminate the bottom half
        List<Player> topPlayers = rankedPlayers.subList(0, halfSize);
        List<Player> eliminatedPlayers = rankedPlayers.subList(halfSize, rankedPlayers.size());

        topPlayers.forEach(player -> {
            player.setStatus(Player.Status.QUALIFIED);
            logger.info("Player qualified: {}", player.getName());
        });

        eliminatedPlayers.forEach(player -> {
            player.setStatus(Player.Status.ELIMINATED);
            logger.info("Player eliminated: {}", player.getName());
        });

        playerRepository.saveAll(topPlayers);
        playerRepository.saveAll(eliminatedPlayers);
        logger.info("Players eliminated: {}, Players qualified: {}", eliminatedPlayers.size(), topPlayers.size());

    }

    private void updatePoints(Player winner, Player loser, boolean isDraw) {
        if (isDraw) {
            winner.addPoints(0.5);
            loser.addPoints(0.5);
            logger.info("Match drawn. Both {} and {} receive 0.5 points.", winner.getName(), loser.getName());
        } else {
            winner.addPoints(1);
            loser.addPoints(0);
            logger.info("Match won by {}. {} receives 1 point, {} receives 0 points.", winner.getName(), winner.getName(), loser.getName());
        }
        playerRepository.save(winner);
        playerRepository.save(loser);
    }

    //This arbitrarily make the player 1 win and player 2 lose because we do not have actually game logic
    private Player determineWinner(Match match) {
        List<Player> players = new ArrayList<>(match.getPlayers());
        return players.get(0);
    }

    private Player determineLoser(Match match) {
        List<Player> players = new ArrayList<>(match.getPlayers());

        return players.get(1);
    }
}
