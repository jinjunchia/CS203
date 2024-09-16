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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SwissRoundManagerImpl implements SwissRoundManager {

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
        if (tournament.getFormat() == TournamentFormat.SWISS || tournament.getFormat() == TournamentFormat.HYBRID) {
            int totalSwissRounds = (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2)) + 1;
            tournament.setTotalSwissRounds(totalSwissRounds);

            List<Player> players = tournament.getPlayers().stream()
                    .filter(player -> player.getEloRating() >= tournament.getMinEloRating()
                            && player.getEloRating() <= tournament.getMaxEloRating())
                    .collect(Collectors.toList());

            Collections.shuffle(players);

            // Initialize rounds
            for (int i = 1; i <= totalSwissRounds; i++) {
                Round round = new Round();
                round.setRoundNumber(i);
                round.setRoundType(RoundType.SWISS); // a RoundType enum
                round.setTournament(tournament);
                round = roundRepository.save(round); // Save the round before creating matches

                pairPlayers(players, round, tournament); // pass the round to pairPlayers
            }
        }
    }


    @Override
    public List<Pair<Player, Player>> pairPlayers(List<Player> players, Round round, Tournament tournament) { // Add Round parameter
        players.sort(Comparator.comparingDouble(Player::getPoints).reversed());

        List<Pair<Player, Player>> pairs = new ArrayList<>();

        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
            } else {
                pairs.add(Pair.of(players.get(i), null)); // assign a bye if odd number
            }
        }

        createMatches(pairs, round, tournament); // pass the round to createMatches, apparently i need to pass tournament if not will have null issue
        return pairs;
    }

    @Transactional
    public void createMatches(List<Pair<Player, Player>> pairs, Round round,Tournament tournament) { // add Round parameter
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
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.addPoints(1.0); // award points for bye
            }
        }
    }

    @Override
    public void updateStandings(Tournament tournament) {
        List<Match> matches = matchRepository.findByTournament(tournament);

        for (Match match : matches) {
            if (match.getStatus() == MatchStatus.COMPLETED) {
                Player winner = match.getWinner();
                Player loser = match.getLoser();

                if (winner != null) {
                    winner.addPoints(1.0); // points for a win
                    winner.incrementWins();//increment player win
                }

                if (loser != null) {
                    loser.incrementLosses(); // increment loss count for the loser
                } else if (match.isDraw()) {
                    match.getPlayer1().addPoints(0.5);
                    match.getPlayer2().addPoints(0.5);
                    match.getPlayer1().incrementDraws();
                    match.getPlayer2().incrementDraws();//increment draws for both side
                }

                // Update ELO ratings after each match
                if (match.getPlayer1() != null && match.getPlayer2() != null) {
                    eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
                }
            }
        }

        tournament.setRoundsCompleted(tournament.getRoundsCompleted() + 1);
    }

    @Override
    public boolean isSwissPhaseComplete(Tournament tournament) {
        return tournament.getRoundsCompleted() >= tournament.getTotalSwissRounds();
    }


    public List<Player> getTopPlayers(Tournament tournament) {
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
