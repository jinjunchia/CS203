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

    /**
     * Creates initial matches for the players in the winner's bracket.
     *
     * @param tournament the tournament where matches will be created.
     */
    private void createIntitalMatches(Tournament tournament){
        for(int i = 1; i < tournament.getWinnersBracket().size(); i += 2){
            tournament.getMatches().add(buildMatch(tournament,MatchBracket.UPPER, tournament.getWinnersBracket().get(i-1),tournament.getWinnersBracket().get(i)));
        }
    }

    /**
     * Handles the movement of the loser to the correct bracket after a match result.
     * Moves players between winner and loser brackets based on the match outcome.
     *
     * @param match the match containing the losing player.
     * @param tournament the tournament being updated.
     */
    private void handleLoserMovement(Match match, Tournament tournament) {
        Player loser = match.getLoser();

        if (match.getBracket() == MatchBracket.UPPER) {
            // If the match is in the UPPER bracket, move the loser to the LOWER bracket
            tournament.getWinnersBracket().remove(loser);
            tournament.getLosersBracket().add(loser);

        } else if (match.getBracket() == MatchBracket.LOWER) {
            // If the match is in the LOWER bracket, remove the loser from the tournament (elimination)
            tournament.getLosersBracket().remove(loser);

        } else if (match.getBracket() == MatchBracket.FINAL) {
            // If the match is in the FINAL bracket
            if (tournament.getWinnersBracket().contains(loser)) {
                // If the loser is from the winners bracket, move to the losers bracket
                tournament.getWinnersBracket().remove(loser);
                tournament.getLosersBracket().add(loser);
            } else if (tournament.getLosersBracket().contains(loser)) {
                // If the loser is already in the losers bracket, the tournament is completed
                tournament.getWinnersBracket().clear();
                tournament.getLosersBracket().clear();
                tournament.setEndDate(LocalDate.now());
                tournament.setStatus(TournamentStatus.COMPLETED);
            }

        } else if (match.getBracket() == MatchBracket.GRAND_FINAL) {
            // For GRAND_FINAL, clearing both brackets signifies the tournament completion
            tournament.getWinnersBracket().clear();
            tournament.getLosersBracket().clear();
            tournament.setEndDate(LocalDate.now());
            tournament.setStatus(TournamentStatus.COMPLETED);
        }
    }

    /**
     * Builds and returns a Match object with specified players and tournament details.
     *
     * @param tournament the tournament the match belongs to.
     * @param bracket the bracket type of the match (e.g., UPPER, LOWER, FINAL).
     * @param player1 the first player in the match.
     * @param player2 the second player in the match.
     * @return the constructed Match object.
     */
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

    /**
     * Checks if all matches in the tournament have been completed.
     *
     * @param tournament the tournament to check.
     * @return true if all matches are completed; otherwise, false.
     */
    private boolean areAllMatchesCompleted(Tournament tournament){
        return tournament.getMatches().stream().allMatch(m->m.getStatus().equals(MatchStatus.COMPLETED));
    }

    /**
     * Determines if final matches should be created, based on the number of remaining players.
     *
     * @param tournament the tournament to check.
     * @return true if the final matches should be created; otherwise, false.
     */
    private boolean shouldCreateFinalMatches(Tournament tournament){
        int totalPlayers = tournament.getWinnersBracket().size() + tournament.getLosersBracket().size();
        return totalPlayers <= 2;
    }

    /**
     * Creates final matches in the tournament if conditions are met, based on the number of players
     * remaining in the winner's and loser's brackets.
     *
     * @param tournament the tournament to update.
     */
    private void createFinalMatches(Tournament tournament){
        if (tournament.getLosersBracket().size() == 2) {
            tournament.getMatches().add(buildMatch(tournament, MatchBracket.GRAND_FINAL,
                    tournament.getLosersBracket().get(0), tournament.getLosersBracket().get(1)));
        } else if (tournament.getLosersBracket().size() == 1 && tournament.getWinnersBracket().size() == 1) {
            tournament.getMatches().add(buildMatch(tournament, MatchBracket.FINAL,
                    tournament.getLosersBracket().get(0), tournament.getWinnersBracket().get(0)));
        }
    }

    /**
     * Creates regular matches for the remaining players in the tournament brackets.
     *
     * @param tournament the tournament to update.
     */
    private void createRegularMatches(Tournament tournament){
        createBracketMatches(tournament,tournament.getWinnersBracket(),MatchBracket.UPPER);
        createBracketMatches(tournament,tournament.getLosersBracket(),MatchBracket.LOWER);
    }

    /**
     * Creates matches within a specified bracket for a list of players in the tournament.
     *
     * @param tournament the tournament being updated.
     * @param bracket the list of players in the bracket.
     * @param matchBracket the type of the bracket for the matches (e.g., UPPER or LOWER).
     */
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

    /**
     * Determines the winner of a completed tournament.
     *
     * @param tournament the completed tournament.
     * @return the winner of the tournament.
     * @throws IllegalStateException if the tournament is not completed or if no winner is found.
     */

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



