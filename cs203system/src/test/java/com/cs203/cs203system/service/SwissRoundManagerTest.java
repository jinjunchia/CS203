package com.cs203.cs203system.service;
import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.EloService;
import com.cs203.cs203system.service.impl.SwissRoundManagerImpl;
import com.cs203.cs203system.utility.SwissRoundUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SwissRoundManagerTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private EloService eloService;

    @InjectMocks
    private SwissRoundManagerImpl swissRoundManagerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitializeSwiss() {
        // Arrange
        Tournament tournament = new Tournament();
        Player player1 = new Player();
        Player player2 = new Player();
        tournament.setPlayers(Arrays.asList(player1, player2));

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.initializeSwiss(tournament);

        // Assert
        assertEquals(2, result.getPlayers().size());
        assertEquals(1, result.getMatches().size());  // 2 players should result in 1 match
        assertEquals(1, result.getCurrentRoundNumber());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testReceiveMatchResult_NotAllMatchesCompleted() {
        // Arrange
        Tournament tournament = new Tournament();
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .status(MatchStatus.COMPLETED)
                .build();
        match.setPlayer1Score(1);
        match.setPlayer2Score(0);

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.receiveMatchResult(match);

        // Assert
        assertEquals(1.0, player1.getPoints());  // Winner should have 1 point
        assertEquals(0.0, player2.getPoints());  // Loser should have 0 points
        verify(eloService, times(1)).updateEloRatings(player1, player2, match);
        verify(playerRepository, times(1)).saveAll(List.of(player1, player2));
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testReceiveMatchResult_AllMatchesCompleted() {
        // Arrange
        Tournament tournament = new Tournament();
        Player player1 = new Player();
        Player player2 = new Player();
        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .status(MatchStatus.COMPLETED)
                .build();
        match.setPlayer1Score(1);
        match.setPlayer2Score(0);

        tournament.setMatches(List.of(match));
        tournament.setTotalSwissRounds(1);  // Simulate that only one round is necessary
        tournament.setCurrentRoundNumber(1);

        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
            when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
            mockedUtils.when(() -> SwissRoundUtils.findWinners(any(Tournament.class)))
                    .thenReturn(List.of(player1));

            // Act
            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);

            // Assert
            assertEquals(TournamentStatus.COMPLETED, result.getStatus());  // Tournament should be completed
            assertEquals(LocalDate.now(), result.getEndDate());  // End date should be set
            verify(tournamentRepository, times(1)).save(any(Tournament.class));
        }
    }

    @Test
    void testDetermineWinner_Success() {
        // Arrange
        Tournament tournament = new Tournament();
        Player player1 = new Player();
        player1.setPoints(3.0);
        tournament.setPlayers(List.of(player1));

        // Mock the static method SwissRoundUtils.findWinners
        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
            mockedUtils.when(() -> SwissRoundUtils.findWinners(any(Tournament.class)))
                    .thenReturn(List.of(player1));

            // Act
            Player result = swissRoundManagerImpl.determineWinner(tournament);

            // Assert
            assertEquals(player1, result);  // The winner should be player1
        }
    }

    @Test
    void testDetermineWinner_NoWinner() {
        // Arrange
        Tournament tournament = new Tournament();

        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
            mockedUtils.when(() -> SwissRoundUtils.findWinners(any(Tournament.class)))
                    .thenReturn(Collections.emptyList());
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                swissRoundManagerImpl.determineWinner(tournament);
            });
            assertEquals("No winner found", exception.getMessage());

        }
    }

    @Test
    void testInitializeSwiss_EmptyPlayers() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setPlayers(Collections.emptyList());

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.initializeSwiss(tournament);

        // Assert
        assertEquals(0, result.getPlayers().size());
        assertEquals(0, result.getMatches().size());  // No matches should be created
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

//    @Test
//    void testReceiveMatchResult_TiebreakerFinals() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        Player player1 = new Player();
//        Player player2 = new Player();
//        player1.setPoints(3.0);
//        player2.setPoints(3.0);
//        tournament.setPlayers(List.of(player1, player2));
//        tournament.setCurrentRoundNumber(3);
//        tournament.setTotalSwissRounds(3);  // Final round, tiebreaker needed
//
//        Match match = Match.builder()
//                .tournament(tournament)
//                .player1(player1)
//                .player2(player2)
//                .status(MatchStatus.COMPLETED)
//                .build();
//        match.setPlayer1Score(1);
//        match.setPlayer2Score(1);  // Simulating a draw
//
//        tournament.setMatches(List.of(match));
//
//
//
//        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
//            when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
//            mockedUtils.when(() -> SwissRoundUtils.findWinners(any(Tournament.class)))
//                    .thenReturn(List.of(player1, player2));  // Simulating a tie
//
//            assertNotNull(match.getTournament(), "Tournament is null");
//            assertNotNull(match.getPlayer1(), "Player 1 is null");
//            assertNotNull(match.getPlayer2(), "Player 2 is null");
//            assertNotNull(tournament.getMatches(), "Tournament matches are null");
//
//            // Act
//            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);
//
//            // Assert
//            assertEquals(1, result.getMatches().size());
//            assertEquals(MatchBracket.GRAND_FINAL, result.getMatches().get(0).getBracket());  // Should create grand final match
//            verify(tournamentRepository, times(1)).save(any(Tournament.class));
//        }
//    }
//
//    @Test
//    void testReceiveMatchResult_OddNumberOfPlayers() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        Player player1 = new Player();
//        Player player2 = new Player();
//        Player player3 = new Player();  // Odd number of players
//        player1.setPoints(3.0);
//        player2.setPoints(2.0);
//        player3.setPoints(1.0);
//        tournament.setPlayers(Arrays.asList(player1, player2, player3));
//
//        // Simulating the match history
//        Map<Player, Set<Player>> matchHistory = new HashMap<>();
//        matchHistory.put(player1, new HashSet<>());
//        matchHistory.put(player2, new HashSet<>());
//        matchHistory.put(player3, new HashSet<>());
//
//        Match match = Match.builder()
//                .tournament(tournament)
//                .player1(player1)
//                .player2(player2)
//                .status(MatchStatus.COMPLETED)
//                .build();
//        match.setPlayer1Score(1);
//        match.setPlayer2Score(0);
//
//        tournament.setMatches(List.of(match));
//        tournament.setCurrentRoundNumber(1);
//
//        // Mocking the necessary utilities and repositories
//        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
//        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
//            mockedUtils.when(() -> SwissRoundUtils.createMatchHistory(any(Tournament.class)))
//                    .thenReturn(matchHistory);
//
//            // Act: Call the existing receiveMatchResult method which contains the matchmaking logic
//            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);
//
//            // Assert: Verify the correct number of matches
//            assertEquals(2, result.getMatches().size());  // 1 actual match, 1 BYE match
//            long byeMatches = result.getMatches().stream().filter(m -> m.getStatus() == MatchStatus.BYE).count();
//            assertEquals(1, byeMatches);  // One player should get a BYE
//
//            verify(tournamentRepository, times(1)).save(any(Tournament.class));
//        }
//    }
//
//    @Test
//    void testReceiveMatchResult_EvenNumberOfPlayers() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        Player player1 = new Player();
//        Player player2 = new Player();
//        Player player3 = new Player();
//        Player player4 = new Player();  // Even number of players
//        player1.setPoints(3.0);
//        player2.setPoints(2.0);
//        player3.setPoints(1.0);
//        player4.setPoints(0.0);
//        tournament.setPlayers(Arrays.asList(player1, player2, player3, player4));
//
//        // Simulating the match history
//        Map<Player, Set<Player>> matchHistory = new HashMap<>();
//        matchHistory.put(player1, new HashSet<>());
//        matchHistory.put(player2, new HashSet<>());
//        matchHistory.put(player3, new HashSet<>());
//        matchHistory.put(player4, new HashSet<>());
//
//        Match match = Match.builder()
//                .tournament(tournament)
//                .player1(player1)
//                .player2(player2)
//                .status(MatchStatus.COMPLETED)
//                .build();
//        match.setPlayer1Score(1);
//        match.setPlayer2Score(0);
//
//        tournament.setMatches(List.of(match));
//        tournament.setCurrentRoundNumber(1);
//
//        // Mocking the necessary utilities and repositories
//        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
//        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
//            mockedUtils.when(() -> SwissRoundUtils.createMatchHistory(any(Tournament.class)))
//                    .thenReturn(matchHistory);
//
//            // Act: Call the existing receiveMatchResult method which contains the matchmaking logic
//            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);
//
//            // Assert: Verify the correct number of matches
//            assertEquals(2, result.getMatches().size());  // Two matches should be created for 4 players
//            long byeMatches = result.getMatches().stream().filter(m -> m.getStatus() == MatchStatus.BYE).count();
//            assertEquals(0, byeMatches);  // No BYE matches should exist
//
//            verify(tournamentRepository, times(1)).save(any(Tournament.class));
//        }
//    }
}
