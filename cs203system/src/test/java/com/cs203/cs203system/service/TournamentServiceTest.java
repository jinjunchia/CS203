//package com.cs203.cs203system.service;
//
//import com.cs203.cs203system.exceptions.NotFoundException;
//import com.cs203.cs203system.model.Tournament;
//import com.cs203.cs203system.repository.PlayerRepository;
//import com.cs203.cs203system.repository.TournamentRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.aspectj.lang.annotation.After;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.dao.EmptyResultDataAccessException;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//public class TournamentServiceTest {
//
//    @InjectMocks
//    TournamentManagerServiceImpl tournamentServiceImpl;
//
//    @Mock
//    TournamentRepository tournamentRepository;
//
//    @Mock
//    PlayerRepository playerRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);  // Initialize mocks
//    }
//
//    @AfterEach
//    void tearDown() {
//        // Reset the mocks after each test to ensure they don't retain state
//        Mockito.reset(tournamentRepository, playerRepository);
//    }
//
//    @Test
//    void createTournament_tournamentCreated() {
//        // Arrange
//        Tournament tournament = new Tournament();
//        tournament.setName("Test Tournament");
//
//        // Mocking tournamentRepository.save() to return the same tournament
//        when(tournamentRepository.save(tournament)).thenReturn(tournament);
//
//        // Act
////        Tournament createdTournament = tournamentServiceImpl.createTournament(tournament);
//
//        // Assert
//        assertNotNull(createdTournament);
//        assertEquals("Test Tournament", createdTournament.getName());
//
//        // Verify that save was called once
//        verify(tournamentRepository, times(1)).save(tournament);
//    }
//
//    @Test
//    void findTournamentById_TournamentFound_ShouldReturnTournament() {
//        // Arrange
//        Long tournamentId = 1L;
//        Tournament tournament = new Tournament();
//        tournament.setId(tournamentId);
//        tournament.setName("Test Tournament");
//
//        // Mocking tournamentRepository.findById() to return an Optional containing the tournament
//        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
//
//        // Act
//        Optional<Tournament> foundTournament = tournamentManagerServiceImpl.findTournamentById(tournamentId);
//
//        // Assert
//        assertTrue(foundTournament.isPresent());
//        assertEquals(tournamentId, foundTournament.get().getId());
//        assertEquals("Test Tournament", foundTournament.get().getName());
//
//        // Verify that findById was called once
//        verify(tournamentRepository, times(1)).findById(tournamentId);
//    }
//
//    @Test
//    void findTournamentById_TournamentNotFound_ReturnNotFoundException() {
//        // Arrange
//        Long tournamentId = 1L;  // Simulate a non-existent tournament ID
//
//        // Mocking the repository to return an empty Optional (tournament not found)
//        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
//            tournamentServiceImpl.findTournamentById(tournamentId);
//        });
//
//        // Assert the exception message
//        assertEquals("Tournament id of " + tournamentId + " does not exist", exception.getMessage());
//
//        // Verify that the repository's findById was called exactly once
//        verify(tournamentRepository, times(1)).findById(tournamentId);
//    }
//}
