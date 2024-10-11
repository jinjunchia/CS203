package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.EloService;
import com.cs203.cs203system.service.SwissRoundManager;
import com.cs203.cs203system.utility.SwissRoundUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service implementation to manage Swiss-style tournaments.
 * Responsible for initializing rounds, handling match results,
 * updating standings, and determining the winner.
 */
@Service
public class SwissRoundManagerImpl implements SwissRoundManager {

    private final TournamentRepository tournamentRepository;

    private static final HashMap<String, Double> scoreMap = new HashMap<>();

    static {
        scoreMap.put("WIN", 1.0);
        scoreMap.put("DRAW", 0.5);
        scoreMap.put("LOSE", 0.0);
    }

    private final PlayerRepository playerRepository;
    private final EloService eloService;

    /**
     * Constructs the SwissRoundManagerImpl with the necessary repositories and services.
     *
     * @param tournamentRepository Repository to manage tournament data.
     * @param playerRepository Repository to manage player data.
     * @param eloService Service to manage player ELO ratings.
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
    public Tournament initializeSwiss(Tournament tournament) {
        int numberOfRounds = (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2));
        tournament.setTotalSwissRounds(numberOfRounds);
        tournament.setCurrentRoundNumber(1);

        Collections.shuffle(tournament.getPlayers());

        for (int i = 1; i < tournament.getPlayers().size(); i += 2) {
            tournament.getPlayers().get(i - 1)
                    .setPoints(0.0);
            tournament.getPlayers().get(i)
                    .setPoints(0.0);

            Match newMatch = Match.builder()
                    .tournament(tournament)
                    .matchDate(LocalDate.now())
                    .player1(tournament.getPlayers().get(i - 1))
                    .player2(tournament.getPlayers().get(i))
                    .status(MatchStatus.SCHEDULED)
                    .round(tournament.getCurrentRoundNumber())
                    .build();
            tournament.getMatches().add(newMatch);
        }

        return tournamentRepository.save(tournament);
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
        Player winner = match.getWinner(), loser = match.getLoser();
        if (!match.isDraw()) {
            winner.setPoints(winner.getPoints() + scoreMap.get("WIN"));
            loser.setPoints(loser.getPoints() + scoreMap.get("LOSE"));
        } else {
            winner.setPoints(winner.getPoints() + scoreMap.get("DRAW"));
            loser.setPoints(loser.getPoints() + scoreMap.get("DRAW"));
        }
        playerRepository.saveAll(List.of(winner, loser));

        // Update elo here
        eloService.updateEloRatings(winner, loser, match);

        // Check if all the previous matches are either completed or Bye (for odd number of people)
        boolean isAllMatchCompleted = match.getTournament().getMatches()
                .stream()
                .allMatch(m -> m.getStatus().equals(MatchStatus.COMPLETED)
                        || m.getStatus().equals(MatchStatus.BYE));

        if (!isAllMatchCompleted) {
            return tournamentRepository.save(tournament);
        }

        // End tournament/Finals Case (if the number of current rounds == total possible rounds)
        if (tournament.getCurrentRoundNumber() >= tournament.getTotalSwissRounds()) {
            List<Player> winners = SwissRoundUtils.findWinners(tournament);

            if (winners.size() <= 1) {
                tournament.setStatus(TournamentStatus.COMPLETED);
                tournament.setEndDate(LocalDate.now());
                return tournamentRepository.save(tournament);
            }

            // Tiebreaker (Finals). Give them the final match to decide
            // IF AFTER ALL THOSE FORMULAS DOES NOT WORK, then have to go to final match
            Match newMatch = Match.builder()
                    .tournament(tournament)
                    .bracket(MatchBracket.GRAND_FINAL)
                    .matchDate(LocalDate.now())
                    .player1(winners.get(0))
                    .player2(winners.get(1))
                    .status(MatchStatus.SCHEDULED)
                    .round(tournament.getCurrentRoundNumber())
                    .build();
            tournament.getMatches().add(newMatch);
            return tournamentRepository.save(tournament);
        }
        tournament.setCurrentRoundNumber(tournament.getCurrentRoundNumber() + 1);

        // Time to do normal matchmaking
        // Need to ensure that when finding opponents, they are previously matched
        // They need to be similar in points
        // 1) Sort the list based on their points
        // 2) Iterate through the list of players and check with match history, if there was a similar match
        // 3) If there is an odd, the last person will be in a match with not one. The match will be "BYE"
        tournament
                .getPlayers()
                .sort(Comparator
                        .comparingDouble(Player::getPoints)
                        .reversed());

        List<Match> newMatches = new ArrayList<>();
        Set<Player> pairedPlayers = new HashSet<>(); // For players that have already been paired
        Map<Player, Set<Player>> matchHistory = SwissRoundUtils.createMatchHistory(tournament);
        for (int i = 0; i < tournament.getPlayers().size(); i++) {

            // Skip if the player is already matched
            if (pairedPlayers.contains(tournament.getPlayers().get(i))) {
                continue;
            }

            // Find the best opponent for the current unmatched player
            boolean paired = false;
            Player player = tournament.getPlayers().get(i);

            for (int j = i + 1; j < tournament.getPlayers().size(); j++) {
                Player possibleOpponent = tournament.getPlayers().get(j);

                if (pairedPlayers.contains(possibleOpponent)
                        && matchHistory.get(player).contains(possibleOpponent)) {
                    continue;
                }

                Match newMatch = Match.builder()
                        .tournament(tournament)
                        .matchDate(LocalDate.now())
                        .bracket(MatchBracket.SWISS)
                        .player1(player)
                        .player2(possibleOpponent)
                        .status(MatchStatus.SCHEDULED)
                        .round(tournament.getCurrentRoundNumber())
                        .build();
                newMatches.add(newMatch);
                pairedPlayers.add(tournament.getPlayers().get(j));
                pairedPlayers.add(possibleOpponent);
                paired = true;
                break;
            }

            if (!paired) {
                Match newMatch = Match.builder()
                        .tournament(tournament)
                        .matchDate(LocalDate.now())
                        .bracket(MatchBracket.SWISS)
                        .player1(player)
                        .status(MatchStatus.BYE)
                        .round(tournament.getCurrentRoundNumber())
                        .build();
                pairedPlayers.add(player);
                newMatches.add(newMatch);
            }
        }

        tournament.getMatches().addAll(newMatches);
        return tournamentRepository.save(tournament);
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
