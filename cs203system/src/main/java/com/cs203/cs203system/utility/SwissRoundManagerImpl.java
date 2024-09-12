package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.EloService;
import com.cs203.cs203system.utility.SwissRoundManager;
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
    private EloService eloService;

    private final Random random = new Random(); // Random generator for simulation for the points

    @Override
    public void initializeRounds(Tournament tournament) {
        if (tournament.getFormat() == TournamentFormat.SWISS || tournament.getFormat() == TournamentFormat.HYBRID) {
            int totalSwissRounds = (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2)) + 1;
            tournament.setTotalSwissRounds(totalSwissRounds);

            List<Player> players = tournament.getPlayers().stream()
                    .filter(player -> player.getEloRating() >= tournament.getMinEloRating()
                            && player.getEloRating() <= tournament.getMaxEloRating())
                    .collect(Collectors.toList());

            Collections.shuffle(players);
            pairPlayers(players);
        }
    }

    @Override
    public List<Pair<Player, Player>> pairPlayers(List<Player> players) {
        players.sort(Comparator.comparingDouble(Player::getPoints).reversed());

        List<Pair<Player, Player>> pairs = new ArrayList<>();

        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
            } else {
                pairs.add(Pair.of(players.get(i), null)); // Assign a bye if odd number
            }
        }

        createMatches(pairs);
        return pairs;
    }

    private void createMatches(List<Pair<Player, Player>> pairs) {
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                // Simulate match outcome by assigning random scores
                int player1Score = random.nextInt(10); // Simulate a score between 0 and 9
                int player2Score = random.nextInt(10);

                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(pair.getFirst().getTournament())
                        .roundNumber(pair.getFirst().getTournament().getCurrentRoundNumber())
                        .player1Score(player1Score)
                        .player2Score(player2Score)
                        .status(MatchStatus.COMPLETED) // Set as completed since we're simulating
                        .build();

                matchRepository.save(match);
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.addPoints(1.0); // Award points for bye
                // Consider saving any changes to player state if needed
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
                    // Consider updating winner stats like total games played, etc.
                    winner.incrementWins();
                }

                if (loser != null) {
                    loser.incrementLosses(); // increment loss count for the loser
                } else if (match.isDraw()) {
                    match.getPlayer1().addPoints(0.5);
                    match.getPlayer2().addPoints(0.5);
                    winner.incrementDraws();
                    loser.incrementDraws();
                }

                // Update ELO ratings after each match
                if (match.getPlayer1() != null && match.getPlayer2() != null) {
                    eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
                }
            }
        }

        tournament.setRoundsCompleted(tournament.getRoundsCompleted() + 1);

        if (isSwissPhaseComplete(tournament) && tournament.getFormat() == TournamentFormat.HYBRID) {
            transitionToDoubleElimination(tournament);
        }
    }

    @Override
    public boolean isSwissPhaseComplete(Tournament tournament) {
        return tournament.getRoundsCompleted() >= tournament.getTotalSwissRounds();
    }

    private void transitionToDoubleElimination(Tournament tournament) {
        List<Player> topPlayers = tournament.getPlayers().stream()
                .sorted(Comparator.comparingDouble(Player::getPoints).reversed())
                .limit(tournament.getPlayers().size() / 2)
                .collect(Collectors.toList());

        // Implement logic for transitioning top players to a Double Elimination phase
        // This could involve seeding players, initializing new matches, etc.
        if (!topPlayers.isEmpty()) {
            // Example placeholder: Initialize the first round of Double Elimination
            System.out.println("Transitioning to Double Elimination with top players: " + topPlayers);
            // Add code here to create double elimination matches/brackets based on topPlayers
        }
    }
}
