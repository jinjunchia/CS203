/**
 * Integration test for the PlayerController.
 * This class tests the endpoints related to player data retrieval and ranking in the PlayerController.
 */
package com.cs203.cs203system.service.IntegrationTest;

import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test class for {@link PlayerService}, testing the retrieval of player data and rankings
 * through PlayerController's endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    /**
     * Tests retrieving a player by ID.
     * Mocks the {@link PlayerService#findPlayerById(Long)} method to return a specific player DTO.
     * Verifies that the response contains the correct player data in JSON format.
     * @throws Exception if there is an error performing the request.
     */
    @Test
    public void testGetPlayerById() throws Exception {
        Long playerId = 1L;
        PlayerWithOutStatsDto playerDto = new PlayerWithOutStatsDto();
        playerDto.setId(playerId);

        when(playerService.findPlayerById(playerId)).thenReturn(Optional.of(playerDto));

        mockMvc.perform(get("/api/player/{playerId}", playerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(playerId));
    }

    /**
     * Tests retrieving all players.
     * Mocks the {@link PlayerService#findAllPlayers()} method to return a list of player DTOs.
     * Verifies that the response contains all players' data in JSON format.
     * @throws Exception if there is an error performing the request.
     */
    @Test
    public void testGetAllPlayers() throws Exception {
        PlayerWithOutStatsDto player1 = new PlayerWithOutStatsDto();
        player1.setId(1L);
        PlayerWithOutStatsDto player2 = new PlayerWithOutStatsDto();
        player2.setId(2L);
        List<PlayerWithOutStatsDto> players = Arrays.asList(player1, player2);

        when(playerService.findAllPlayers()).thenReturn(players);

        mockMvc.perform(get("/api/player"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    /**
     * Tests retrieving players by ranking.
     * Mocks the {@link PlayerService#findAllPlayersOrderByEloRating()} method to return a list of players ordered by ELO rating.
     * Verifies that the response contains players' ranking data in JSON format.
     * @throws Exception if there is an error performing the request.
     */
    @Test
    public void testGetPlayerRanking() throws Exception {
        PlayerWithOutStatsDto player1 = new PlayerWithOutStatsDto();
        player1.setId(1L);
        PlayerWithOutStatsDto player2 = new PlayerWithOutStatsDto();
        player2.setId(2L);
        List<PlayerWithOutStatsDto> rankedPlayers = Arrays.asList(player1, player2);

        when(playerService.findAllPlayersOrderByEloRating()).thenReturn(rankedPlayers);

        mockMvc.perform(get("/api/player/ranking"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    /**
     * Tests retrieving a player by a non-existent ID.
     * Mocks the {@link PlayerService#findPlayerById(Long)} method to return an empty result.
     * Verifies that the response status is 404 Not Found.
     * @throws Exception if there is an error performing the request.
     */
    @Test
    public void testGetPlayerByIdNotFound() throws Exception {
        Long playerId = 10000000L;

        when(playerService.findPlayerById(playerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/player/{playerId}", playerId))
                .andExpect(status().isNotFound());
    }

}
