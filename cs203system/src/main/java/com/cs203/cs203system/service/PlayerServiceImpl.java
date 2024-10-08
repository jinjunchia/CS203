package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.players.PlayerUpdateRequest;
import com.cs203.cs203system.dtos.players.CreatePlayerMapper;
import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDtoMapper;
import com.cs203.cs203system.dtos.players.PlayerWithOutStatsDto;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper;
    private final CreatePlayerMapper createPlayerMapper;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository,
                             PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper,
                             CreatePlayerMapper createPlayerMapper) {
        this.playerRepository = playerRepository;
        this.playerWithOutStatsDtoMapper = playerWithOutStatsDtoMapper;
        this.createPlayerMapper = createPlayerMapper;
    }

    @Override
    public List<PlayerWithOutStatsDto> findAllPlayers() {
        return playerRepository
                .findAll()
                .stream()
                .map(playerWithOutStatsDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PlayerWithOutStatsDto> findPlayerById(Long id) {
        return playerRepository
                .findById(id)
                .map(playerWithOutStatsDtoMapper::toDto);
    }

    @Override
    public PlayerWithOutStatsDto createPlayer(CreateUserRequest createUserRequest) {
        Player newPlayer = createPlayerMapper
                .toEntity(createUserRequest);
        return playerWithOutStatsDtoMapper
                .toDto(playerRepository.save(newPlayer));
    }

    @Override
    @Transactional
    public Player updatePlayer(Long id, PlayerUpdateRequest updateRequest) {
        Player player = playerRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Player with id " + id + " not found"));

        updateRequest.getName().ifPresent(player::setName);
        updateRequest.getEmail().ifPresent(player::setEmail);
        updateRequest.getUsername().ifPresent(player::setUsername);

        return playerRepository.save(player);
    }

    @Override
    public void deletePlayer(Long id) {
        try {
            playerRepository.deleteById(id);  // Attempt to delete the player by ID
        } catch (EmptyResultDataAccessException e) {
            // If the player doesn't exist, throw EntityNotFoundException
            throw new EntityNotFoundException("Player with ID " + id + " not found.");
        }
    }
}