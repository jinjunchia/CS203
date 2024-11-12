package com.cs203.cs203system.service;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.impl.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @InjectMocks
    private MatchServiceImpl matchServiceImpl;

    @Mock
    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findMatchById_MatchExists_ReturnsMatch() {
        // Arrange
        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);

        // Mocking the repository
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Act
        Match result = matchServiceImpl.findMatchById(matchId);

        // Assert
        assertNotNull(result);
        assertEquals(matchId, result.getId());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    void findMatchById_MatchNotFound_ThrowsNotFoundException() {
        // Arrange
        Long matchId = 1L;

        // Mocking the repository
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchServiceImpl.findMatchById(matchId);
        });

        assertEquals("Match with id " + matchId + " not found", exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    void findAllMatches_ReturnsListOfMatches() {
        // Arrange
        Match match1 = new Match();
        Match match2 = new Match();
        List<Match> matches = Arrays.asList(match1, match2);

        // Mocking the repository
        when(matchRepository.findAll()).thenReturn(matches);

        // Act
        List<Match> result = matchServiceImpl.findAllMatches();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    void findAllMatchesByTournamentId_ReturnsListOfMatches() {
        // Arrange
        Long tournamentId = 1L;
        Match match1 = new Match();
        Match match2 = new Match();
        List<Match> matches = Arrays.asList(match1, match2);

        // Mocking the repository
        when(matchRepository.findByTournamentId(tournamentId)).thenReturn(matches);

        // Act
        List<Match> result = matchServiceImpl.findAllMatchesByTournamentId(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findByTournamentId(tournamentId);
    }
}