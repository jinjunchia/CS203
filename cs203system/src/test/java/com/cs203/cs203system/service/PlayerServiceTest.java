package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDtoMapper;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceImplTest {

    @InjectMocks
    private PlayerServiceImpl playerServiceImpl;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void findAllPlayers_AllPlayers_ReturnAll() {
        // Arrange: Create mock Players and their corresponding DTOs
        Player player1 = new Player();
        player1.setName("Player1");

        Player player2 = new Player();
        player2.setName("Player2");

        List<Player> mockPlayers = Arrays.asList(player1, player2);

        PlayerWithOutStatsDto dto1 = new PlayerWithOutStatsDto();
        dto1.setName("Player1");

        PlayerWithOutStatsDto dto2 = new PlayerWithOutStatsDto();
        dto2.setName("Player2");

        // Mocking repository to return the list of players
        when(playerRepository.findAll()).thenReturn(mockPlayers);

        // Mocking the mapper to return DTOs for the players
        when(playerWithOutStatsDtoMapper.toDto(player1)).thenReturn(dto1);
        when(playerWithOutStatsDtoMapper.toDto(player2)).thenReturn(dto2);

        // Act: Call the findAllPlayers() method
        List<PlayerWithOutStatsDto> result = playerServiceImpl.findAllPlayers();

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());  // There should be 2 DTOs
        assertEquals("Player1", result.get(0).getName());
        assertEquals("Player2", result.get(1).getName());

        // Verify that the repository and mapper methods were called correctly
        verify(playerRepository, times(1)).findAll();
        verify(playerWithOutStatsDtoMapper, times(1)).toDto(player1);
        verify(playerWithOutStatsDtoMapper, times(1)).toDto(player2);
    }
}