package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import com.cs203.cs203system.dtos.players.CreatePlayerMapper;
import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDtoMapper;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
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

class PlayerServiceTest {

    @InjectMocks
    private PlayerServiceImpl playerServiceImpl;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper;

    @Mock
    private CreatePlayerMapper createPlayerMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @AfterEach
    void tearDown() {
        // Reset the mocks after each test to ensure they don't retain state
        Mockito.reset(playerRepository, playerWithOutStatsDtoMapper);
    }
    @Test
    void findAllPlayers_PlayersExist_ReturnAll() {
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
    @Test
    void findAllPlayers_NoPlayers_ReturnEmptyList() {

        // Arrange: Mock the repository to return an empty list
        when(playerRepository.findAll()).thenReturn(Arrays.asList());

        // Act: Call the findAllPlayers() method
        List<PlayerWithOutStatsDto> result = playerServiceImpl.findAllPlayers();

        // Assert: The result should be an empty list
        assertNotNull(result);  // Check that the method does not return null
        assertEquals(0, result.size());  // Check that the list size is zero

        // Verify that the repository's findAll method was called once
        verify(playerRepository, times(1)).findAll();

        // Verify that the mapper was never called, since no players exist
        verify(playerWithOutStatsDtoMapper, never()).toDto(any(Player.class));
    }

    @Test
    void findPlayerById_PlayerId_ReturnId() {
        // Arrange: Create a mock Player and its corresponding DTO
        Long playerId = 123L;

        Player mockPlayer = new Player();
        mockPlayer.setId(playerId);
        mockPlayer.setName("Player1");

        PlayerWithOutStatsDto mockDto = new PlayerWithOutStatsDto();
        mockDto.setId(playerId);
        mockDto.setName("Player1");

        // Mock the repository to return the Player when findById is called
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(mockPlayer));

        // Mock the mapper to convert the Player to PlayerWithOutStatsDto
        when(playerWithOutStatsDtoMapper.toDto(mockPlayer)).thenReturn(mockDto);

        // Act: Call the findPlayerById method
        Optional<PlayerWithOutStatsDto> result = playerServiceImpl.findPlayerById(playerId);

        // Assert: Check that the result is present and has the correct values
        assertTrue(result.isPresent());
        assertEquals(playerId, result.get().getId());
        assertEquals("Player1", result.get().getName());

        // Verify that the repository's findById method was called once
        verify(playerRepository, times(1)).findById(playerId);

        // Verify that the mapper's toDto method was called once
        verify(playerWithOutStatsDtoMapper, times(1)).toDto(mockPlayer);
    }

    @Test
    void findPlayerById_PlayerNotFound_ReturnNull() {
        // Arrange: Mock the repository to return an empty Optional when findById is called
        Long playerId = 1L;
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act: Call the findPlayerById method
        Optional<PlayerWithOutStatsDto> result = playerServiceImpl.findPlayerById(playerId);

        // Assert: The result should be an empty Optional
        assertFalse(result.isPresent(), "Expected no player to be found, but a player was returned.");

        // Verify that the repository's findById method was called once
        verify(playerRepository, times(1)).findById(playerId);

        // Verify that the mapper's toDto method was never called since the player was not found
        verify(playerWithOutStatsDtoMapper, never()).toDto(any(Player.class));
    }

    @Test
    void createPlayer_ShouldReturnPlayerWithOutStatsDto() {
        // Arrange: Mock input CreateUserRequest and expected outputs
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Player1");
        createUserRequest.setEmail("player1@email.com");
        createUserRequest.setPassword("password123");

        Player newPlayer = new Player();
        newPlayer.setName("Player1");
        newPlayer.setEmail("player1@email.com");

        Player savedPlayer = new Player();
        savedPlayer.setId(1L);  // Simulate saved player having an ID
        savedPlayer.setName("Player1");
        savedPlayer.setEmail("player1@email.com");

        PlayerWithOutStatsDto playerDto = new PlayerWithOutStatsDto();
        playerDto.setId(1L);
        playerDto.setName("Player1");
        playerDto.setEmail("player1@email.com");

        // Mock the behavior of the mappers and repository
        when(createPlayerMapper.toEntity(createUserRequest)).thenReturn(newPlayer);
        when(playerRepository.save(newPlayer)).thenReturn(savedPlayer);
        when(playerWithOutStatsDtoMapper.toDto(savedPlayer)).thenReturn(playerDto);

        // Act: Call the createPlayer() method
        PlayerWithOutStatsDto result = playerServiceImpl.createPlayer(createUserRequest);

        // Assert: Verify that the result matches the expected DTO
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        // Verify that the repository and mappers were called as expected
        verify(createPlayerMapper, times(1)).toEntity(createUserRequest);
        verify(playerRepository, times(1)).save(newPlayer);
        verify(playerWithOutStatsDtoMapper, times(1)).toDto(savedPlayer);
    }

    @Test
    void updatePlayer_UpdatePlayerName_ReturnUpdatedPlayerName() {
        // Arrange: Mock the existing player and the update request
        Long playerId = 123L;
        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setName("CurrentName");

        PlayerUpdateRequest updateRequest = new PlayerUpdateRequest();
        updateRequest.setName(Optional.of("ChangedName")); // New name

        Player updatedPlayer = new Player();
        updatedPlayer.setId(playerId);
        updatedPlayer.setName("ChangedName");

        // Mock the repository findById to return the existing player
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));

        // Mock the repository save method to return the updated player
        when(playerRepository.save(existingPlayer)).thenReturn(updatedPlayer);

        // Act: Call the updatePlayer() method
        Player result = playerServiceImpl.updatePlayer(playerId, updateRequest);

        // Assert: Verify the player's name was updated correctly
        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals("ChangedName", result.getName()); // Name should be updated to "ChangedName"

        // Verify that findById was called once
        verify(playerRepository, times(1)).findById(playerId);

        // Verify that save was called once with the updated player
        verify(playerRepository, times(1)).save(existingPlayer);
    }
    @Test
    void updatePlayer_UpdatePlayerUsername_ReturnUpdatedPlayerUsername() {
        // Arrange: Mock the existing player and the update request
        Long playerId = 123L;
        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setUsername("MyUsername");

        PlayerUpdateRequest updateRequest = new PlayerUpdateRequest();
        updateRequest.setUsername(Optional.of("MyNewUsername")); // New name

        Player updatedPlayer = new Player();
        updatedPlayer.setId(playerId);
        updatedPlayer.setUsername("MyNewUsername");

        // Mock the repository findById to return the existing player
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));

        // Mock the repository save method to return the updated player
        when(playerRepository.save(existingPlayer)).thenReturn(updatedPlayer);

        // Act: Call the updatePlayer() method
        Player result = playerServiceImpl.updatePlayer(playerId, updateRequest);

        // Assert: Verify the player's name was updated correctly
        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals("MyNewUsername", result.getUsername()); // Username should be updated to "MyNewUsername"

        // Verify that findById was called once
        verify(playerRepository, times(1)).findById(playerId);

        // Verify that save was called once with the updated player
        verify(playerRepository, times(1)).save(existingPlayer);
    }

    @Test
    void updatePlayer_UpdatePlayerEmail_ReturnUpdatedPlayerEmail() {
        // Arrange: Mock the existing player and the update request
        Long playerId = 123L;
        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setEmail("123@email.com");

        PlayerUpdateRequest updateRequest = new PlayerUpdateRequest();
        updateRequest.setEmail(Optional.of("456@email.com")); // New name

        Player updatedPlayer = new Player();
        updatedPlayer.setId(playerId);
        updatedPlayer.setEmail("456@email.com");

        // Mock the repository findById to return the existing player
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));

        // Mock the repository save method to return the updated player
        when(playerRepository.save(existingPlayer)).thenReturn(updatedPlayer);

        // Act: Call the updatePlayer() method
        Player result = playerServiceImpl.updatePlayer(playerId, updateRequest);

        // Assert: Verify the player's name was updated correctly
        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals("456@email.com", result.getEmail()); // Username should be updated to "MyNewUsername"

        // Verify that findById was called once
        verify(playerRepository, times(1)).findById(playerId);

        // Verify that save was called once with the updated player
        verify(playerRepository, times(1)).save(existingPlayer);
    }

    @Test
    void deletePlayer_RemovePlayer_ReturnDeleted() {
        // Arrange
        Player player = new Player();
        Long playerId = 1L;
        player.setId(playerId);

        // Act: Call the deletePlayer() method
        playerServiceImpl.deletePlayer(playerId);

        // Assert: Verify that playerRepository.deleteById() was called once with the correct ID
        verify(playerRepository, times(1)).deleteById(playerId);
    }

    @Test
    void deletePlayer_NoSuchId_ShouldThrowException() {
        // Arrange: Mock the repository to throw an EmptyResultDataAccessException for a non-existent ID
        Long playerId = 999L; // Simulate a non-existent player ID
        doThrow(new EmptyResultDataAccessException(1)).when(playerRepository).deleteById(playerId);

        // Act & Assert: Verify that the EmptyResultDataAccessException is thrown
        assertThrows(EntityNotFoundException.class, () -> playerServiceImpl.deletePlayer(playerId));

        // Verify that deleteById() was called once with the non-existent ID
        verify(playerRepository, times(1)).deleteById(playerId);
    }
}