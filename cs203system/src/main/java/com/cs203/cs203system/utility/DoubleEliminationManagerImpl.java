package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.RoundType;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.RoundRepository;
import com.cs203.cs203system.service.EloService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoubleEliminationManagerImpl implements DoubleEliminationManager {
    private static final Logger logger = LoggerFactory.getLogger(DoubleEliminationManagerImpl.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private EloService eloService;

    private final Random random = new Random();

    private int round_num = 0;

    // Updated to accept Tournament and List<Player>
    @Override
    @Transactional
    public void initializeDoubleElimination(Tournament tournament, List<Player> players) {
        // Ensure the tournament format is suitable for Double Elimination
        if (tournament.getFormat() != TournamentFormat.DOUBLE_ELIMINATION
                && tournament.getFormat() != TournamentFormat.HYBRID) {
            logger.warn("Tournament format is not Double Elimination or Hybrid. Skipping initialization.");
            return;
        }

        if (players.isEmpty()) {
            logger.warn("No eligible players found for tournament: {}", tournament.getName());
            return;
        }

        // Shuffle players and place them in the upper bracket
        Collections.shuffle(players);
        players.forEach(player -> {
            player.setBracket(PlayerBracket.UPPER);
            player.setStatus(PlayerStatus.QUALIFIED);
            playerRepository.save(player);
            logger.debug("Player {} initialized in UPPER bracket", player.getName());
        });

        // Initialize the first round in the upper bracket
        Round upperRound = initializeRound(tournament, 1, RoundType.UPPER);
        createMatches(tournament, players, upperRound);
//        logger.debug(round_num  + "in initialise");
//        UpperCreateMatches(tournament, players, upperRound);
//        LowerCreateMatches(tournament, players, upperRound);
    }

    @Override
    @Transactional
    public Round initializeNewRound(Tournament tournament, RoundType roundType) {
        int nextRoundNumber = getNextRoundNumber(tournament, roundType);
        Round round = new Round();
        round.setRoundNumber(nextRoundNumber);
        round.setRoundType(roundType);
        return roundRepository.save(round);
    }

    @Override
    @Transactional
    public Round initializeRound(Tournament tournament, int roundNumber, RoundType roundType) {
        Round round = new Round();
        round.setRoundNumber(roundNumber);
        round.setRoundType(roundType);
//        round.setTournament(tournament);
        return roundRepository.save(round);
    }

    private int getNextRoundNumber(Tournament tournament, RoundType roundType) {
        Optional<Round> latestRound = roundRepository.findTopByMatches_Tournament_IdAndRoundTypeOrderByRoundNumberDesc(tournament.getId(), roundType);
        return latestRound.map(round -> round.getRoundNumber() + 1).orElse(1);
    }

    @Override
    @Transactional
    public List<Match> createMatches(Tournament tournament, List<Player> players, Round round) {
        players = players.stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        Collections.shuffle(players);
        List<Pair<Player, Player>> pairs = pairPlayers(players);
        List<Match> matches = new ArrayList<>();
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .round(round)
                        .status(MatchStatus.SCHEDULED)
                        .build();

                matchRepository.save(match);
                matches.add(match);
                logger.debug("Scheduled match between {} and {} in round {}",
                        pair.getFirst().getName(), pair.getSecond().getName(), round.getRoundNumber());
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.incrementWins();  // Bye counts as a win
                playerRepository.save(playerWithBye);
                logger.debug("Player {} gets a bye in round {}", playerWithBye.getName(), round.getRoundNumber());
            }
        }
        return matches;
    }

    public List<Match> LowerCreateMatches(Tournament tournament, List<Player> players, Round round) {
        players = players.stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED && player.getBracket() != PlayerBracket.UPPER)
                .collect(Collectors.toList());
        Collections.shuffle(players);
        List<Pair<Player, Player>> pairs = pairPlayers(players);
        List<Match> matches = new ArrayList<>();
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .round(round)
                        .status(MatchStatus.SCHEDULED)
                        .build();

                matchRepository.save(match);
                matches.add(match);
                logger.debug("Scheduled match between {} and {} in round {}",
                        pair.getFirst().getName(), pair.getSecond().getName(), round.getRoundNumber());
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.incrementWins();  // Bye counts as a win
                playerRepository.save(playerWithBye);
                logger.debug("Player {} gets a bye in round {}", playerWithBye.getName(), round.getRoundNumber());
            }
        }
        return matches;
    }

    public List<Match> UpperCreateMatches(Tournament tournament, List<Player> players, Round round) {
        players = players.stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED && player.getBracket() != PlayerBracket.UPPER)
                .collect(Collectors.toList());
        Collections.shuffle(players);
        List<Pair<Player, Player>> pairs = pairPlayers(players);
        List<Match> matches = new ArrayList<>();
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .round(round)
                        .status(MatchStatus.SCHEDULED)
                        .build();

                matchRepository.save(match);
                matches.add(match);
                logger.debug("Scheduled match between {} and {} in round {}",
                        pair.getFirst().getName(), pair.getSecond().getName(), round.getRoundNumber());
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.incrementWins();  // Bye counts as a win
                playerRepository.save(playerWithBye);
                logger.debug("Player {} gets a bye in round {}", playerWithBye.getName(), round.getRoundNumber());
            }
        }
        return matches;
    }

    @Override
    @Transactional
    public void updateStandings(Tournament tournament) {
        logger.debug("Updating standings for tournament: {}", tournament.getName());
        List<Match> matches = matchRepository.findByTournament(tournament);

        for (Match match : matches) {

            Double p1_rating = match.getPlayer1().getEloRating();
            Double p2_rating = match.getPlayer2().getEloRating();

            Double p1_EO = 1 / ( 1 + Math.pow(10,(p2_rating-p1_rating) / 400));
            Double p2_EO = 1 / ( 1 + Math.pow(10,(p1_rating-p2_rating) / 400));

            int randomNum = random.nextInt(100);

            Player winner = match.getPlayer1();
            Player loser = match.getPlayer2();

            winner.setTotalGamesPlayed(winner.getTotalGamesPlayed()+1);
            loser.setTotalGamesPlayed(loser.getTotalGamesPlayed()+1);

            winner.setEloRating(winner.getEloRating() + 32 * (1-p1_EO));
            loser.setEloRating(winner.getEloRating() + 32 * (0-p2_EO));

            if (randomNum > p1_EO*100) {
                loser = match.getPlayer1();
                winner = match.getPlayer2();
                winner.setEloRating(winner.getEloRating() + 32 * (1-p2_EO));
                loser.setEloRating(winner.getEloRating() + 32 * (0-p1_EO));
            }
            logger.debug(winner + " this is the winner");
            logger.debug(winner + " this is the loser");
            if (round_num == 1) {
                logger.debug("Hello im here");
                //Put new updatePlayerBracketHere
            }
            updatePlayerBracket(winner, loser);
            updateEloRatings(match);

            winner.incrementWins();
            loser.incrementLosses();

            match.setStatus(MatchStatus.COMPLETED);
            matchRepository.save(match);
            logger.debug("Match completed between {} and {} - Winner: {}",
                    winner.getName(), loser.getName(), winner.getName());
        }
        round_num++;    //Temporary counter
        handleFinalMatchIfNecessary(tournament);
    }


    @Override
    public boolean isDoubleEliminationComplete(Tournament tournament) {
        logger.debug("Checking if double elimination is complete for tournament: {}", tournament.getName());

        // Check if all matches are completed
        boolean allMatchesCompleted = matchRepository.findByTournament(tournament).stream()
                .allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);

        if (!allMatchesCompleted) {
            logger.debug("Matches not completed yet");
        }

        // Check the number of remaining active players
        long remainingPlayers = tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .count();

        logger.debug(remainingPlayers + " still remaining");

        // Double Elimination should be complete if only one player remains
        boolean isComplete = allMatchesCompleted && remainingPlayers < 2;

        if (remainingPlayers == 2) {
            logger.debug("Exactly 2 players remain. Checking for final match.");
            handleFinalMatchIfNecessary(tournament);
        } else if (remainingPlayers > 2) {
            logger.error("Unexpected number of remaining players: {}. Expected 1.", remainingPlayers);
        }

        logger.debug("Double elimination completion status: {} (All matches completed: {}, Remaining players: {})",
                isComplete, allMatchesCompleted, remainingPlayers);

        return isComplete;
    }

    @Override
    public Player determineWinner(Tournament tournament) {
        logger.debug("Determining winner for tournament: {}", tournament.getName());
        Player winner = tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .findFirst()
                .orElse(null);
        if (winner != null) {
            logger.debug("Winner determined: {}", winner.getName());
        } else {
            logger.debug("No winner determined yet.");
        }
        return winner;
    }

    @Transactional
    public void updatePlayerBracket(Player winner, Player loser) {
        playerRepository.save(winner);
        if (loser.getBracket() == PlayerBracket.UPPER) {
            loser.setBracket(PlayerBracket.LOWER);
            playerRepository.save(loser);
            logger.debug("Player {} moved to LOWER bracket after losing in UPPER", loser.getName());
        } else if (loser.getBracket() == PlayerBracket.LOWER) {
            loser.incrementLosses();
            if (loser.getLosses() >= 2) {
                loser.setStatus(PlayerStatus.ELIMINATED);
                logger.debug("Player {} has been eliminated after second loss in LOWER bracket", loser.getName());
            }
            playerRepository.save(loser);
        }
    }

    //Testing
//    @Transactional
//    public void updatePlayerBracket(Player winner, Player loser) {
//        winner.incrementWins();
//        loser.incrementLosses();
//        playerRepository.save(winner);
//        if (loser.getBracket() == PlayerBracket.UPPER) {
//            loser.setBracket(PlayerBracket.LOWER);
//            playerRepository.save(loser);
//            logger.debug("Player {} moved to LOWER bracket after losing in UPPER", loser.getName());
//        }
//    }
//
    public void loserBracket(Player winner, Player loser) {
        loser.incrementLosses();
        winner.incrementWins();
        loser.setStatus(PlayerStatus.ELIMINATED);
    }

    public void upperBracket(Player winner, Player loser) {
        loser.incrementLosses();
        winner.incrementWins();
        loser.setStatus(PlayerStatus.ELIMINATED);
    }

    private void handleFinalMatchIfNecessary(Tournament tournament) {
        logger.debug("Checking if final match is necessary for tournament: {}", tournament.getName());
        List<Player> remainingPlayers = tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());

        if (remainingPlayers.size() == 2) {
            Player upperBracketWinner = remainingPlayers.stream()
                    .filter(player -> player.getBracket() == PlayerBracket.UPPER)
                    .findFirst()
                    .orElse(null);

            Player lowerBracketWinner = remainingPlayers.stream()
                    .filter(player -> player.getBracket() == PlayerBracket.LOWER)
                    .findFirst()
                    .orElse(null);

            if (upperBracketWinner != null && lowerBracketWinner != null) {
                Round finalRound = initializeNewRound(tournament, RoundType.FINAL);
                createMatches(tournament, Arrays.asList(upperBracketWinner, lowerBracketWinner), finalRound);
                logger.debug("Final match scheduled between {} and {}",
                        upperBracketWinner.getName(), lowerBracketWinner.getName());
            }
        }
    }

    private void updateEloRatings(Match match) {
        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
        logger.debug("Elo ratings updated for match between {} and {}",
                match.getPlayer1().getName(), match.getPlayer2().getName());
    }

    private List<Pair<Player, Player>> pairPlayers(List<Player> players) {
        logger.debug("Pairing players for the round");
        List<Pair<Player, Player>> pairs = new ArrayList<>();
        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
            } else {
                pairs.add(Pair.of(players.get(i), null));
                logger.debug("Player {} gets a bye", players.get(i).getName());
            }
        }
        return pairs;
    }
}
