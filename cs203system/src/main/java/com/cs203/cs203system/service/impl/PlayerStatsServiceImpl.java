package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.dtos.players.PlayerStatsDTO;
import com.cs203.cs203system.dtos.players.PlayerStatsDTOMapper;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.PlayerStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class PlayerStatsServiceImpl implements PlayerStatsService {

    private final MatchRepository matchRepository;

    @Autowired
    public PlayerStatsServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public List<PlayerStatsDTO> getAllPlayerStats() {
        List<Match> matches = matchRepository.findAll();
        Map<Long, PlayerStatsDTO> playerStatsMap = new HashMap<>();

        for (Match match : matches) {
            // Update stats for player1
            playerStatsMap.computeIfAbsent(match.getPlayer1().getId(), id ->
                            PlayerStatsDTOMapper.mapToDto(match.getPlayer1(), 0, 0, 0))
                    .addPunches(match.getPunchesPlayer1())
                    .addDodges(match.getDodgesPlayer1())
                    .addKOs(match.isKoByPlayer1() ? 1 : 0);

            // Update stats for player2
            playerStatsMap.computeIfAbsent(match.getPlayer2().getId(), id ->
                            PlayerStatsDTOMapper.mapToDto(match.getPlayer2(), 0, 0, 0))
                    .addPunches(match.getPunchesPlayer2())
                    .addDodges(match.getDodgesPlayer2())
                    .addKOs(match.isKoByPlayer2() ? 1 : 0);
        }

        return new ArrayList<>(playerStatsMap.values());
    }

    @Override
    public PlayerStatsDTO getPlayerStatsByPlayerId(Long playerId) {
        List<Match> matches = matchRepository.findByPlayerId(playerId);
        if (matches.isEmpty()) {
            return new PlayerStatsDTO(playerId, "", 0, 0 ,0);
        }
        Map<Long, PlayerStatsDTO> playerStatsMap = new HashMap<>();

        for (Match match : matches) {
            playerStatsMap.computeIfAbsent(match.getPlayer1().getId(), id ->
                            PlayerStatsDTOMapper.mapToDto(match.getPlayer1(), 0, 0, 0))
                    .addPunches(match.getPunchesPlayer1())
                    .addDodges(match.getDodgesPlayer1())
                    .addKOs(match.isKoByPlayer1() ? 1 : 0);

            playerStatsMap.computeIfAbsent(match.getPlayer2().getId(), id ->
                            PlayerStatsDTOMapper.mapToDto(match.getPlayer2(), 0, 0, 0))
                    .addPunches(match.getPunchesPlayer2())
                    .addDodges(match.getDodgesPlayer2())
                    .addKOs(match.isKoByPlayer2() ? 1 : 0);
        }

        return playerStatsMap.get(playerId);
    }
}

