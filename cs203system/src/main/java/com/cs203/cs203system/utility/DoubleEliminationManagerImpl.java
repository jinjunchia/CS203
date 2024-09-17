package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.RoundType;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.RoundRepository;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.service.EloService;
import jakarta.transaction.Transactional;
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
    private PlayerRepository playerRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private EloService eloService;

    private final Random random = new Random();


    private List<Player> getPlayersForTournament(Tournament tournament) {

        return playerRepository.findByTournamentId(tournament.getId());
    }
    @Override
    @Transactional
    public void initializeDoubleElimination(Tournament tournament, List<Player> players) {
        if (tournament.getFormat() == TournamentFormat.HYBRID) {
            Collections.shuffle(players);
            Round upperRound = initializeRound(tournament, 1, RoundType.UPPER);
            createMatches(tournament, players, upperRound);
        } else if (tournament.getFormat() == TournamentFormat.DOUBLE_ELIMINATION) {
            List<Player> eligiblePlayers = getEligiblePlayers(tournament, getPlayersForTournament(tournament));
            Collections.shuffle(eligiblePlayers);
            Round upperRound = initializeRound(tournament, 1, RoundType.UPPER);
            createMatches(tournament, eligiblePlayers, upperRound);
        }
    }


    private List<Player> getEligiblePlayers(Tournament tournament, List<Player> players) {
        return players.stream()
                .filter(player -> player.getEloRating() >= tournament.getMinEloRating() &&
                        player.getEloRating() <= tournament.getMaxEloRating())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Round initializeRound(Tournament tournament, int roundNumber, RoundType roundType) {
        Round round = new Round();
        round.setRoundNumber(roundNumber);
        round.setRoundType(roundType);
        round.setTournament(tournament);
        return roundRepository.save(round);
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
                int player1Score = random.nextInt(10);
                int player2Score = random.nextInt(10);

                Match match = Match.builder()
                        .player1(pair.getFirst())
                        .player2(pair.getSecond())
                        .tournament(tournament)
                        .round(round)
                        .player1Score(player1Score)
                        .player2Score(player2Score)
                        .status(MatchStatus.SCHEDULED)
                        .build();

                matchRepository.save(match);
                matches.add(match);
            } else {
                Player playerWithBye = pair.getFirst();
                playerWithBye.addPoints(1.0);
                playerRepository.save(playerWithBye);
            }
        }
        return matches;
    }

    @Override
    @Transactional
    public void updateStandings(Tournament tournament) {
        List<Match> matches = matchRepository.findByTournament(tournament);

        for (Match match : matches) {
            if (match.getStatus() == MatchStatus.SCHEDULED) {
                Player winner = match.getWinner();
                Player loser = match.getLoser();
                if (winner != null) {
                    winner.incrementWins();
                }
                if (loser != null) {
                    loser.incrementLosses();
                }
                if (match.isDraw()) {
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
                .count();
        return remainingPlayers <= 1;
    }

    @Override
    public Player determineWinner(Tournament tournament) {
        return tournament.getPlayers().stream()
                .filter(player -> player.getStatus() != PlayerStatus.ELIMINATED)
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void updatePlayerBracket(Player winner, Player loser) {
        if (winner != null) {
            playerRepository.save(winner);
        }
        if(loser != null){
            if (loser.getBracket() == PlayerBracket.UPPER) {
                loser.setBracket(PlayerBracket.LOWER);
            } else {
                loser.incrementLosses();
                if (loser.hasLostTwice()) {
                    loser.setStatus(PlayerStatus.ELIMINATED);
                }
            }
            playerRepository.save(loser);
        }
    }

    private void updateEloRatings(Match match) {
        if (match.getPlayer1() != null && match.getPlayer2() != null) {
            eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
        }
    }

    private List<Pair<Player, Player>> pairPlayers(List<Player> players) {
        List<Pair<Player, Player>> pairs = new ArrayList<>();
        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                pairs.add(Pair.of(players.get(i), players.get(i + 1)));
            }
        }
        return pairs;
    }
}
