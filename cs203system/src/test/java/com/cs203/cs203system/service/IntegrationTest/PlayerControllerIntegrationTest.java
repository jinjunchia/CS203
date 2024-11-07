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

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Test
    public void testGetPlayerById() throws Exception {
        Long playerId = 1L;
        PlayerWithOutStatsDto playerDto = new PlayerWithOutStatsDto();
        // Assuming PlayerWithOutStatsDto has a setId method
        playerDto.setId(playerId);

        when(playerService.findPlayerById(playerId)).thenReturn(Optional.of(playerDto));

    }

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

    @Test
    public void testGetPlayerByIdNotFound() throws Exception {
        Long playerId = 10000000L;

        when(playerService.findPlayerById(playerId)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(get("/api/player/{playerId}", playerId))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
