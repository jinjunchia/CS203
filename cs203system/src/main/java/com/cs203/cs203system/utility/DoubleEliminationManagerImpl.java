package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.service.EloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DoubleEliminationManagerImpl implements DoubleEliminationManager {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository; // Add PlayerRepository to save player updates

    @Autowired
    private EloService eloService;

    private final Random random = new Random();


    public List<Player> getPlayersForTournament(Tournament tournament) {
        return playerRepository.findByTournamentId(tournament.getId());
    }
    @Override
    public void initializeDoubleElimination(Tournament tournament, List<Player> topPlayers) {
        // when i initialize i should check for the players  max and min elo rating and i should also initialize a normal double elimination
        // initialize the double elimination bracket with top players
        // this method sets up the first round of matches
        if (tournament.getFormat() == TournamentFormat.HYBRID) {
            // If Hybrid, assume topPlayers have already been filtered by Swiss phase
            Collections.shuffle(topPlayers); // Shuffle for random initial pairings
            createMatches(tournament, topPlayers);
        } else if (tournament.getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            // For standard Double Elimination, filter players by ELO ratings
            List<Player> eligiblePlayers = getEligiblePlayers(tournament, getPlayersForTournament(tournament));
            Collections.shuffle(eligiblePlayers); // Shuffle for random initial pairings
            createMatches(tournament, eligiblePlayers);
        }
    }
    private List<Player> getEligiblePlayers(Tournament tournament, List<Player> players) {
        // Filter players based on tournament ELO restrictions this is usually for normal double eliminate
        return players.stream()
                .filter(player -> player.getEloRating() >= tournament.getMinEloRating() &&
                        player.getEloRating() <= tournament.getMaxEloRating())
                .collect(Collectors.toList());
    }

    public List<Match> createMatches(Tournament tournament, List<Player> players) {
        // filter players
        players = players.stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .collect(Collectors.toList());

        // shuffle players for random matchups
        Collections.shuffle(players);
        List<Pair<Player, Player>> pairs = pairPlayers(players);
        List<Match> matches = new ArrayList<>();

        // create matches from pairs
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getSecond() != null) {
                // simulate match outcome by assigning random scores
                int player1Score = random.nextInt(10);
                int player2Score = random.nextInt(10);

                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .roundNumber(tournament.getCurrentRoundNumber())
                        .player1Score(player1Score)
                        .player2Score(player2Score)
                        .status(MatchStatus.SCHEDULED) // set to SCHEDULED initially, update after simulation
                        .build();

                matchRepository.save(match);
                matches.add(match);
            } else {
                // handle the case of an odd player (bye scenario), not sure if this is necessary but scared got odd number suddendly come out might need to change this
                Player playerWithBye = pair.getFirst();
                playerWithBye.addPoints(1.0); // award points for the bye
                playerRepository.save(playerWithBye); // save changes to the player's points, same as the bottom might need to change to intemediary table
            }
        }
        return matches;
    }

    @Override
    public void updateStandings(Tournament tournament) {
        // Update standings after matches are completed
        List<Match> matches = matchRepository.findByTournament(tournament);

        for (Match match : matches) {
            if (match.getStatus() == MatchStatus.SCHEDULED) {

                Player winner = match.getWinner();
                Player loser = match.getLoser();
                if(winner != null){
                    winner.incrementWins();
                }
                if(loser != null){
                    loser.incrementLosses();
                }
                if(match.isDraw()){
                    match.getPlayer1().addPoints(0.5);
                    match.getPlayer2().addPoints(0.5);
                    match.getPlayer1().incrementDraws();
                    match.getPlayer2().incrementDraws();
                }

                updatePlayerBracket(winner, loser);
                updateEloRatings(match);
                match.setStatus(MatchStatus.COMPLETED);
                matchRepository.save(match);
            }
        }
    }

    @Override
    public boolean isDoubleEliminationComplete(Tournament tournament) {
        long remainingPlayers = tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .count(); // this stream, streams the player and get their status
        return remainingPlayers <= 1; // complete when 1 remains
    }

    @Override
    public Player determineWinner(Tournament tournament) {
        return tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .findFirst()
                .orElse(null); // return the last standing player as the winner
    }

    private void updatePlayerBracket(Player winner, Player loser) {
        // update brackets and statuses for winner and loser
        if(winner != null){
            playerRepository.save(winner); // this might need to change since there might be a intemediary table , matchParticipation need to discuss this further
        }
        if (loser.getBracket() == PlayerBracket.UPPER) {
            loser.setBracket(PlayerBracket.LOWER); // Move loser to lower bracket
        } else {
            loser.incrementLosses();
            if (loser.hasLostTwice()) {
                loser.setStatus(PlayerStatus.ELIMINATED); // Eliminate after second loss
            }
        }
        playerRepository.save(loser); // this might need to change since there might be a intemediary table , matchParticipation need to discuss this further
    }

    private void updateEloRatings(Match match) {
        // update ELO ratings using EloService
        if (match.getPlayer1() != null && match.getPlayer2() != null) {
            eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
        }
    }

    private List<Pair<Player, Player>> pairPlayers(List<Player> players) {
        // pair players for matches
        List<Pair<Player, Player>> pairs = new ArrayList<>();
        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
            }
        }
        return pairs;
    }
}
