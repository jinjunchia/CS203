package com.cs203.cs203system.service;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindMatchById_Found() {
        // Arrange: Prepare the necessary mock and input data
        Long matchId = 1L;
        Match mockMatch = new Match();
        mockMatch.setId(matchId);
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(mockMatch));

        // Act: Call the method under test
        Match foundMatch = matchService.findMatchById(matchId);

        // Assert: Verify the expected result
        assertNotNull(foundMatch);
        assertEquals(matchId, foundMatch.getId());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void testFindMatchById_NotFound() {
        // Arrange: Set up the mock to return an empty result
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert: Call the method and expect a NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.findMatchById(matchId);
        });

        assertEquals("Tournament with id " + matchId + " not found", exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }
}
