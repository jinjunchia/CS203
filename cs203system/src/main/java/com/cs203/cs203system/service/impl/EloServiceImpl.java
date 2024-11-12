package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import com.cs203.cs203system.service.EloService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service implementation for calculating and updating Elo ratings.
 *
 * This service calculates updated Elo ratings for players after each match,
 * taking into account factors such as punches, dodges, and knockouts.
 * It also saves Elo record history for each rating change.
 */
@Service
public class EloServiceImpl implements EloService {

    private static final int K_FACTOR = 32; // ELO constant

    private final EloRecordRepository eloRecordRepository;

    /**
     * Constructs an EloServiceImpl with the necessary dependency.
     *
     * @param eloRecordRepository the repository for saving Elo records
     */
    @Autowired
    public EloServiceImpl(EloRecordRepository eloRecordRepository) {
        this.eloRecordRepository = eloRecordRepository;
    }

    /**
     * Updates the Elo ratings for two players after a match, considering additional
     * metrics such as punches, dodges, and knockouts.
     *
     * @param player1 the first player in the match
     * @param player2 the second player in the match
     * @param match the match containing player scores and other metrics
     * @throws IllegalArgumentException if player scores are null
     * @throws IllegalStateException if both players are recorded as having performed a KO
     */
    @Override
    @Transactional
    public void updateEloRatings(Player player1, Player player2, Match match) {
        Integer player1Score = match.getPlayer1Score();
        Integer player2Score = match.getPlayer2Score();

        if (player1Score == null || player2Score == null) {
            throw new IllegalArgumentException("Player scores must not be null.");
        }

        // Determine actual scores for Elo calculation
        double actualScorePlayer1 = player1Score > player2Score ? 1.0 : (player1Score < player2Score ? 0.0 : 0.5);
        double actualScorePlayer2 = 1.0 - actualScorePlayer1;

        // Retrieve additional metrics
        int punchesPlayer1 = match.getPunchesPlayer1();
        int punchesPlayer2 = match.getPunchesPlayer2();
        int dodgesPlayer1 = match.getDodgesPlayer1();
        int dodgesPlayer2 = match.getDodgesPlayer2();
        boolean koByPlayer1 = match.isKoByPlayer1();
        boolean koByPlayer2 = match.isKoByPlayer2();

        if (koByPlayer1 && koByPlayer2) {
            throw new IllegalStateException("Both players cannot perform a KO in the same match.");
        }

        // Store old Elo ratings
        double oldEloPlayer1 = player1.getEloRating();
        double oldEloPlayer2 = player2.getEloRating();

        // Calculate expected scores based on Elo ratings
        double expectedScorePlayer1 = expectedScore(player1.getEloRating(), player2.getEloRating());
        double expectedScorePlayer2 = expectedScore(player2.getEloRating(), player1.getEloRating());

        // Apply additional factors, with reduced weight for the losing player
        double punchFactorPlayer1 = actualScorePlayer1 == 1.0 ? calculatePunchFactor(punchesPlayer1, punchesPlayer2) : calculatePunchFactor(punchesPlayer1, punchesPlayer2) * 0.5;
        double punchFactorPlayer2 = actualScorePlayer2 == 1.0 ? calculatePunchFactor(punchesPlayer2, punchesPlayer1) : calculatePunchFactor(punchesPlayer2, punchesPlayer1) * 0.5;
        double dodgeFactorPlayer1 = actualScorePlayer1 == 1.0 ? calculateDodgeFactor(dodgesPlayer1, dodgesPlayer2) : calculateDodgeFactor(dodgesPlayer1, dodgesPlayer2) * 0.5;
        double dodgeFactorPlayer2 = actualScorePlayer2 == 1.0 ? calculateDodgeFactor(dodgesPlayer2, dodgesPlayer1) : calculateDodgeFactor(dodgesPlayer2, dodgesPlayer1) * 0.5;
        double koFactorPlayer1 = actualScorePlayer1 == 1.0 && koByPlayer1 ? calculateKOFactor(true) : 0;
        double koFactorPlayer2 = actualScorePlayer2 == 1.0 && koByPlayer2 ? calculateKOFactor(true) : 0;

        // Calculate new Elo ratings with adjusted factors
        double newEloPlayer1 = player1.getEloRating() + K_FACTOR * (actualScorePlayer1 - expectedScorePlayer1) + punchFactorPlayer1 + dodgeFactorPlayer1 + koFactorPlayer1;
        double newEloPlayer2 = player2.getEloRating() + K_FACTOR * (actualScorePlayer2 - expectedScorePlayer2) + punchFactorPlayer2 + dodgeFactorPlayer2 + koFactorPlayer2;

        // Update players' Elo ratings
        player1.setEloRating(newEloPlayer1);
        player2.setEloRating(newEloPlayer2);

        // Save Elo records for historical tracking
        saveEloRecord(player1, match, oldEloPlayer1, newEloPlayer1, "Match against " + player2.getName());
        saveEloRecord(player2, match, oldEloPlayer2, newEloPlayer2, "Match against " + player1.getName());
    }

    /**
     * Calculates the expected score based on Elo ratings.
     *
     * @param playerElo the Elo rating of the player
     * @param opponentElo the Elo rating of the opponent
     * @return the expected score of the player
     */
    public double expectedScore(double playerElo, double opponentElo) {
        return 1 / (1 + Math.pow(10, (opponentElo - playerElo) / 400));
    }

    /**
     * Calculates the punch factor for the Elo adjustment based on punches landed.
     *
     * @param punchesPlayer1 punches landed by player1
     * @param punchesPlayer2 punches landed by player2
     * @return the punch factor for Elo adjustment
     */
    private double calculatePunchFactor(int punchesPlayer1, int punchesPlayer2) {
        int totalPunches = punchesPlayer1 + punchesPlayer2;
        if (totalPunches == 0) return 0;

        double punchRatio = (double) punchesPlayer1 / totalPunches;
        return K_FACTOR * (punchRatio - 0.5); // Adjust to give more points to higher punch ratio
    }

    /**
     * Calculates the dodge factor for the Elo adjustment based on dodges performed.
     *
     * @param dodgesPlayer1 dodges performed by player1
     * @param dodgesPlayer2 dodges performed by player2
     * @return the dodge factor for Elo adjustment
     */
    private double calculateDodgeFactor(int dodgesPlayer1, int dodgesPlayer2) {
        int totalDodges = dodgesPlayer1 + dodgesPlayer2;
        if (totalDodges == 0) return 0;

        double dodgeRatio = (double) dodgesPlayer1 / totalDodges;
        return K_FACTOR * (dodgeRatio - 0.5); // Adjust to reward higher dodge performance
    }

    /**
     * Calculates the knockout factor for the Elo adjustment based on a KO win.
     *
     * @param isKO indicates if the player won by knockout
     * @return the knockout factor for Elo adjustment
     */
    private double calculateKOFactor(boolean isKO) {
        return isKO ? K_FACTOR * 1.5 : 0; // Example: Bonus points for a knockout win
    }

    /**
     * Saves an Elo record for a player to track rating changes.
     *
     * @param player the player whose Elo rating has changed
     * @param match the match associated with the Elo change
     * @param oldRating the player's Elo rating before the match
     * @param newRating the player's Elo rating after the match
     * @param reason the reason for the Elo change
     */
    private void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason) {
        EloRecord record = EloRecord.builder()
                .player(player)
                .match(match)
                .oldRating(oldRating)
                .newRating(newRating)
                .changeReason(reason)
                .date(LocalDateTime.now())
                .build();

        eloRecordRepository.save(record);
    }
}
