package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.enums.RoundType;
import com.cs203.cs203system.repository.RoundRepository;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.EloService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SwissRoundManagerImpl implements SwissRoundManager {

    private static final Logger logger = LoggerFactory.getLogger(SwissRoundManagerImpl.class);


    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private EloService eloService;

    private final Random random = new Random(); // random generator for simulation for the points

    @Override
    @Transactional // Ensure this method runs within a transaction
    public void initializeRounds(Tournament tournament) {
        logger.debug("Initializing Swiss rounds for tournament: {}", tournament.getId());
        if (tournament.getFormat() == TournamentFormat.SWISS || tournament.getFormat() == TournamentFormat.HYBRID) {
            int totalSwissRounds = (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2)) + 1;
            tournament.setTotalSwissRounds(totalSwissRounds);

            logger.debug("Total Swiss rounds set to: {}", totalSwissRounds);

            List<Player> players = tournament.getPlayers().stream()
                    .filter(player -> player.getEloRating() >= tournament.getMinEloRating()
                            && player.getEloRating() <= tournament.getMaxEloRating())
                    .collect(Collectors.toList());

            Collections.shuffle(players);

            // Initialize rounds
            for (int i = 1; i <= totalSwissRounds; i++) {
                logger.debug("Initializing round {} for tournament: {}", i, tournament.getId());
                Round round = new Round();
                round.setRoundNumber(i);
                round.setRoundType(RoundType.SWISS); // a RoundType enum
//                round.setTournament(tournament);
                round = roundRepository.save(round); // Save the round before creating matches
                logger.debug("Round {} saved with ID: {}", round.getRoundNumber(), round.getId());

                pairPlayers(players, round, tournament); // pass the round to pairPlayers
            }
        }
    }


    @Override
    public List<Pair<Player, Player>> pairPlayers(List<Player> players, Round round, Tournament tournament) { // Add Round parameter
        logger.debug("Pairing players for round {} of tournament: {}", round.getRoundNumber(), tournament.getId());
        players.sort(Comparator.comparingDouble(Player::getPoints).reversed());

        List<Pair<Player, Player>> pairs = new ArrayList<>();

        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
                logger.debug("Paired player {} with player {}", players.get(i).getId(), players.get(i + 1).getId());
            } else {
                pairs.add(Pair.of(players.get(i), null)); // assign a bye if odd number
                logger.debug("Player {} gets a bye", players.get(i).getId());
            }
        }

        createMatches(pairs, round, tournament); // pass the round to createMatches, apparently i need to pass tournament if not will have null issue
        return pairs;
    }

    @Transactional
    public void createMatches(List<Pair<Player, Player>> pairs, Round round,Tournament tournament) { // add Round parameter
        logger.debug("Creating matches for round {} of tournament: {}", round.getRoundNumber(), tournament.getId());
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                // simulate match outcome by assigning random scores
                int player1Score = random.nextInt(10); // simulate a score between 0 and 9
                int player2Score = random.nextInt(10);

                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .round(round) // associate match with the round
                        .player1Score(player1Score)
                        .player2Score(player2Score)
                        .status(MatchStatus.COMPLETED) // set as completed since we're simulating
                        .build();

                matchRepository.save(match);
                logger.debug("Match saved between player {} and player {} with scores {}-{}",
                        pair.getFirst().getId(), pair.getSecond().getId(), player1Score, player2Score);
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.addPoints(1.0); // award points for bye
                logger.debug("Player {} receives a bye and is awarded 1 point", playerWithBye.getId());
            }
        }
    }
//    @Transactional
//    public void createMatches(List<Pair<Player, Player>> pairs, Round round, Tournament tournament, List<Pair<Player, Player>> results) {
//        logger.debug("Creating matches for round {} of tournament: {}", round.getRoundNumber(), tournament.getId());
//
//        for (int i = 0; i < pairs.size(); i++) {
//            Pair<Player, Player> pair = pairs.get(i);
//            Pair<Player, Player> result = results.get(i);
//
//            if (pair.getSecond() != null) {
//                Player winner = result.getFirst();
//                Player loser = result.getSecond();
//
//                Match match = Match.builder()
//                        .player1(pair.getFirst())
//                        .player2(pair.getSecond())
//                        .tournament(tournament)
//                        .round(round) // associate match with the round
//                        .winner(winner)
//                        .loser(loser)
//                        .status(MatchStatus.COMPLETED) // set as completed since it's manually set
//                        .build();
//
//                // Update player stats for winner
//                winner.addPoints(1.0);
//                winner.incrementWins();
//                logger.debug("Player {} wins and now has {} points", winner.getId(), winner.getPoints());
//
//                // Update player stats for loser
//                loser.incrementLosses();
//                logger.debug("Player {} loses and now has {} losses", loser.getId(), loser.getLosses());
//
//                // Update ELO ratings
//                eloService.updateEloRatings(winner, loser, match);
//
//                matchRepository.save(match);
//                logger.debug("Match saved between player {} and player {} with winner {}", pair.getFirst().getId(), pair.getSecond().getId(), winner.getId());
//            } else {
//                Player playerWithBye = pair.getFirst();
//                playerWithBye.addPoints(1.0); // award points for bye
//                logger.debug("Player {} receives a bye and is awarded 1 point", playerWithBye.getId());
//            }
//        }
//    }


    @Override
    @Transactional
    public void updateStandings(Tournament tournament) {
        logger.debug("Updating standings for tournament: {}", tournament.getId());
        List<Match> matches = matchRepository.findByTournament(tournament);

        for (Match match : matches) {
            if (match.getStatus() == MatchStatus.COMPLETED) {
                Player winner = match.getWinner();
                Player loser = match.getLoser();

                if (winner != null) {
                    winner.addPoints(1.0); // points for a win
                    winner.incrementWins();//increment player win
                    logger.debug("Player {} wins and now has {} points", winner.getId(), winner.getPoints());
                }

                if (loser != null) {
                    loser.incrementLosses(); // increment loss count for the loser
                    logger.debug("Player {} loses and now has {} losses", loser.getId(), loser.getLosses());
                } else if (match.isDraw()) {
                    match.getPlayer1().addPoints(0.5);
                    match.getPlayer2().addPoints(0.5);
                    match.getPlayer1().incrementDraws();
                    match.getPlayer2().incrementDraws();//increment draws for both side
                    logger.debug("Match between player {} and player {} is a draw, both receive 0.5 points",
                            match.getPlayer1().getId(), match.getPlayer2().getId());
                }

                // Update ELO ratings after each match
                if (match.getPlayer1() != null && match.getPlayer2() != null) {
                    eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
                    logger.debug("Updated ELO ratings for players {} and {}", match.getPlayer1().getId(), match.getPlayer2().getId());
                }
            }
        }

        tournament.setRoundsCompleted(tournament.getRoundsCompleted() + 1);
        logger.debug("Rounds completed updated to {} for tournament: {}", tournament.getRoundsCompleted(), tournament.getId());

    }

    @Override
    public boolean isSwissPhaseComplete(Tournament tournament) {
        boolean isComplete = tournament.getRoundsCompleted() >= tournament.getTotalSwissRounds();
        logger.debug("Checking if Swiss phase is complete for tournament: {} - {}", tournament.getId(), isComplete);
        return isComplete;
    }


    public List<Player> getTopPlayers(Tournament tournament) {
        logger.debug("Getting top players for tournament: {}", tournament.getId());
        return tournament.getPlayers().stream()
                .sorted(Comparator.comparingDouble(Player::getPoints).reversed())
                .limit(tournament.getPlayers().size() / 2) // take top half of the players
                .collect(Collectors.toList());
    }

    // determine the winner if the tournament is Swiss only
    public Player determineSwissWinner(Tournament tournament) {
        if (tournament.getFormat() == TournamentFormat.SWISS && isSwissPhaseComplete(tournament)) {
            return tournament.getPlayers().stream()
                    .max(Comparator.comparingDouble(Player::getPoints))
                    .orElse(null); // return the player with the highest points
        }
        return null;
    }
}
