package com.cs203.cs203system.service;
import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
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
    private MatchRepository matchRepository;

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
        tournament.setFormat(TournamentFormat.SWISS);

        Player player1 = new Player();
        Player player2 = new Player();
        tournament.setPlayers(Arrays.asList(player1, player2));

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.initializeTournament(tournament);

        // Assert
        assertEquals(2, result.getPlayers().size());
        assertEquals(1, result.getMatches().size());  // 2 players should result in 1 match
        assertEquals(1, result.getCurrentRoundNumber());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testReceiveMatchResult_AllMatchesCompleted() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.SWISS);
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
        tournament.setFormat(TournamentFormat.SWISS);
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
        tournament.setFormat(TournamentFormat.SWISS);

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
        tournament.setFormat(TournamentFormat.SWISS);
        tournament.setPlayers(Collections.emptyList());

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.initializeTournament(tournament);

        // Assert
        assertEquals(0, result.getPlayers().size());
        assertEquals(0, result.getMatches().size());  // No matches should be created
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testReceiveMatchResult_NoDraw() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.SWISS);
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setPoints(3.0);
        player2.setPoints(2.0);
        tournament.setPlayers(Arrays.asList(player1, player2));
        tournament.setCurrentRoundNumber(1);
        tournament.setTotalSwissRounds(1);

        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .status(MatchStatus.COMPLETED)
                .build();
        System.out.println("Player 1 original score:" + player1.getPoints() + "Player 2 original score:" + player2.getPoints());
        match.setPlayer1Score(1);
        match.setPlayer2Score(0);  // No draw, player1 is the winner

        System.out.println("Match winner is:" + match.getWinner());

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament result = swissRoundManagerImpl.receiveMatchResult(match);

        // Assert
        System.out.println("Player 1 score is " + player1.getPoints() + " and Player 2 score is " + player2.getPoints());
        assertEquals(4.0, player1.getPoints());  // Winner's points should increase
        assertEquals(2.0, player2.getPoints());  // Loser's points remain the same
        verify(eloService, times(1)).updateEloRatings(player1, player2, match);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testReceiveMatchResult_Draw() {
        // Arrange
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.SWISS);
        Player player1 = new Player();
        Player player2 = new Player();

        // Initialize player fields
        player1.setId(1L);  // Ensure each player has an ID
        player1.setPoints(3.0);  // Starting points
        player2.setId(2L);
        player2.setPoints(2.0);

        // Set the players in the tournament
        tournament.setPlayers(Arrays.asList(player1, player2));
        tournament.setCurrentRoundNumber(1);
        tournament.setTotalSwissRounds(1);

        Match match = Match.builder()
                .tournament(tournament)
                .player1(player1)
                .player2(player2)
                .status(MatchStatus.COMPLETED)
                .build();
        match.setPlayer1Score(1);
        match.setPlayer2Score(1);  // Simulate a draw

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        System.out.println("Match:" + match);
        // Act
        Tournament result = swissRoundManagerImpl.receiveMatchResult(match);


        // Assert
        assertEquals(3.5, player1.getPoints());  // Both players get points for a draw
        assertEquals(2.5, player2.getPoints());
        verify(eloService, times(1)).updateEloRatings(player1, player2, match);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

//    @Test
//    void testReceiveMatchResult_NormalMatchmaking() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        tournament.setFormat(TournamentFormat.SWISS);
//        Player player1 = new Player();
//        Player player2 = new Player();
//        Player player3 = new Player();  // Odd number of players for BYE
//        player1.setPoints(3.0);
//        player2.setPoints(2.0);
//        player3.setPoints(1.0);
//        tournament.setPlayers(Arrays.asList(player1, player2, player3));
//        tournament.setTotalSwissRounds(3);
//        tournament.setCurrentRoundNumber(1);
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
//        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
//        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
//            mockedUtils.when(() -> SwissRoundUtils.createMatchHistory(any(Tournament.class)))
//                    .thenReturn(new HashMap<>());
//
//            // Act
//            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);
//
//            // Assert
//            assertEquals(2, result.getMatches().size());  // Should create 2 new matches (1 real match, 1 BYE)
//            long byeMatches = result.getMatches().stream().filter(m -> m.getStatus() == MatchStatus.BYE).count();
//            assertEquals(1, byeMatches);  // One player should get a BYE
//            verify(tournamentRepository, times(1)).save(any(Tournament.class));
//        }
//    }

//    @Test
//    void testReceiveMatchResult_NormalMatchmaking() {
//        // Arrange: Set up a tournament with an odd number of players for a BYE scenario
//        Tournament tournament = new Tournament();
//        tournament.setFormat(TournamentFormat.SWISS);
//        tournament.setTotalSwissRounds(3);
//        tournament.setCurrentRoundNumber(1);
//
//        Player player1 = new Player();
//        Player player2 = new Player();
//        Player player3 = new Player();  // Odd player to get BYE
//        player1.setPoints(3.0);
//        player2.setPoints(2.0);
//        player3.setPoints(1.0);
//        tournament.setPlayers(Arrays.asList(player1, player2, player3));
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
//        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
//
//        try (MockedStatic<SwissRoundUtils> mockedUtils = mockStatic(SwissRoundUtils.class)) {
//            // Mock match history to simulate no previous matches
//            mockedUtils.when(() -> SwissRoundUtils.createMatchHistory(any(Tournament.class)))
//                    .thenReturn(new HashMap<>());
//
//            // Act: Call receiveMatchResult to process the match
//            Tournament result = swissRoundManagerImpl.receiveMatchResult(match);
//
//            // Assert: Check the correct number of matches
//            assertEquals(2, result.getMatches().size(), "Should create 2 matches (1 real match, 1 BYE for odd player)");
//
//            // Ensure one of the matches is a BYE
//            long byeMatches = result.getMatches().stream().filter(m -> m.getStatus() == MatchStatus.BYE).count();
//            assertEquals(1, byeMatches, "One player should receive a BYE match due to odd number of players");
//
//            // Verify Elo rating update and save operations
//            verify(eloService, times(1)).updateEloRatings(player1, player2, match);
//            verify(tournamentRepository, times(1)).save(any(Tournament.class));
//        }
//    }

//    @Test
//    void testReceiveMatchResult_EvenNumberOfPlayers() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        tournament.setFormat(TournamentFormat.SWISS);
//        Player player1 = new Player();
//        Player player2 = new Player();
//        Player player3 = new Player();
//        Player player4 = new Player();  // Even number of players
//        player1.setPoints(3.0);
//        player2.setPoints(2.0);
//        player3.setPoints(1.0);
//        player4.setPoints(0.0);
//
//        tournament.setPlayers(Arrays.asList(player1, player2, player3, player4));
////
////         Simulating the match history
//        Map<Player, Set<Player>> matchHistory = new HashMap<>();
//        matchHistory.put(player1, new HashSet<>());
//        matchHistory.put(player2, new HashSet<>());
//        matchHistory.put(player3, new HashSet<>());
//        matchHistory.put(player4, new HashSet<>());
//
//        // Set up a completed match with winner and loser
//        Match match = Match.builder()
//                .tournament(tournament)
//                .player1(player1)
//                .player2(player2)
//                .status(MatchStatus.COMPLETED)
//                .build();
//
//        match.setPlayer1Score(3);
//        match.setPlayer2Score(2);
//
//        Match match2 = Match.builder()
//                .tournament(tournament)
//                .player1(player3)
//                .player2(player4)
//                .status(MatchStatus.COMPLETED)
//                .build();
//
//        match2.setPlayer1Score(3);
//        match2.setPlayer2Score(2);
//
//        List<Match> matches = new ArrayList<>();
//        matches.add(match);
//        matches.add(match2);
//
//        System.out.println("Match winner:" + match.getWinner());
//
//        tournament.setMatches(matches);
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
