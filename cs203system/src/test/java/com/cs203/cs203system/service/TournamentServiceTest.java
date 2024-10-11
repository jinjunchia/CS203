package com.cs203.cs203system.service;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.impl.TournamentManagerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class TournamentServiceTest {

    @InjectMocks
    TournamentManagerServiceImpl tournamentManagerServiceImpl;

    @Mock
    TournamentRepository tournamentRepository;

    @Mock
    MatchRepository matchRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    SwissRoundManager swissRoundManager;

    @Mock
    DoubleEliminationManager doubleEliminationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @AfterEach
    void tearDown() {

        // Reset the mocks after each test to ensure they will not retain state
        Mockito.reset(tournamentRepository, playerRepository);
    }

    //findTournamentById -> 2 Tests
    @Test
    void findTournamentById_TournamentExists_ReturnsTournament() {

        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        // Mock the repository to return the tournament wrapped in an Optional
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        Optional<Tournament> result = tournamentManagerServiceImpl.findTournamentById(tournamentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(tournamentId, result.get().getId());
        assertEquals("Test Tournament", result.get().getName());

        // Verify the repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }
    @Test
    void findTournamentById_TournamentNotFound_ThrowsNotFoundException() {

        // Arrange
        Long tournamentId = 1L;

        // Mock the repository to return an empty Optional
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tournamentManagerServiceImpl.findTournamentById(tournamentId);
        });

        // Assert the exception message
        assertEquals("Tournament id of " + tournamentId + " does not exist", exception.getMessage());

        // Verify the repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    //createTournament -> 1 Test
    @Test
    void createTournament_ValidTournament_ReturnsCreatedTournament() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");

        // Mocking the repository to return the tournament when save is called
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament savedTournament = invocation.getArgument(0);
            savedTournament.setId(1L);  // Simulate that the ID is generated upon saving
            return savedTournament;
        });

        // Act
        Tournament createdTournament = tournamentManagerServiceImpl.createTournament(tournament);

        // Assert
        assertNotNull(createdTournament);
        assertEquals(1L, createdTournament.getId());  // The ID should now be set
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus());  // Status should be SCHEDULED
        assertEquals("Test Tournament", createdTournament.getName());

        // Verify that the save method was called exactly once
        verify(tournamentRepository, times(1)).save(tournament);
    }

    // updatePlayerToTournament -> 5 Tests
    @Test
    void updatePlayersToTournament_ValidPlayers_ReturnsUpdatedTournament() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> playerIds = Arrays.asList(123L, 456L);

        // Mock tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setMinEloRating(1000.0);
        tournament.setMaxEloRating(2000.0);

        // Mock players
        Player player1 = new Player();
        player1.setId(123L);
        player1.setEloRating(1500.0);

        Player player2 = new Player();
        player2.setId(456L);
        player2.setEloRating(1800.0);

        // Mock repository behavior
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(123L)).thenReturn(Optional.of(player1));
        when(playerRepository.findById(456L)).thenReturn(Optional.of(player2));

        // Mock the save behavior to return the updated tournament
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament updatedTournament = tournamentManagerServiceImpl.updatePlayersToTournament(tournamentId, playerIds);

        // Assert
        assertNotNull(updatedTournament);
        assertEquals(2, updatedTournament.getPlayers().size());
        assertTrue(updatedTournament.getPlayers().contains(player1));
        assertTrue(updatedTournament.getPlayers().contains(player2));

        // Verify repository interactions
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerRepository, times(1)).findById(123L);
        verify(playerRepository, times(1)).findById(456L);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void updatePlayersToTournament_TournamentNotFound_ThrowsNotFoundException() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> playerIds = Arrays.asList(1L, 2L);

        // Mock tournament not found
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tournamentManagerServiceImpl.updatePlayersToTournament(tournamentId, playerIds);
        });

        assertEquals("Tournament id of " + tournamentId + " does not exist", exception.getMessage());

        // Verify repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void updatePlayersToTournament_TournamentNotScheduled_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> playerIds = Arrays.asList(1L, 2L);

        // Mock a non-scheduled tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.ONGOING);  // Not scheduled

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.updatePlayersToTournament(tournamentId, playerIds);
        });

        assertEquals("Tournament is already ongoing or completed", exception.getMessage());

        // Verify repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void updatePlayersToTournament_EmptyPlayerList_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> playerIds = Arrays.asList();  // Empty list

        // Mock a scheduled tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.updatePlayersToTournament(tournamentId, playerIds);
        });

        assertEquals("Please add at least 1 player", exception.getMessage());

        // Verify repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void updatePlayersToTournament_PlayerNotFound_ThrowsNotFoundException() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> playerIds = Arrays.asList(1L, 2L);

        // Mock a scheduled tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(new Player()));
        when(playerRepository.findById(2L)).thenReturn(Optional.empty());  // Player 2 not found

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tournamentManagerServiceImpl.updatePlayersToTournament(tournamentId, playerIds);
        });

        assertEquals("Player 2 not found", exception.getMessage());

        // Verify repository interaction
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).findById(2L);
    }

    //startTournament 5 Tests
    @Test
    void startTournament_TournamentNotFound_ThrowsNotFoundException() {
        // Arrange
        Long tournamentId = 1L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tournamentManagerServiceImpl.startTournament(tournamentId);
        });

        assertEquals("Tournament id of " + tournamentId + " does not exist", exception.getMessage());

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void startTournament_TournamentNotScheduled_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.ONGOING);  // Not scheduled

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.startTournament(tournamentId);
        });

        assertEquals("Tournament needs to be scheduled", exception.getMessage());

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void startTournament_NotEnoughPlayers_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        // Only 1 player
        tournament.setPlayers(Arrays.asList(new Player()));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.startTournament(tournamentId);
        });

        assertEquals("Tournament needs at least 2 players", exception.getMessage());

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void startTournament_OddNumberOfPlayers_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setPlayers(Arrays.asList(new Player(), new Player(), new Player()));  // 3 players (odd number)

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.startTournament(tournamentId);
        });

        assertEquals("Tournament needs an even number of players", exception.getMessage());

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void startTournament_DoubleElimination_InvalidNumberOfPlayers_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);

        // Number of players are divisible by 2 but not a power of 2 -> 10 players, 12 players, 14 players
        tournament.setPlayers(Arrays.asList(new Player(), new Player(), new Player(), new Player(), new Player(), new Player())); // 6 players ( divisible by 2 but not a power of two)

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.startTournament(tournamentId);
        });

        assertEquals("Double Elimination must have total number of players to power 2", exception.getMessage());

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void startTournament_ValidSwissTournament_StartsSuccessfully() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(TournamentFormat.SWISS);
        tournament.setPlayers(Arrays.asList(new Player(), new Player()));  // Valid number of players

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(swissRoundManager.initializeSwiss(tournament)).thenReturn(tournament);

        // Act
        Tournament result = tournamentManagerServiceImpl.startTournament(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(TournamentStatus.ONGOING, result.getStatus());

        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(swissRoundManager, times(1)).initializeSwiss(tournament);
    }

    @Test
    void startTournament_ValidDoubleEliminationTournament_StartsSuccessfully() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
        tournament.setPlayers(Arrays.asList(new Player(), new Player(), new Player(), new Player()));  // Power of 2 players

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(doubleEliminationManager.initializeDoubleElimination(tournament)).thenReturn(tournament);

        // Act
        Tournament result = tournamentManagerServiceImpl.startTournament(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(TournamentStatus.ONGOING, result.getStatus());

        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(doubleEliminationManager, times(1)).initializeDoubleElimination(tournament);
    }

    //inputResult 7 Tests
    @Test
    void inputResult_InvalidNegativeScores_ThrowsRuntimeException() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(-1);
        match.setPlayer2Score(5);

        Tournament tournament = new Tournament();
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.inputResult(match);
        });

        assertEquals("Match score cannot be negative", exception.getMessage());
        verify(matchRepository, times(1)).findById(match.getId());
    }

    @Test
    void inputResult_TotalScoreIsZero_ThrowsRuntimeException() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);

        Tournament tournament = new Tournament();
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.inputResult(match);
        });

        assertEquals("The total match score must be more than 0", exception.getMessage());
        verify(matchRepository, times(1)).findById(match.getId());
    }

    @Test
    void inputResult_DrawInDoubleElimination_ThrowsRuntimeException() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(3);
        match.setPlayer2Score(3);

        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.inputResult(match);
        });

        assertEquals("Draws are not allowed for Double Elimination", exception.getMessage());
        verify(matchRepository, times(1)).findById(match.getId());
    }

    @Test
    void inputResult_InvalidMatchStatus_ThrowsRuntimeException() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(1);
        match.setPlayer2Score(2);
        match.setStatus(MatchStatus.SCHEDULED);  // Invalid status

        Tournament tournament = new Tournament();
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.inputResult(match);
        });

        assertEquals("Please input a valid match status", exception.getMessage());
        verify(matchRepository, times(1)).findById(match.getId());
    }

    @Test
    void inputResult_MatchAlreadyCompleted_ThrowsRuntimeException() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(1);
        match.setPlayer2Score(2);
        match.setStatus(MatchStatus.COMPLETED);  // Already completed

        Tournament tournament = new Tournament();
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.inputResult(match);
        });

        assertEquals("Match has completed", exception.getMessage());
        verify(matchRepository, times(1)).findById(match.getId());
    }

    @Test
    void inputResult_ValidSwissMatch_UpdatesSuccessfully() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(3);
        match.setPlayer2Score(5);
        match.setStatus(MatchStatus.PENDING);  // Valid status

        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.SWISS);
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(swissRoundManager.receiveMatchResult(any(Match.class))).thenReturn(tournament);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        Tournament result = tournamentManagerServiceImpl.inputResult(match);

        // Assert
        assertNotNull(result);
        assertEquals(tournament, result);

        verify(matchRepository, times(1)).findById(match.getId());
        verify(swissRoundManager, times(1)).receiveMatchResult(match);
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void inputResult_ValidDoubleEliminationMatch_UpdatesSuccessfully() {
        // Arrange
        Match match = new Match();
        match.setId(1L);
        match.setPlayer1Score(3);
        match.setPlayer2Score(5);
        match.setStatus(MatchStatus.PENDING);  // Valid status

        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
        match.setTournament(tournament);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(doubleEliminationManager.receiveMatchResult(any(Match.class))).thenReturn(tournament);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        Tournament result = tournamentManagerServiceImpl.inputResult(match);

        // Assert
        assertNotNull(result);
        assertEquals(tournament, result);

        verify(matchRepository, times(1)).findById(match.getId());
        verify(doubleEliminationManager, times(1)).receiveMatchResult(match);
        verify(matchRepository, times(1)).save(match);
    }

    // check if the match status changes from PENDING to COMPLETED
    @Test
    void inputResult_ValidSwissMatch_UpdatesMatchStatus() {
        // Arrange
        Long matchId = 1L;

        // Create a match with status PENDING
        Match match = new Match();
        match.setId(matchId);
        match.setPlayer1Score(3);
        match.setPlayer2Score(5);
        match.setStatus(MatchStatus.PENDING);  // Initial status is PENDING

        // Create a tournament with SWISS format
        Tournament tournament = new Tournament();
        tournament.setFormat(TournamentFormat.SWISS);
        match.setTournament(tournament);

        // Mock the repository to return the match when found
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Mock the swissRoundManager behavior to return the tournament
        when(swissRoundManager.receiveMatchResult(any(Match.class))).thenReturn(tournament);

        // Mock the save behavior to return the updated match
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = tournamentManagerServiceImpl.inputResult(match);

        // Assert
        assertNotNull(result);  // Ensure the result is not null
        assertEquals(MatchStatus.COMPLETED, match.getStatus());  // Verify the match status changed to COMPLETED

        // Verify interactions with repository and managers
        verify(matchRepository, times(1)).findById(matchId);
        verify(swissRoundManager, times(1)).receiveMatchResult(match);
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void determineWinner_TournamentNotFound_ThrowsNotFoundException() {
        // Arrange
        Long tournamentId = 1L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tournamentManagerServiceImpl.determineWinner(tournamentId);
        });

        assertEquals("Tournament id " + tournamentId + " does not exist", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void determineWinner_TournamentNotCompleted_ThrowsRuntimeException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.ONGOING);  // Not completed

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentManagerServiceImpl.determineWinner(tournamentId);
        });

        assertEquals("Winner cannot be determined in a tournament that has not completed.", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void determineWinner_SwissTournament_ReturnsWinner() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.COMPLETED);  // Completed
        tournament.setFormat(TournamentFormat.SWISS);

        Player winner = new Player();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(swissRoundManager.determineWinner(tournament)).thenReturn(winner);

        // Act
        Player result = tournamentManagerServiceImpl.determineWinner(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(winner, result);
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(swissRoundManager, times(1)).determineWinner(tournament);
    }

    @Test
    void determineWinner_DoubleEliminationTournament_ReturnsWinner() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.COMPLETED);  // Completed
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);

        Player winner = new Player();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(doubleEliminationManager.determineWinner(tournament)).thenReturn(winner);

        // Act
        Player result = tournamentManagerServiceImpl.determineWinner(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(winner, result);
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(doubleEliminationManager, times(1)).determineWinner(tournament);
    }


}
