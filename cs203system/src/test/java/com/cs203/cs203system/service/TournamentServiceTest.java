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
import com.cs203.cs203system.service.impl.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    SwissRoundManagerImpl swissRoundManagerImpl;

    @Mock
    DoubleEliminationManagerImpl doubleEliminationManagerImpl;

    @Mock
    SwissDoubleEliminationHybridManagerImpl hybridManagerImpl;

    @Mock
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tournamentManagerServiceImpl = new TournamentManagerServiceImpl(
                swissRoundManagerImpl,
                doubleEliminationManagerImpl,
                hybridManagerImpl,
                tournamentRepository,
                playerRepository,
                matchRepository,
                notificationService
        );
    }

    @AfterEach
    void tearDown() {
        reset(tournamentRepository, playerRepository, matchRepository, swissRoundManagerImpl, doubleEliminationManagerImpl, hybridManagerImpl, notificationService);
    }

    // Tests for finding tournaments
    @Test
    void findTournamentById_TournamentExists_ReturnsTournament() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        Optional<Tournament> result = tournamentManagerServiceImpl.findTournamentById(tournamentId);

        assertTrue(result.isPresent());
        assertEquals("Test Tournament", result.get().getName());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void findTournamentById_TournamentNotFound_ThrowsNotFoundException() {
        Long tournamentId = 1L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> tournamentManagerServiceImpl.findTournamentById(tournamentId));
        assertEquals("Tournament id of " + tournamentId + " does not exist", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    // Test for creating a tournament
    @Test
    void createTournament_ValidTournament_ReturnsCreatedTournament() {
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");

        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament savedTournament = invocation.getArgument(0);
            savedTournament.setId(1L);
            return savedTournament;
        });

        Tournament createdTournament = tournamentManagerServiceImpl.createTournament(tournament);

        assertNotNull(createdTournament);
        assertEquals(1L, createdTournament.getId());
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus());
        assertEquals("Test Tournament", createdTournament.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    // Tests for starting tournaments with various formats
    @Test
    void startTournament_ValidSwissTournament_StartsSuccessfully() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(TournamentFormat.SWISS);
        tournament.setPlayers(Arrays.asList(new Player(), new Player()));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(swissRoundManagerImpl.initializeTournament(tournament)).thenReturn(tournament);

        Tournament result = tournamentManagerServiceImpl.startTournament(tournamentId);

        assertNotNull(result);
        assertEquals(TournamentStatus.ONGOING, result.getStatus());
        verify(swissRoundManagerImpl, times(1)).initializeTournament(tournament);
    }

    @Test
    void startTournament_DoubleElimination_InvalidNumberOfPlayers_ThrowsRuntimeException() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);
        tournament.setPlayers(Arrays.asList(new Player(), new Player(), new Player())); // Not a power of 2

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tournamentManagerServiceImpl.startTournament(tournamentId));
        assertEquals("Double Elimination must have a total number of players equal to a power of 2", exception.getMessage());
    }

    @Test
    void startTournament_HybridFormat_UnsupportedFormat_ThrowsIllegalArgumentException() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        tournament.setFormat(null);
        tournament.setPlayers(Arrays.asList(new Player(), new Player()));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tournamentManagerServiceImpl.startTournament(tournamentId));
        assertEquals("Unsupported tournament format: null", exception.getMessage());
    }

    // Tests for determining winners
    @Test
    void determineWinner_DoubleEliminationTournament_ReturnsWinner() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.COMPLETED);
        tournament.setFormat(TournamentFormat.DOUBLE_ELIMINATION);

        Player winner = new Player();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(doubleEliminationManagerImpl.determineWinner(tournament)).thenReturn(winner);

        Player result = tournamentManagerServiceImpl.determineWinner(tournamentId);

        assertNotNull(result);
        assertEquals(winner, result);
        verify(doubleEliminationManagerImpl, times(1)).determineWinner(tournament);
    }

    // Add other tests as needed

    // Utility method to reset all mocks
    @AfterEach
    void resetMocks() {
        reset(tournamentRepository, playerRepository, matchRepository, swissRoundManagerImpl, doubleEliminationManagerImpl, hybridManagerImpl, notificationService);
    }
}
