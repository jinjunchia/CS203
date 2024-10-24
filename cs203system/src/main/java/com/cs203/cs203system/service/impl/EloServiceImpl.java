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

@Service
public class EloServiceImpl implements EloService {

    private static final int K_FACTOR = 32; // ELO constant

    private final EloRecordRepository eloRecordRepository;

    @Autowired
    public EloServiceImpl(EloRecordRepository eloRecordRepository) {
        this.eloRecordRepository = eloRecordRepository;
    }

    @Override
    @Transactional
    public void updateEloRatings(Player player1, Player player2, Match match) {
        Player winner = match.getWinner();

        if (winner == null) {
            return;
        }

        double actualScorePlayer1;
        double actualScorePlayer2;

        if (winner.equals(player1)) {
            actualScorePlayer1 = 1.0; // player1 wins
            actualScorePlayer2 = 0.0; // player2 loses
        } else {
            actualScorePlayer1 = 0.0; // player1 loses
            actualScorePlayer2 = 1.0; // player2 wins
        }

        // Store old EloRating
        double oldEloPlayer1 = player1.getEloRating();
        double oldEloPlayer2 = player2.getEloRating();

        // Calculate expected scores
        double expectedScorePlayer1 = expectedScore(player1.getEloRating(), player2.getEloRating());
        double expectedScorePlayer2 = expectedScore(player2.getEloRating(), player1.getEloRating());

        // Calculate new ELO ratings
        double newEloPlayer1 = player1.getEloRating() + K_FACTOR * (actualScorePlayer1 - expectedScorePlayer1);
        double newEloPlayer2 = player2.getEloRating() + K_FACTOR * (actualScorePlayer2 - expectedScorePlayer2);

        // Update players' ELO ratings
        player1.setEloRating(newEloPlayer1);
        player2.setEloRating(newEloPlayer2);

        // Save ELO records for historical tracking
        saveEloRecord(player1, match, oldEloPlayer1, newEloPlayer1, "Match against " + player2.getName());
        saveEloRecord(player2, match, oldEloPlayer2, newEloPlayer2, "Match against " + player1.getName());
    }

    public double expectedScore(double playerElo, double opponentElo) {
        // Calculates the expected score based on ELO ratings
        return 1 / (1 + Math.pow(10, (opponentElo - playerElo) / 400));
    }

    private void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason) {
        // Creates and saves an EloRecord entity to track rating changes
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
