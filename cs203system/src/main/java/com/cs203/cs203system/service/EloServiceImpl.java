package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EloServiceImpl implements EloService {

    private static final int K_FACTOR = 32; // ELO constant, can be adjusted based on requirements

    @Autowired
    private EloRecordRepository eloRecordRepository;

    @Override
    public void updateEloRatings(Player player1, Player player2, Match match) {
        double expectedScorePlayer1 = expectedScore(player1.getEloRating(), player2.getEloRating());
        double expectedScorePlayer2 = expectedScore(player2.getEloRating(), player1.getEloRating());

        // Determine actual scores based on match results
        double actualScorePlayer1 = match.getPlayer1Score() > match.getPlayer2Score() ? 1.0 :
                (match.getPlayer1Score().equals(match.getPlayer2Score()) ? 0.5 : 0.0);
        double actualScorePlayer2 = 1.0 - actualScorePlayer1;

        // Calculate new ELO ratings
        double newEloPlayer1 = player1.getEloRating() + K_FACTOR * (actualScorePlayer1 - expectedScorePlayer1);
        double newEloPlayer2 = player2.getEloRating() + K_FACTOR * (actualScorePlayer2 - expectedScorePlayer2);

        // Update players' ELO ratings
        player1.setEloRating(newEloPlayer1);
        player2.setEloRating(newEloPlayer2);

        // Save ELO records for historical tracking
        saveEloRecord(player1, match, player1.getEloRating(), newEloPlayer1, "Match against " + player2.getName());
        saveEloRecord(player2, match, player2.getEloRating(), newEloPlayer2, "Match against " + player1.getName());
    }

    private double expectedScore(double playerElo, double opponentElo) {
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
