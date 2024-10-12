package com.cs203.cs203system.service;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.impl.DoubleEliminationManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoubleEliminationManagerTest {

    @InjectMocks
    private DoubleEliminationManagerImpl doubleEliminationManagerImpl;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private EloService eloService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initializeDoubleElimination_ValidTournament_CreatesMatches() {
        // Arrange
        Tournament tournament = new Tournament();
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(2L);
        Player player3 = new Player();
        player3.setId(3L);
        Player player4 = new Player();
        player4.setId(4L);

        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        tournament.setPlayers(players);

        // Mock the shuffle operation to maintain player order (for deterministic results)
        doAnswer(invocation -> {
            Collections.shuffle(players);  // Still shuffle the players, but you can manipulate if needed for the test
            return null;
        }).when(tournamentRepository).save(any(Tournament.class));

        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = doubleEliminationManagerImpl.initializeDoubleElimination(tournament);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getMatches().size());  // 4 players should result in 2 matches
        assertEquals(MatchStatus.SCHEDULED, result.getMatches().get(0).getStatus());
        assertEquals(MatchBracket.UPPER, result.getMatches().get(0).getBracket());

        // Ensure players are paired correctly (based on shuffled list)
        assertNotNull(result.getMatches().get(0).getPlayer1());
        assertNotNull(result.getMatches().get(0).getPlayer2());
        assertNotNull(result.getMatches().get(1).getPlayer1());
        assertNotNull(result.getMatches().get(1).getPlayer2());

        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void receiveMatchResult_UpperBracket_LoserMovesToLowerBracket() {
        // Arrange
        Tournament tournament = new Tournament();

        Player player1 = new Player();
        Player player2 = new Player();
        player1.setName("winner123");
        player2.setName("loser123");

        tournament.setWinnersBracket(new ArrayList<>(List.of(player1, player2)));
        tournament.setLosersBracket(new ArrayList<>());

        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .player1Score(5)  // Player 1 wins
                .player2Score(1)  // Player 2 loses
                .bracket(MatchBracket.UPPER)
                .status(MatchStatus.COMPLETED)
                .build();


        // Mock the repository's save method to return the tournament after saving
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = doubleEliminationManagerImpl.receiveMatchResult(match);

        System.out.println("winners bracket" + result.getWinnersBracket());
        System.out.println("losers bracket" + result.getLosersBracket());

        // Assert
        assertNotNull(result);  // Ensure that result is not null
        assertTrue(result.getWinnersBracket().contains(player1));  // Player1 should remain in the winners bracket
        assertTrue(result.getLosersBracket().contains(player2));   // Player2 should move to the losers bracket

        // Verify interactions
        verify(eloService, times(1)).updateEloRatings(player1, player2, match);
        verify(tournamentRepository, times(1)).save(tournament);
    }


    @Test
    void receiveMatchResult_LowerBracket_LoserEliminated() {
        // Arrange
        Tournament tournament = new Tournament();

        Player player1 = new Player();
        Player player2 = new Player();
        player1.setName("winner123");
        player2.setName("loser123");

        tournament.setLosersBracket(new ArrayList<>(List.of(player1, player2)));

        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .player1Score(5)
                .player2Score(2)
                .bracket(MatchBracket.LOWER)
                .status(MatchStatus.COMPLETED)
                .build();

        // Mock the repository's save method to return the tournament after saving
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = doubleEliminationManagerImpl.receiveMatchResult(match);

        // Assert
        assertNotNull(result);
        assertFalse(result.getLosersBracket().contains(player2));   // Player2 is eliminated

        verify(eloService, times(1)).updateEloRatings(player1, player2, match);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void receiveMatchResult_FinalBracket_PlayerMovesBetweenBrackets() {
        // Arrange: Set up a Tournament object and initialize the winner's and loser's brackets
        Tournament tournament = new Tournament();


        // Define two players involved in the match
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setName("winner123");
        player2.setName("loser123");

        tournament.setWinnersBracket(new ArrayList<>(List.of(player1)));
        tournament.setLosersBracket(new ArrayList<>(List.of(player2)));

        // Simulate a match in the final bracket where PLAYER1 LOSES
        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .player1Score(2)
                .player2Score(3)
                .bracket(MatchBracket.FINAL)  // This is the final bracket
                .status(MatchStatus.COMPLETED)  // Match has been completed
                .build();

        // Mock the save method to return the updated tournament
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the method being tested
        Tournament result = doubleEliminationManagerImpl.receiveMatchResult(match);

//        System.out.println("winner bracket should contain no one" + result.getWinnersBracket());
//        System.out.println("loser's bracket should contain 2 players" + result.getLosersBracket());

        // Assert: Check that the tournament result is not null
        assertNotNull(result);

        // Player1 should be moved from the Winner's bracket to the Loser's bracket
        // Player2 should remain in Loser's bracket for a FINAL match in double elimination
        assertFalse(result.getWinnersBracket().contains(player1));  // Player1 is no longer in the Winner's bracket
        assertTrue(result.getLosersBracket().contains(player1));    // Player1 is now in the Loser's bracket
        assertTrue(result.getLosersBracket().contains(player2));    // Player2 remains in Loser's bracket


        // Verify that Elo ratings were updated for both players
        verify(eloService, times(1)).updateEloRatings(player1, player2, match);

        // Verify that the tournament was saved after processing the match result
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void receiveMatchResult_AllMatchesCompleted_UpdatesTournamentStatus() {
        // Arrange
        Tournament tournament = new Tournament();

        Player player1 = new Player();
        Player player2 = new Player();
        player1.setName("winner123");
        player2.setName("loser123");

        tournament.setLosersBracket(new ArrayList<>(List.of(player1, player2)));

        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .player1Score(5)
                .player2Score(2)
                .bracket(MatchBracket.FINAL)
                .status(MatchStatus.COMPLETED)
                .build();

        // Mock the repository's save method to return the tournament after saving
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = doubleEliminationManagerImpl.receiveMatchResult(match);

//        System.out.println("loser" + result.getLosersBracket());
//        System.out.println("winner" + result.getWinnersBracket());
//        System.out.println("tournament status" + result.getStatus());

        // Assert
        assertNotNull(result);
        assertFalse(result.getLosersBracket().contains(player2));   //both brackets should be cleared
        assertFalse(result.getWinnersBracket().contains(player2));
        assertEquals(TournamentStatus.COMPLETED,result.getStatus());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void determineWinner_TournamentNotCompleted_ThrowsIllegalStateException() {
        // Arrange: Create a tournament that is not completed
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.ONGOING);  // Set the status to anything but COMPLETED

        // Act & Assert: Ensure that calling determineWinner throws an IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            doubleEliminationManagerImpl.determineWinner(tournament);
        });

        // Assert the exception message
        assertEquals("Tournament is not completed. Winner cannot be determined.", exception.getMessage());
    }

    @Test
    void determineWinner_TournamentCompleted_ReturnsWinner() {
        // Arrange: Create a completed tournament
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.COMPLETED);  // Set the tournament status to COMPLETED

        // Create two players
        Player player1 = new Player();
        player1.setName("Player 1");

        Player player2 = new Player();
        player2.setName("Player 2");

        // Mock two matches, with match1 having a higher ID
        Match match1 = new Match();
        match1.setId(3L);  // Higher ID
        match1.setPlayer1(player1);
        match1.setPlayer2(player2);
        match1.setPlayer1Score(5);  // player1 wins match1
        match1.setPlayer2Score(1);

        Match match2 = new Match();
        match2.setId(2L);  // Lower ID
        match2.setPlayer1(player1);
        match2.setPlayer2(player2);
        match2.setPlayer1Score(2);  // player2 wins match2
        match2.setPlayer2Score(4);

        // Add the matches to the tournament
        tournament.setMatches(new ArrayList<>(List.of(match1, match2)));

        // Act: Call the method being tested
        Player result = doubleEliminationManagerImpl.determineWinner(tournament);

        // Assert: Ensure the winner is player1 (from match1, since it has the highest ID and the built-in logic determines the winner)
        assertNotNull(result);
        assertEquals(player1, result);  // match1 has the highest ID, and player1 won match1 based on the scores
    }
}