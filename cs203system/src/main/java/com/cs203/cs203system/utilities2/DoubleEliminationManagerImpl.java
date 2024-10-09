package com.cs203.cs203system.utilities2;

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
import com.cs203.cs203system.utilities2.DoubleEliminationManager;
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
    public void initializeDoubleElimination(Tournament tournament) {
        // Ensure the tournament format is suitable for Double Elimination
        if (tournament.getFormat() != TournamentFormat.DOUBLE_ELIMINATION
                && tournament.getFormat() != TournamentFormat.HYBRID) {
            logger.warn("Tournament format is not Double Elimination or Hybrid. Skipping initialization.");
            return;
        }

        if (tournament.getPlayers().isEmpty()) {
            logger.warn("No eligible players found for tournament: {}", tournament.getName());
            return;
        }

        Collections.shuffle(tournament.getPlayers());
        tournament.getPlayers().forEach(player -> {
            player.setBracket(PlayerBracket.UPPER);
            player.setStatus(PlayerStatus.QUALIFIED);
            player.setTournamentLosses(0);
            playerRepository.save(player);
            logger.debug("Player {} initialized in UPPER bracket", player.getName());
        });
        tournamentRepository.save(tournament);
    }

    @Transactional
    public Round initializeRound(Tournament tournament, List<Player> players ){
        Round round = new Round();
        round.setTournament(tournament);
        round.setRoundType(RoundType.DOUBLE_ELIMINATION); // Ensure this is set properly
        int roundNumber = getNextRoundNumber(tournament);
        logger.debug("Currently at round " + roundNumber);
        round.setRoundNumber(roundNumber);
        roundRepository.save(round);

        // Create matches for this round
//        createMatches(tournament, players, round);
        logger.debug("Saved round: " + round);
    }

    @Transactional
    public int getNextRoundNumber(Tournament tournament){
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



    @Transactional
    public void processNextRound(Tournament tournament){
        //fetch all remaining players
        List<Player> remainingPlayers = playerRepository.findAllByTournamentsAndStatus(tournament,PlayerStatus.QUALIFIED);
        if (isDoubleEliminationComplete(tournament)) {
            Player winner = determineTournamentWinner(remainingPlayers);
            logger.info("Tournament {} is complete. The winner is {}", tournament.getName(), winner.getName());
            return;
        }
        // Initialize the next round
        initializeRound(tournament, remainingPlayers);
    }

    public List<List<Player>> splitMatch (List<Player> players) {
        List<List<Player>> out = new ArrayList<>();

        List<Player> upperBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.UPPER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> lowerBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> upperLowerBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.UPPER_TO_LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
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
    public Tournament createBracketMatches (Tournament tournament, Round round) {

        List<Pair<Player, Player>> pairs = pairPlayers(tournament.getPlayers());
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
        tournament.setMatches(matches);
        return tournament;
    }

    @Transactional
    //havent include draw
    public List<Match> receiveResult(Tournament tournament, int roundNumber, boolean isFinal){
        List<Match> output = new ArrayList<>();
        List<Match> matches = tournament.getMatches();
        for(Match match: matches){

            Player winner = match.getWinner();
            Player loser = match.getLoser();

            winner.incrementWins();
            loser.incrementLosses();

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
            } else{
                //second loss
                if (loser.getTournamentLosses() >= 2){
                    loser.setStatus(PlayerStatus.ELIMINATED);
                    logger.debug(loser.getName() + " has been eliminated");
                    //delete player from tournament?
                }
            }
            match.setStatus(MatchStatus.COMPLETED);
            updateEloRatings(match);
            matchRepository.save(match);
            playerRepository.save(winner);
            playerRepository.save(loser);
            output.add(match);
        }
        tournamentRepository.save(tournament);
        return output;
    }

    public List<Pair<Player, Player>> pairPlayers(List<Player> players) {
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
//
//    public void setAllToLower (List<Player> players) {
//        for (Player P1:players) {
//            P1.setBracket(PlayerBracket.LOWER);
//        }
//    }
//
//    public void setAllToHigher (List<Player> players) {
//        for (Player P1:players) {
//            P1.setBracket(PlayerBracket.UPPER);
//        }
//    }
}

