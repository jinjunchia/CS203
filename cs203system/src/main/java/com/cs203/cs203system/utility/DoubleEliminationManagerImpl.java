package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.*;
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
            player.setTournamentLosses(0);
            playerRepository.save(player);
            logger.debug("Player {} initialized in UPPER bracket", player.getName());
        });
        //initalze the round here
        initializeRound(tournament, players);
    }

    @Transactional
    public void initializeRound(Tournament tournament, List<Player> players ){
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


    @Override
    @Transactional
    public void createMatches(Tournament tournament, List<Player> players, Round round) {

        List<Player> upperBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.UPPER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> lowerBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());
        List<Player> upperToLowerBracketPlayers = players.stream()
                .filter(player-> player.getBracket() == PlayerBracket.UPPER_TO_LOWER && player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());

        //shuffle them
        Collections.shuffle(upperBracketPlayers);
        Collections.shuffle(lowerBracketPlayers);
        Collections.shuffle(upperToLowerBracketPlayers);        //Upper players who have fallen
        lowerBracketPlayers.addAll(upperToLowerBracketPlayers); //Adding upper players who have fallen to lower bracket

        //If left one player from both lowerBracket and UpperBracket, then combine them
        List<Match> finalMatch = null;
        if (upperBracketPlayers.size() == 1 && lowerBracketPlayers.size() == 1) {
            upperBracketPlayers.addAll(lowerBracketPlayers);
            finalMatch = createBracketMatches(tournament, upperBracketPlayers, round);
        } else {
            List<Match> upperBracketMatches = createBracketMatches(tournament, upperBracketPlayers, round);
            List<Match> lowerBracketMatches = createBracketMatches(tournament, lowerBracketPlayers, round);
        }

        //
        List<Match> upperBracketMatches = createBracketMatches(tournament, upperBracketPlayers, round);
        List<Match> lowerBracketMatches = createBracketMatches(tournament, lowerBracketPlayers, round);




        //combine all matches
        List<Match> allMatches = new ArrayList<>(upperBracketMatches);
        allMatches.addAll(lowerBracketMatches);



        //play matches immediately after creating
        playMatches(allMatches, this.getNextRoundNumber(tournament));
//        playMatches(upperBracketMatches, this.getNextRoundNumber(tournament)); //Calling playMatch function separately for each bracket
//        playMatches(lowerBracketMatches, this.getNextRoundNumber(tournament));
    }

    @Transactional
    public List<Match> createBracketMatches(Tournament tournament, List<Player> players, Round round){
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

    @Transactional
    //havent include draw
    public void playMatches(List<Match> matches, int roundNumber){
        for(Match match: matches){
            if(match.getPlayer2() != null){
                Player winner = random.nextBoolean() ? match.getPlayer1() : match.getPlayer2();
                Player loser = winner == match.getPlayer1() ? match.getPlayer2() : match.getPlayer1();
                match.setWinner(winner);
                loser.incrementTournamentLosses();

                if(winner.getBracket() == PlayerBracket.UPPER){
                    winner.setBracket(PlayerBracket.UPPER);
                }
                if(winner.getBracket() == PlayerBracket.LOWER) {
                    winner.setBracket(PlayerBracket.LOWER);
                }
                if (loser.getBracket() == PlayerBracket.UPPER_TO_LOWER && loser.getTournamentLosses() > 1) {
                    loser.setStatus(PlayerStatus.ELIMINATED);
                }
                if(loser.getBracket() == PlayerBracket.UPPER){
                    if (roundNumber == 2) { //Falling from upper
                        loser.setBracket(PlayerBracket.UPPER_TO_LOWER);
                    }
                    loser.setBracket(PlayerBracket.LOWER); //first loss
                    if (roundNumber > 1 && loser.getTournamentLosses() >= 1) {
                        loser.setStatus(PlayerStatus.ELIMINATED);
                    }
                }else{
                    //second loss
                    //if player is already in lower bracket ,increment tournament losses and then check if lost twice
                    logger.debug(loser.getName() + " has " + loser.getTournamentLosses());
                    if(loser.getTournamentLosses() == 2){
                        loser.setStatus(PlayerStatus.ELIMINATED);
                        logger.debug(loser.getName() + " has been eliminated");
                        //delete player from tournament?
                    }
                }
                logger.debug(winner + " is now in " + winner.getBracket());
                logger.debug(loser + " loser is now in " + loser.getBracket());
                //update the status of the winner and loseer
                match.setStatus(MatchStatus.COMPLETED);
                winner.incrementWins();
                loser.incrementLosses();
                updateEloRatings(match);
                matchRepository.save(match);
                playerRepository.save(winner);
                playerRepository.save(loser);
            }
        }
        processNextRound(matches.get(0).getTournament());
    }

    private void updateEloRatings(Match match) {
        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
        logger.debug("Elo ratings updated for match between {} and {}",
                match.getPlayer1().getName(), match.getPlayer2().getName());
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


    //update elo
    //settle win by bye
    //check if tournament complete
    //determineWinner
    //processthe rounds
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

    @Override
    @Transactional
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
        return players.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER && player.getStatus() == PlayerStatus.QUALIFIED)
                .findFirst()
                .orElse(null); // There should be one player left in the upper bracket
    }

    @Override
    public boolean isDoubleEliminationComplete(Tournament tournament) {
        // Fetch all players in the given tournament who are still qualified (not eliminated)
        List<Player> remainingPlayers = playerRepository.findAllByTournamentsAndStatus(tournament, PlayerStatus.QUALIFIED);

        // Count the players in each bracket
        long upperBracketPlayers = remainingPlayers.stream()
                .filter(player -> player.getBracket() == PlayerBracket.UPPER)
                .count();
        long lowerBracketPlayers = remainingPlayers.stream()
                .filter(player -> player.getBracket() == PlayerBracket.LOWER)
                .count();

        // The tournament is complete if there's only one player left in the upper bracket and no more in the lower bracket
        return upperBracketPlayers == 1 && lowerBracketPlayers == 0;
    }

}
