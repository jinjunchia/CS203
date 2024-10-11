package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.dtos.players.*;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.service.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing players.
 * Provides methods for CRUD operations related to Player entities.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper;
    private final CreatePlayerMapper createPlayerMapper;

    /**
     * Constructs a PlayerServiceImpl with the required dependencies.
     *
     * @param playerRepository the repository for accessing player data
     * @param playerWithOutStatsDtoMapper the mapper for converting Player entities to PlayerWithOutStatsDto
     * @param createPlayerMapper the mapper for converting CreateUserRequest to Player entity
     */
    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository,
                             PlayerWithOutStatsDtoMapper playerWithOutStatsDtoMapper,
                             CreatePlayerMapper createPlayerMapper) {
        this.playerRepository = playerRepository;
        this.playerWithOutStatsDtoMapper = playerWithOutStatsDtoMapper;
        this.createPlayerMapper = createPlayerMapper;
    }

    /**
     * Retrieves a list of all players.
     *
     * @return a list of PlayerWithOutStatsDto representing all players
     */
    @Override
    public List<PlayerWithOutStatsDto> findAllPlayers() {
        return playerRepository
                .findAll()
                .stream()
                .map(playerWithOutStatsDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds a player by their ID.
     *
     * @param id the ID of the player to find
     * @return an Optional containing the PlayerWithOutStatsDto if found, or empty if not found
     */
    @Override
    public Optional<PlayerWithOutStatsDto> findPlayerById(Long id) {
        return playerRepository
                .findById(id)
                .map(playerWithOutStatsDtoMapper::toDto);
    }

    /**
     * Creates a new player.
     *
     * @param createUserRequest the request data for creating a new player
     * @return the created player as a PlayerWithOutStatsDto
     */
    @Override
    @Transactional
    public PlayerWithOutStatsDto createPlayer(CreateUserRequest createUserRequest) {
        Player newPlayer = createPlayerMapper
                .toEntity(createUserRequest);
        return playerWithOutStatsDtoMapper
                .toDto(playerRepository.save(newPlayer));
    }

    /**
     * Updates an existing player by their ID.
     *
     * @param id the ID of the player to update
     * @param updateRequest the request data containing updated player information
     * @return the updated Player entity
     */
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

    /**
     * Deletes a player by their ID.
     *
     * @param id the ID of the player to delete
     * @throws EntityNotFoundException if the player with the given ID is not found
     */
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