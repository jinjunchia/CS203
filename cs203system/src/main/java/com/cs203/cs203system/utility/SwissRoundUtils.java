package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for managing Swiss-style tournament rounds.
 * This class contains methods to create match histories, determine winners,
 * and calculate tie-breaking scores such as Buchholz and Sonneborn-Berger.
 */
public class SwissRoundUtils {
    private static final HashMap<String, Double> scoreMap = new HashMap<>();

    static {
        scoreMap.put("WIN", 1.0);
        scoreMap.put("DRAW", 0.5);
        scoreMap.put("LOSE", 0.0);
    }

    /**
     * Creates a match history for a given tournament, mapping each player to a set of opponents they have faced.
     *
     * @param tournament The tournament for which the match history is created.
     * @return A map where each player is mapped to the set of players they have faced.
     */
    public static Map<Player, Set<Player>> createMatchHistory(Tournament tournament) {
        Map<Player, Set<Player>> matchHistory = new HashMap<>();
        tournament.getPlayers().forEach(p -> matchHistory.put(p, new HashSet<>()));

        for (Match match : tournament.getMatches()) {
            matchHistory.get(match.getPlayer1()).add(match.getPlayer2());
            matchHistory.get(match.getPlayer2()).add(match.getPlayer1());
        }

        return matchHistory;
    }

    /**
     * Determines the winners of a tournament based on the players' scores.
     * If there is a tie, the Buchholz or Sonneborn-Berger tie-breaking system is used.
     *
     * @param tournament The tournament for which the winners are determined.
     * @return A list of players who have the highest scores.
     */
    public static List<Player> findWinners(Tournament tournament) {
        List<Match> matches = tournament.getMatches();
        Map<Player, Double> ranking = tournament.getPlayers()
                .stream()
                .collect(Collectors.toMap(
                        player -> player,
                        player -> 0.0
                ));

        double highestScore = 0.0;
        for (Match match : matches) {
            if (match.isDraw()) {
                double newRating1 = ranking.get(match.getPlayer1()) + scoreMap.get("DRAW");
                double newRating2 = ranking.get(match.getPlayer2()) + scoreMap.get("DRAW");
                ranking.put(match.getPlayer1(), newRating1);
                ranking.put(match.getPlayer2(), newRating2);
            } else {
                double newRatingWinner = ranking.get(match.getWinner()) + scoreMap.get("WIN");
                ranking.put(match.getWinner(), newRatingWinner);
            }

            // Check winner
            highestScore = Math.max(highestScore, ranking.get(match.getPlayer1()));
            highestScore = Math.max(highestScore, ranking.get(match.getPlayer2()));
        }

        List<Player> winners = new ArrayList<>();

        for (Map.Entry<Player, Double> entry : ranking.entrySet()) {
            if (entry.getValue() == highestScore) {
                winners.add(entry.getKey());
            }
        }

        // If the normal score ranking does not work, we will use Buchholz.
        // If not found 1 winner, then it will use Sonneborn-Berger (SB)
        for (int i = 0; i < 2; i++) {
            if (winners.size() <= 1) {
                break;
            }

            if (i == 0) {
                winners = findWinnersBuchhloz(ranking, winners, tournament);
            } else {
                winners = findWinnerSB(ranking, winners, tournament);
            }
        }

        return winners;
    }

    /**
     * Determines winners using the Buchholz tie-breaking system.
     *
     * @param ranking The current ranking of players.
     * @param winners The list of players tied for the highest score.
     * @param tournament The tournament for which the winners are determined.
     * @return A list of players after applying the Buchholz tie-breaking system.
     */
    public static List<Player> findWinnersBuchhloz(Map<Player, Double> ranking, List<Player> winners, Tournament tournament) {
        Map<Player, Double> rankingBuchhloz = winners
                .stream()
                .collect(Collectors.toMap(player ->
                                player,
                        ranking::get));
        double highestScore = 0.0;

        for (Map.Entry<Player, Double> entry : rankingBuchhloz.entrySet()) {
            Player player = entry.getKey();
            Double sum = entry.getValue();

            // Find all players that he/she has played with
            for (Match match : tournament.getMatches()) {
                if (!match.getPlayer1().equals(player) && !match.getPlayer2().equals(player)) {
                    continue;
                }

                Player opponent = match.getPlayer1().equals(player) ? match.getPlayer2() : match.getPlayer1();
                sum += opponent.getPoints();
            }
            highestScore = Math.max(highestScore, sum);
            entry.setValue(sum);
        }

        return getWinners(ranking, highestScore);
    }

    /**
     * Determines winners using the Sonneborn-Berger tie-breaking system.
     *
     * @param ranking The current ranking of players.
     * @param winners The list of players tied for the highest score.
     * @param tournament The tournament for which the winners are determined.
     * @return A list of players after applying the Sonneborn-Berger tie-breaking system.
     */
    public static List<Player> findWinnerSB(Map<Player, Double> ranking, List<Player> winners, Tournament tournament) {
        Map<Player, Double> rankingSB = winners
                .stream()
                .collect(Collectors.toMap(player ->
                                player,
                        ranking::get));
        double highestScore = 0.0;

        for (Map.Entry<Player, Double> entry : rankingSB.entrySet()) {
            Player player = entry.getKey();
            Double sum = entry.getValue();

            // Find all players that he/she has played with
            for (Match match : tournament.getMatches()) {
                if (!match.getPlayer1().equals(player) && !match.getPlayer2().equals(player)) {
                    continue;
                }

                Player opponent = match.getPlayer1().equals(player) ? match.getPlayer2() : match.getPlayer1();

                if (match.isDraw()) {
                    sum += opponent.getPoints() / 2;
                } else if (match.getWinner().equals(player)) {
                    sum += opponent.getPoints();
                }
            }
            highestScore = Math.max(highestScore, sum);
            entry.setValue(sum);
        }

        return getWinners(ranking, highestScore);
    }

    /**
     * Retrieves the list of players with the highest score.
     *
     * @param ranking The current ranking of players.
     * @param highestScore The highest score to be matched.
     * @return A list of players who have the highest score.
     */
    private static List<Player> getWinners(Map<Player, Double> ranking, double highestScore) {
        List<Player> winners = new ArrayList<>();

        for (Map.Entry<Player, Double> entry : ranking.entrySet()) {
            if (Objects.equals(entry.getValue(), highestScore)) {
                winners.add(entry.getKey());
            }
        }

        return winners;
    }

    /**
     * Retrieves the top players based on their rankings from the given tournament.
     * <p>
     * This method calculates the ranking for each player in the tournament based on the outcomes
     * of the matches. Player scores are initialized to 0.0, and are updated based on whether
     * they won or drew a match. The method returns a list of players with the highest scores,
     * sorted in descending order of their ranking.
     * </p>
     *
     * @param tournament      The tournament from which players and matches are retrieved.
     * @param numberOfPlayers The number of top players to retrieve.
     * @return A list of the top players, sorted by their ranking in descending order.
     */
    public static List<Player> getTopPlayers(Tournament tournament, int numberOfPlayers) {
        Map<Player, Double> ranking = tournament.getPlayers()
                .stream()
                .collect(Collectors.toMap(
                        player -> player,
                        player -> 0.0
                ));

        // Update rankings based on match results
        for (Match match : tournament.getMatches()) {
            Player player1 = match.getPlayer1();
            Player player2 = match.getPlayer2();

            if (match.isDraw()) {
                ranking.put(player1, ranking.get(player1) + scoreMap.get("DRAW"));
                ranking.put(player2, ranking.get(player2) + scoreMap.get("DRAW"));
            } else {
                Player winner = match.getWinner();
                ranking.put(winner, ranking.get(winner) + scoreMap.get("WIN"));
            }
        }

        // Use a priority queue to keep track of the top N players
        PriorityQueue<Map.Entry<Player, Double>> minHeap = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue)
        );

        // Remove the player with the lowest score if heap exceeds size
        for (Map.Entry<Player, Double> entry : ranking.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > numberOfPlayers) {
                minHeap.poll();
            }
        }

        // Extract top players from the priority queue
        List<Player> topPlayers = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            topPlayers.add(minHeap.poll().getKey());
        }

        // Since the priority queue gives players in ascending order, reverse the list for descending order.
        Collections.reverse(topPlayers);

        return topPlayers;
    }
}
