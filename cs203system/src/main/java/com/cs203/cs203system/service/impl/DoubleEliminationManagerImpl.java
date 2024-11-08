package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.DoubleEliminationManager;
import com.cs203.cs203system.service.EloService;
import com.cs203.cs203system.service.TournamentFormatManager;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the DoubleEliminationManager interface.
 * Responsible for managing the double-elimination tournament process, including initialization,
 * receiving match results, and determining winners.
 */

@Builder
@Service
public class DoubleEliminationManagerImpl implements TournamentFormatManager {

    private final TournamentRepository tournamentRepository;
    private final EloService eloService;

    /**
     * Constructor for DoubleEliminationManagerImpl.
     *
     * @param tournamentRepository the repository used to manage Tournament data.
     */
    @Autowired
    public DoubleEliminationManagerImpl(TournamentRepository tournamentRepository, EloService eloService) {
        this.tournamentRepository = tournamentRepository;
        this.eloService = eloService;
    }

    /**
     * Initializes the double-elimination tournament by pairing players and setting initial matches.
     * All players will start in the winner's bracket.
     *
     * @param tournament the tournament to be initialized.
     * @return the initialized tournament with players assigned to matches.
     */
    @Override
    @Transactional
    public Tournament initializeTournament(Tournament tournament){
        tournament.getPlayers().forEach(player -> tournament.getWinnersBracket().add(player));
        Collections.shuffle(tournament.getWinnersBracket());
        createIntitalMatches(tournament);
        return tournamentRepository.save(tournament);
    }

//    public Tournament initializeDoubleElimination(Tournament tournament) {
//        tournament.getPlayers().forEach(player -> tournament.getWinnersBracket().add(player));
//        Collections.shuffle(tournament.getWinnersBracket());
//        createIntitalMatches(tournament);
//        return tournamentRepository.save(tournament);
////        for (int i = 1; i < tournament.getWinnersBracket().size(); i += 2) {
////            Match match = Match.builder()
////                    .tournament(tournament)
////                    .bracket(MatchBracket.UPPER)
////                    .matchDate(LocalDateTime.now())
////                    .status(MatchStatus.SCHEDULED)
////                    .player1(tournament.getWinnersBracket().get(i))
////                    .player2(tournament.getWinnersBracket().get(i - 1))
////                    .build();
////            tournament.getMatches().add(match);
////        }
////
////        return tournamentRepository.save(tournament);
//    }

    private void createIntitalMatches(Tournament tournament){
        for(int i = 1; i < tournament.getWinnersBracket().size(); i += 2){
            tournament.getMatches().add(buildMatch(tournament,MatchBracket.UPPER, tournament.getWinnersBracket().get(i-1),tournament.getWinnersBracket().get(i)));
        }
    }

    private void handleLoserMovement(Match match, Tournament tournament) {
        Player loser = match.getLoser();

        if (match.getBracket() == MatchBracket.UPPER) {
            tournament.getWinnersBracket().remove(loser);
            tournament.getLosersBracket().add(loser);
        } else if (match.getBracket() == MatchBracket.LOWER ||
                (match.getBracket() == MatchBracket.FINAL && !tournament.getLosersBracket().contains(loser))) {
            tournament.getLosersBracket().remove(loser);
        } else if ((match.getBracket() == MatchBracket.FINAL && !tournament.getLosersBracket().contains(loser))) {
            tournament.getWinnersBracket().remove(loser);
            tournament.getLosersBracket().add(loser);
        } else if ((match.getBracket() == MatchBracket.FINAL && tournament.getLosersBracket().contains(loser))
                || match.getBracket() == MatchBracket.GRAND_FINAL) {
            tournament.getWinnersBracket().clear();
            tournament.getLosersBracket().clear();
            tournament.setStatus(TournamentStatus.COMPLETED);
        }
    }



    private Match buildMatch(Tournament tournament, MatchBracket bracket, Player player1, Player player2) {
        return Match.builder()
                .tournament(tournament)
                .bracket(bracket)
                .matchDate(LocalDateTime.now())
                .status(MatchStatus.SCHEDULED)
                .player1(player1)
                .player2(player2)
                .build();
    }

    private boolean areAllMatchesCompleted(Tournament tournament){
        return tournament.getMatches().stream().allMatch(m->m.getStatus().equals(MatchStatus.COMPLETED));
    }

    private boolean shouldCreateFinalMatches(Tournament tournament){
        int totalPlayers = tournament.getWinnersBracket().size() + tournament.getLosersBracket().size();
        return totalPlayers <= 2;
    }

    private void createFinalMatches(Tournament tournament){
        if (tournament.getLosersBracket().size() == 2) {
            tournament.getMatches().add(buildMatch(tournament, MatchBracket.GRAND_FINAL,
                    tournament.getLosersBracket().get(0), tournament.getLosersBracket().get(1)));
        } else if (tournament.getLosersBracket().size() == 1 && tournament.getWinnersBracket().size() == 1) {
            tournament.getMatches().add(buildMatch(tournament, MatchBracket.FINAL,
                    tournament.getLosersBracket().get(0), tournament.getWinnersBracket().get(0)));
        }
    }

    private void createRegularMatches(Tournament tournament){
        createBracketMatches(tournament,tournament.getWinnersBracket(),MatchBracket.UPPER);
        createBracketMatches(tournament,tournament.getLosersBracket(),MatchBracket.LOWER);
    }
    private void createBracketMatches(Tournament tournament, List<Player> bracket, MatchBracket matchBracket) {
        for (int i = 1; i < bracket.size(); i += 2) {
            tournament.getMatches().add(buildMatch(tournament, matchBracket, bracket.get(i - 1), bracket.get(i)));
        }
    }

    /**
     * Records the result of a match and updates the tournament brackets accordingly.
     * If all matches are completed, proceeds to the next round of matchmaking.
     *
     * @param match the match whose result is being recorded.
     * @return the updated tournament.
     */
    @Override
    @Transactional

    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = match.getTournament();
        handleLoserMovement(match, tournament);
        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);

        if (!areAllMatchesCompleted(tournament)) {
            return tournamentRepository.save(tournament);
        }

        if (shouldCreateFinalMatches(tournament)) {
            createFinalMatches(tournament);
        } else {
            createRegularMatches(tournament);
        }

        return tournamentRepository.save(tournament);
    }

//    public Tournament receiveMatchResult(Match match) {
//
//        Player loser = match.getLoser();
//        Tournament tournament = match.getTournament();
//
//        // Move all the winners and losers the correct bracket
//        // If the loser is already in the loser bracket, he will get kicked
//        if (match.getBracket() == MatchBracket.UPPER) {
//            tournament.getWinnersBracket().remove(loser);
//            tournament.getLosersBracket().add(loser);
//
//        } else if (match.getBracket() == MatchBracket.LOWER) {
//            tournament.getLosersBracket().remove(loser);
//
//        } else if (match.getBracket() == MatchBracket.FINAL
//                && !tournament.getLosersBracket().contains(loser)) {
//            tournament.getWinnersBracket().remove(loser);
//            tournament.getLosersBracket().add(loser);
//
//        } else if ((match.getBracket() == MatchBracket.FINAL
//                && tournament.getLosersBracket().contains(loser))
//                || match.getBracket() == MatchBracket.GRAND_FINAL) {
//            tournament.getWinnersBracket().clear();
//            tournament.getLosersBracket().clear();
//            tournament.setStatus(TournamentStatus.COMPLETED);
//            return tournamentRepository.save(tournament);
//
//        }
//
//        // Update the stats here
//        eloService.updateEloRatings(match.getPlayer1(), match.getPlayer2(), match);
//
//        // Do the checking
//        boolean isAllMatchCompleted = match.getTournament().getMatches()
//                .stream()
//                .allMatch(m -> m.getStatus().equals(MatchStatus.COMPLETED));
//
//        if (!isAllMatchCompleted) {
//            return tournamentRepository.save(tournament);
//        }
//
//        // This will happen after match have been saved and there is only 1 player left in
//        // each bracket. This means that we can now start final and grand final match (if any)
//        if (tournament.getWinnersBracket().size() + tournament.getLosersBracket().size() <= 2) {
//            if (tournament.getLosersBracket().size() == 2) {
//                Match newMatch = Match.builder()
//                        .tournament(tournament)
//                        .bracket(MatchBracket.GRAND_FINAL)
//                        .matchDate(LocalDateTime.now())
//                        .status(MatchStatus.SCHEDULED)
//                        .player1(tournament.getLosersBracket().get(0))
//                        .player2(tournament.getLosersBracket().get(1))
//                        .build();
//                tournament.getMatches().add(newMatch);
//                return tournamentRepository.save(tournament);
//            }
//
//            if (tournament.getLosersBracket().size() == 1 && tournament.getWinnersBracket().size() == 1) {
//                Match newMatch = Match.builder()
//                        .tournament(tournament)
//                        .bracket(MatchBracket.FINAL)
//                        .matchDate(LocalDateTime.now())
//                        .status(MatchStatus.SCHEDULED)
//                        .player1(tournament.getLosersBracket().get(0))
//                        .player2(tournament.getWinnersBracket().get(0))
//                        .build();
//                tournament.getMatches().add(newMatch);
//                return tournamentRepository.save(tournament);
//            }
//
//            return tournamentRepository.save(tournament);
//        }
//
//
//        // Time to do usual match making
//        for (int i = 1; i < match.getTournament().getWinnersBracket().size(); i += 2) {
//            Match newMatch = Match.builder()
//                    .tournament(tournament)
//                    .bracket(MatchBracket.UPPER)
//                    .matchDate(LocalDateTime.now())
//                    .status(MatchStatus.SCHEDULED)
//                    .player1(tournament.getWinnersBracket().get(i - 1))
//                    .player2(tournament.getWinnersBracket().get(i))
//                    .build();
//            tournament.getMatches().add(newMatch);
//        }
//
//        for (int i = 1; i < match.getTournament().getLosersBracket().size(); i += 2) {
//            Match newMatch = Match.builder()
//                    .tournament(tournament)
//                    .bracket(MatchBracket.LOWER)
//                    .matchDate(LocalDateTime.now())
//                    .status(MatchStatus.SCHEDULED)
//                    .player1(tournament.getLosersBracket().get(i - 1))
//                    .player2(tournament.getLosersBracket().get(i))
//                    .build();
//            tournament.getMatches().add(newMatch);
//        }
//
//        return tournamentRepository.save(tournament);
//
//    }

    /**
     * Determines the winner of a completed tournament.
     *
     * @param tournament the completed tournament.
     * @return the winner of the tournament.
     * @throws IllegalStateException if the tournament is not completed or if no winner is found.
     */
//    public Player determineWinner(Tournament tournament) {
//        if (!tournament.getStatus().equals(TournamentStatus.COMPLETED)) {
//            throw new IllegalStateException("Tournament is not completed. Winner cannot be determined.");
//        }
//
//        return tournament.getMatches()
//                .stream()
//                .reduce((match1, match2) -> match1.getId() > match2.getId() ? match1 : match2)
//                .orElseThrow(() -> new IllegalStateException("There seems to be no winner"))
//                .getWinner();
//    }

    public Player determineWinner(Tournament tournament){
        if(!tournament.getStatus().equals(TournamentStatus.COMPLETED)){
            throw new IllegalStateException("Tournament is not completed. Winner cannot be determined.");
        }
        return tournament.getMatches().stream()
                .reduce((match1,match2) -> match1.getId() > match2.getId() ? match1 : match2)
                .orElseThrow(()->new IllegalStateException("There seems to be no winner"))
                .getWinner();
    }
}



