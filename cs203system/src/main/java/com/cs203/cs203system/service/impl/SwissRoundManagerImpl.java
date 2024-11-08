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

//    static {
//        scoreMap.put("WIN", 1.0);
//        scoreMap.put("DRAW", 0.5);
//        scoreMap.put("LOSE", 0.0);
//    }

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

//    public Tournament initializeSwiss(Tournament tournament) {
////        int numberOfRounds = (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2));
//        int numberOfRounds = calculateTotalRounds(tournament);
//        tournament.setTotalSwissRounds(numberOfRounds);
//        tournament.setCurrentRoundNumber(1);
//
//        initializePlayersPoints(tournament.getPlayers());
//        Collections.shuffle(tournament.getPlayers());
//
//        List<Match> initialMatches =  createInitialMatches(tournament);
//        tournament.getMatches().addAll(initialMatches);
//
//        return tournamentRepository.save(tournament);
//
////        for (int i = 1; i < tournament.getPlayers().size(); i += 2) {
////            tournament.getPlayers().get(i - 1)
////                    .setPoints(0.0);
////            tournament.getPlayers().get(i)
////                    .setPoints(0.0);
////
////            Match newMatch = Match.builder()
////                    .tournament(tournament)
////                    .matchDate(LocalDateTime.now())
////                    .player1(tournament.getPlayers().get(i - 1))
////                    .player2(tournament.getPlayers().get(i))
////                    .status(MatchStatus.SCHEDULED)
////                    .bracket(MatchBracket.SWISS)
////                    .round(tournament.getCurrentRoundNumber())
////                    .build();
////            tournament.getMatches().add(newMatch);
////        }
////
////        return tournamentRepository.save(tournament);
//    }

    private int calculateTotalRounds(Tournament tournament) {
        return (int) Math.ceil(Math.log(tournament.getPlayers().size()) / Math.log(2));
    }

    private void initializePlayersPoints(List<Player> players) {
        players.forEach(player -> player.setPoints(0.0));
    }

    private List<Match> createInitialMatches(Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        for (int i = 1; i < tournament.getPlayers().size(); i += 2) {
            Match match = createMatch(tournament, tournament.getPlayers().get(i - 1), tournament.getPlayers().get(i), MatchStatus.SCHEDULED);
            matches.add(match);
        }
        return matches;
    }

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
//    public Tournament receiveMatchResult(Match match) {
//        Tournament tournament = match.getTournament();
//        if (!match.isDraw()) {
//            Player winner = match.getWinner(), loser = match.getLoser();
//            System.out.println("Winner is: " + winner);
//            winner.setPoints(winner.getPoints() + scoreMap.get("WIN"));
//            loser.setPoints(loser.getPoints() + scoreMap.get("LOSE"));
//            playerRepository.saveAll(List.of(winner, loser));
//        } else {
//            Player player1 = match.getPlayer1(), player2 = match.getPlayer2();
//            player1.setPoints(player1.getPoints() + scoreMap.get("DRAW"));
//            player2.setPoints(player2.getPoints() + scoreMap.get("DRAW"));
//            playerRepository.saveAll(List.of(player1, player2));
//        }
//
//        // Update elo here
//        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
//
//        // Check if all the previous matches are either completed or Bye (for odd number of people)
//        boolean isAllMatchCompleted = match.getTournament().getMatches()
//                .stream()
//                .allMatch(m -> m.getStatus().equals(MatchStatus.COMPLETED)
//                        || m.getStatus().equals(MatchStatus.BYE));
//
//        if (!isAllMatchCompleted) {
//            return tournamentRepository.save(tournament);
//        }
//
//        // End tournament/Finals Case (if the number of current rounds == total possible rounds)
//        if (tournament.getCurrentRoundNumber() >= tournament.getTotalSwissRounds()) {
//
//            List<Player> winners = SwissRoundUtils.findWinners(tournament);
//
//            // It will end the swiss tournament if
//            //      a) There is only 1 winner (No draws)
//            //      b) If it's a hybrid (need to start the double elimination)
//            if (winners.size() <= 1 || tournament.getFormat().equals(TournamentFormat.HYBRID)) {
//                tournament.setStatus(TournamentStatus.COMPLETED);
//                if (tournament.getFormat().equals(TournamentFormat.SWISS)) tournament.setEndDate(LocalDate.now());
//                return tournamentRepository.save(tournament);
//            }
//
//            // Tiebreaker (Finals). Give them the final match to decide
//            // IF AFTER ALL THOSE FORMULAS DOES NOT WORK, then have to go to final match
//            Match newMatch = Match.builder()
//                    .tournament(tournament)
//                    .bracket(MatchBracket.GRAND_FINAL)
//                    .matchDate(LocalDateTime.now())
//                    .player1(winners.get(0))
//                    .player2(winners.get(1))
//                    .status(MatchStatus.SCHEDULED)
//                    .round(tournament.getCurrentRoundNumber())
//                    .build();
//            tournament.getMatches().add(newMatch);
//            return tournamentRepository.save(tournament);
//        }
//        tournament.setCurrentRoundNumber(tournament.getCurrentRoundNumber() + 1);
//
//        // Time to do normal matchmaking
//        // Need to ensure that when finding opponents, they are previously matched
//        // They need to be similar in points
//        // 1) Sort the list based on their points
//        // 2) Iterate through the list of players and check with match history, if there was a similar match
//        // 3) If there is an odd, the last person will be in a match with not one. The match will be "BYE"
//        tournament
//                .getPlayers()
//                .sort(Comparator
//                        .comparingDouble(Player::getPoints)
//                        .reversed());
//
//        List<Match> newMatches = new ArrayList<>();
//        Set<Player> pairedPlayers = new HashSet<>(); // For players that have already been paired
//        Map<Player, Set<Player>> matchHistory = SwissRoundUtils.createMatchHistory(tournament);
//        for (int i = 0; i < tournament.getPlayers().size(); i++) {
//
//            // Skip if the player is already matched
//            if (pairedPlayers.contains(tournament.getPlayers().get(i))) {
//                continue;
//            }
//
//            // Find the best opponent for the current unmatched player
//            boolean paired = false;
//            Player player = tournament.getPlayers().get(i);
//
//            for (int j = i + 1; j < tournament.getPlayers().size(); j++) {
//                Player possibleOpponent = tournament.getPlayers().get(j);
//
//                if (pairedPlayers.contains(possibleOpponent)
//                        && matchHistory.get(player).contains(possibleOpponent)) {
//                    continue;
//                }
//
//                Match newMatch = Match.builder()
//                        .tournament(tournament)
//                        .matchDate(LocalDateTime.now())
//                        .bracket(MatchBracket.SWISS)
//                        .player1(player)
//                        .player2(possibleOpponent)
//                        .status(MatchStatus.SCHEDULED)
//                        .round(tournament.getCurrentRoundNumber())
//                        .build();
//                newMatches.add(newMatch);
//                pairedPlayers.add(tournament.getPlayers().get(j));
//                pairedPlayers.add(possibleOpponent);
//                paired = true;
//                break;
//            }
//
//            if (!paired) {
//                Match newMatch = Match.builder()
//                        .tournament(tournament)
//                        .matchDate(LocalDateTime.now())
//                        .bracket(MatchBracket.SWISS)
//                        .player1(player)
//                        .status(MatchStatus.BYE)
//                        .round(tournament.getCurrentRoundNumber())
//                        .build();
//                pairedPlayers.add(player);
//                newMatches.add(newMatch);
//            }
//        }
//
//        tournament.getMatches().addAll(newMatches);
//        return tournamentRepository.save(tournament);
//    }



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

    //check all match complete
    private boolean allMatchesCompleted(Tournament tournament) {
        return tournament.getMatches().stream()
                .allMatch(m -> m.getStatus().equals(MatchStatus.COMPLETED) || m.getStatus().equals(MatchStatus.BYE));
    }
    //isFinalRound
    private boolean isFinalRound(Tournament tournament){
        return tournament.getCurrentRoundNumber() >= tournament.getTotalSwissRounds();
    }

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
