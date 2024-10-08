package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.*;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.RoundRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.EloService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

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
    @Autowired
    private TournamentRepository tournamentRepository;

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
            player.setTournamentLosses(0);
            playerRepository.save(player);
            logger.debug("Player {} initialized in UPPER bracket", player.getName());
        });
        //initalze the round here
        initializeRound(tournament, players);
    }

    @Transactional
    public void initializeRound(Tournament tournament, List<Player> players) {
        Round round = new Round();
        round.setTournament(tournament);
        round.setRoundType(RoundType.DOUBLE_ELIMINATION); // Ensure this is set properly
        int roundNumber = getNextRoundNumber(tournament);
        logger.debug("Currently at round " + roundNumber);
        round.setRoundNumber(roundNumber);
        roundRepository.save(round);

        // Create matches for this round
        createMatches(tournament, players, round);
        logger.debug("Saved round: " + round);
    }

    @Transactional
    public int getNextRoundNumber(Tournament tournament) {
        long tournamentId = tournament.getId();
        logger.debug("Fetching last round for tournament ID: " + tournamentId);
        RoundType roundType = RoundType.DOUBLE_ELIMINATION;
        Optional<Round> lastRound = roundRepository.findTopByMatches_Tournament_IdAndRoundTypeOrderByRoundNumberDesc(tournamentId, roundType);
        logger.debug("Last round found: " + lastRound);
        return lastRound.map(round -> {
            logger.debug("Found round number: " + round.getRoundNumber());
            return round.getRoundNumber() + 1;
        }).orElse(1);
    }

    public void createMatches(Tournament tournament, List<Player> players, Round round) {
        boolean isFinal = false;
        int round_number = getNextRoundNumber(tournament);
        logger.debug("Round number {}", round_number);
        List<List<Player>> allPlayers = splitMatch(players); //First element is always upper players, second element is always lower players
        List<Player> upperBracketPlayers = allPlayers.get(0);
        List<Player> lowerBracketPlayers = allPlayers.get(1);
        List<Player> upperToLowerBracketPlayers = new ArrayList<>();

        if (allPlayers.get(2).isEmpty() == false) {
            upperToLowerBracketPlayers = allPlayers.get(2);
        }

        List<Match> upperBracketMatch = new ArrayList<>();
        List<Match> lowerBracketMatch = new ArrayList<>();

        List<Match> existingMatch = tournament.getMatches();

        if (round_number == 1) {
            upperBracketMatch = createBracketMatches(tournament, upperBracketPlayers, round);
            playMatches(upperBracketMatch, round_number, isFinal);
        } else if (round_number > 1) {
            if (lowerBracketPlayers.size() % 2 == 0 && (upperToLowerBracketPlayers.size() % 2 == 1 || upperBracketPlayers.size() % 2 == 1)) {
                lowerBracketMatch = createBracketMatches(tournament, lowerBracketPlayers, round);
                lowerBracketMatch = playMatches(lowerBracketMatch, round_number, isFinal);
            } else if (lowerBracketPlayers.size() % 2 == 1 && upperToLowerBracketPlayers.size() % 2 == 1) {
                setAllToLower(upperToLowerBracketPlayers);
                lowerBracketPlayers.addAll(upperToLowerBracketPlayers);
                lowerBracketMatch = createBracketMatches(tournament, lowerBracketPlayers, round);
                lowerBracketMatch = playMatches(lowerBracketMatch, round_number, isFinal);
            } else if (upperBracketPlayers.size() == 1 && lowerBracketPlayers.size() == 1 && upperToLowerBracketPlayers.size() == 0) {
                isFinal = true;
                upperBracketPlayers.addAll(lowerBracketPlayers);
                upperBracketMatch = createBracketMatches(tournament, upperBracketPlayers, round);
                upperBracketMatch = playMatches(upperBracketMatch, round_number, isFinal);
            } else if (upperBracketPlayers.size() == 1 && lowerBracketPlayers.size() == 0 && upperToLowerBracketPlayers.size() == 1) {
                isFinal = true;
                upperBracketPlayers.addAll(upperToLowerBracketPlayers);
                upperBracketMatch = createBracketMatches(tournament, upperBracketPlayers, round);
                upperBracketMatch = playMatches(upperBracketMatch, round_number, isFinal);
            } else {
                setAllToLower(upperToLowerBracketPlayers);
                lowerBracketPlayers.addAll(upperToLowerBracketPlayers);
                lowerBracketMatch = createBracketMatches(tournament, lowerBracketPlayers, round);
                upperBracketMatch = createBracketMatches(tournament, upperBracketPlayers, round);
                upperBracketMatch = playMatches(upperBracketMatch, round_number, isFinal);
                lowerBracketMatch = playMatches(lowerBracketMatch, round_number, isFinal);
            }
        }
        if (upperBracketMatch.isEmpty() == false) {
            logger.error("Upperbracketmatch data:" + upperBracketMatch);
            existingMatch.addAll(upperBracketMatch);
        }
        if (lowerBracketMatch.isEmpty() == false) {
            logger.error("Upperbracketmatch data:" + lowerBracketMatch);
            existingMatch.addAll(lowerBracketMatch);
        }
        tournament.setMatches(existingMatch);
        processNextRound(tournament);
    }

    @Transactional
    public void processNextRound(Tournament tournament) {
        //fetch all remaining players
        List<Player> remainingPlayers = playerRepository.findAllByTournamentsAndStatus(tournament, PlayerStatus.QUALIFIED);
        if (isDoubleEliminationComplete(tournament)) {
            Player winner = determineTournamentWinner(remainingPlayers);
            logger.info("Tournament {} is complete. The winner is {}", tournament.getName(), winner.getName());
            return;
        }
        // Initialize the next round
        initializeRound(tournament, remainingPlayers);
    }

    public List<List<Player>> splitMatch(List<Player> players) {
        List<List<Player>> out = new ArrayList<>();

        List<Player> upperBracketPlayers = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> lowerBracketPlayers = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> upperLowerBracketPlayers = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER_TO_LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());

        Collections.shuffle(upperBracketPlayers);
        Collections.shuffle(lowerBracketPlayers);
        Collections.shuffle(upperLowerBracketPlayers);

        out.add(upperBracketPlayers);
        out.add(lowerBracketPlayers);
        out.add(upperLowerBracketPlayers);

        return out;
    }

    @Override
    public List<Match> createBracketMatches(Tournament tournament, List<Player> players, Round round) {
        logger.debug("{} getting pairs", players);
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


                matches.add(match);
                matchRepository.save(match);
                logger.debug("Scheduled match between {} and {} in round {}",
                        pair.getFirst().getName(), pair.getSecond().getName(), round.getRoundNumber());
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.incrementWins();  // Bye counts as a win
                playerRepository.save(playerWithBye);
                logger.debug("Player {} gets a bye in round {}", playerWithBye.getName(), round.getRoundNumber());
            }
        }
//        List<Match> existingMatches = tournament.getMatches();
//        existingMatches.addAll(matches);
//        tournament.setMatches(existingMatches);
        return matches;
    }

    @Transactional
    //havent include draw
    public List<Match> playMatches(List<Match> Match, int roundNumber, boolean isFinal) {
        List<Match> output = new ArrayList<>();
        for (Match match : Match) {
            logger.debug("List of matches: " + match.getId());
            Player winner = random.nextBoolean() ? match.getPlayer1() : match.getPlayer2();
            Player loser = winner == match.getPlayer1() ? match.getPlayer2() : match.getPlayer1();
            match.setWinner(winner);
            logger.debug("{} is the winner, {} is the loser", winner.getName(), loser.getName());
            logger.error("{} is the winner of {}", match.getWinner().getName(), match.getId());
            loser.incrementTournamentLosses();
            if (isFinal) {
                loser.setStatus(PlayerStatus.ELIMINATED);
            }
            if (winner.getBracket() == PlayerBracket.UPPER) {
                winner.setBracket(PlayerBracket.UPPER);
            }
            if (winner.getBracket() == PlayerBracket.LOWER) {
                winner.setBracket(PlayerBracket.LOWER);
            }
            if (loser.getBracket() == PlayerBracket.UPPER) {
                if (roundNumber > 1) {
                    loser.setBracket(PlayerBracket.UPPER_TO_LOWER);
                } else {
                    loser.setBracket(PlayerBracket.LOWER);
                }
            } else {
                //second loss
                if (loser.getTournamentLosses() >= 2) {
                    loser.setStatus(PlayerStatus.ELIMINATED);
                    logger.debug(loser.getName() + " has been eliminated");
                    //delete player from tournament?
                }
            }
            match.setStatus(MatchStatus.COMPLETED);
            winner.incrementWins();
            loser.incrementLosses();
            updateEloRatings(match);
            matchRepository.save(match);
            playerRepository.save(winner);
            playerRepository.save(loser);
            output.add(match);
        }
        return output;
    }

    @Transactional
    //havent include draw
    public void ReceiveResult(List<Match> Match, int roundNumber, boolean isFinal) {

        for (Match match : Match) {
            logger.debug("List of matches: " + match.getId());
            Player winner = random.nextBoolean() ? match.getPlayer1() : match.getPlayer2();
            Player loser = winner == match.getPlayer1() ? match.getPlayer2() : match.getPlayer1();
            logger.debug("{} is the winner, {} is the loser", winner.getName(), loser.getName());
            match.setWinner(winner);
            loser.incrementTournamentLosses();
            if (isFinal) {
                loser.setStatus(PlayerStatus.ELIMINATED);
            }
            if (winner.getBracket() == PlayerBracket.UPPER) {
                winner.setBracket(PlayerBracket.UPPER);
            }
            if (winner.getBracket() == PlayerBracket.LOWER) {
                winner.setBracket(PlayerBracket.LOWER);
            }
            if (loser.getBracket() == PlayerBracket.UPPER) {
                if (roundNumber > 1) {
                    loser.setBracket(PlayerBracket.UPPER_TO_LOWER);
                } else {
                    loser.setBracket(PlayerBracket.LOWER);
                }
            } else {
                //second loss
                if (loser.getTournamentLosses() >= 2) {
                    loser.setStatus(PlayerStatus.ELIMINATED);
                    logger.debug(loser.getName() + " has been eliminated");
                    //delete player from tournament?
                }

                logger.debug("{} is status:{}", winner.getName(), winner.getBracket());
                logger.debug("{} is status:{}", loser.getName(), loser.getBracket());
                //update the status of the winner and loser
                match.setStatus(MatchStatus.COMPLETED);
                winner.incrementWins();
                loser.incrementLosses();
                updateEloRatings(match);
                matchRepository.save(match);
                playerRepository.save(winner);
                playerRepository.save(loser);
            }
        }
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

    private void updateEloRatings(Match match) {
        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
        logger.debug("Elo ratings updated for match between {} and {}",
                match.getPlayer1().getName(), match.getPlayer2().getName());
    }

    @Override
    public Player determineWinner(Tournament tournament) {
        // Fetch all remaining players who are still in the tournament
        List<Player> remainingPlayers = playerRepository.findAllByTournamentsAndStatus(tournament, PlayerStatus.QUALIFIED);

        // Check if the tournament is complete (only one player remains in the upper bracket)
        if (isDoubleEliminationComplete(tournament)) {
            return determineTournamentWinner(remainingPlayers);
        }

        return null;  // Tournament is not complete yet
    }


    public Player determineTournamentWinner(List<Player> players) {
        long upperBracketCount = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER && player.getStatus() == PlayerStatus.QUALIFIED)
                .count();

        long lowerBracketCount = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.LOWER && player.getStatus() == PlayerStatus.QUALIFIED)
                .count();

        long upperlowerBracketCount = players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER_TO_LOWER && player.getStatus() == PlayerStatus.QUALIFIED)
                .count();

        // Check if exactly one player is left in one of the brackets and others are empty
        if (upperBracketCount == 1 && lowerBracketCount == 0 && upperlowerBracketCount == 0) {
            return players.stream()
                    .filter(player -> player.getBracket() == PlayerBracket.UPPER && player.getStatus() == PlayerStatus.QUALIFIED)
                    .findFirst()
                    .orElse(null);
        } else if (lowerBracketCount == 1 && upperBracketCount == 0 && upperlowerBracketCount == 0) {
            return players.stream()
                    .filter(player -> player.getBracket() == PlayerBracket.LOWER && player.getStatus() == PlayerStatus.QUALIFIED)
                    .findFirst()
                    .orElse(null);
        }
        return null; // If no valid condition is met, return null
    }

    @Override
    public boolean isDoubleEliminationComplete(Tournament tournament) {
        // Fetch all players in the given tournament who are still qualified (not eliminated)
        List<Player> remainingPlayers = playerRepository.findAllByTournamentsAndStatus(tournament, PlayerStatus.QUALIFIED);
        logger.debug("Players remaining:{}", remainingPlayers);

        // Count the players in each bracket
        long upperBracketPlayers = remainingPlayers.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER)
                .count();
        long lowerBracketPlayers = remainingPlayers.stream()
                .filter(player -> player.getBracket() == PlayerBracket.LOWER)
                .count();
        long upperLowerBracketPlayers = remainingPlayers.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER_TO_LOWER)
                .count();
        logger.debug("{} players left in upper, {} players left in lower, {} players left in upperlower", upperBracketPlayers, lowerBracketPlayers, upperLowerBracketPlayers);
        // The tournament is complete if there's only one player left in the upper bracket and no more in the lower bracket
        return upperBracketPlayers + lowerBracketPlayers + upperLowerBracketPlayers == 1;
    }

    public void setAllToLower(List<Player> players) {
        for (Player P1 : players) {
            P1.setBracket(PlayerBracket.LOWER);
        }
    }

    public void setAllToHigher(List<Player> players) {
        for (Player P1 : players) {
            P1.setBracket(PlayerBracket.UPPER);
        }
    }
}

