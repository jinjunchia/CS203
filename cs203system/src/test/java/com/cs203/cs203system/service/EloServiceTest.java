package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import com.cs203.cs203system.service.impl.EloServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EloServiceTest {

    @InjectMocks
    private EloServiceImpl eloServiceImpl;

    @Mock
    private EloRecordRepository eloRecordRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateEloRatings_Player1WinsPlayer2Loses_UpdatesRatingsAndSavesRecords() {
        // Arrange
        Player player1 = new Player();
        player1.setName("Player 1");
        player1.setEloRating(1500.0);

        Player player2 = new Player();
        player2.setName("Player 2");
        player2.setEloRating(1400.0);

        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(3);
        match.setPlayer2Score(1);

        // Act
        eloServiceImpl.updateEloRatings(player1, player2, match);

        // Assert
        // Expected ELO ratings after calculation
        double expectedNewEloPlayer1 = 1500 + 32 * (1.0 - eloServiceImpl.expectedScore(1500, 1400));
        double expectedNewEloPlayer2 = 1400 + 32 * (0.0 - eloServiceImpl.expectedScore(1400, 1500));

//        System.out.println("player1 new elo = " + expectedNewEloPlayer1);
//        System.out.println("player2 new elo = " + expectedNewEloPlayer2);

        assertEquals(expectedNewEloPlayer1, player1.getEloRating(), 0.01);
        assertEquals(expectedNewEloPlayer2, player2.getEloRating(), 0.01);

        // Verify that EloRecordRepository saved EloRecords for both players
        verify(eloRecordRepository, times(2)).save(any(EloRecord.class));
    }

    @Test
    void updateEloRatings_NoWinner_NoChanges() {
        // Arrange
        Player player1 = new Player();
        player1.setName("Player 1");
        player1.setEloRating(1500.0);

        Player player2 = new Player();
        player2.setName("Player 2");
        player2.setEloRating(1400.0);

        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(1);
        match.setPlayer2Score(1); // No winner (draw)

        // Act
        eloServiceImpl.updateEloRatings(player1, player2, match);

        System.out.println("match winner" + match.getWinner());

        // Assert
        // Elo ratings should not change
        assertEquals(1500, player1.getEloRating());
        assertEquals(1400, player2.getEloRating());

        // Verify that EloRecordRepository was never called (since no changes should be made)
        verify(eloRecordRepository, never()).save(any(EloRecord.class));
    }
}