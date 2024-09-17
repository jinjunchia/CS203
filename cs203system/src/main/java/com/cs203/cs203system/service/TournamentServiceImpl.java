package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.TournamentUpdateRequest;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }
    @Override
    public List<Player> getPlayersForTournament(Tournament tournament) {
        return playerRepository.findByTournamentId(tournament.getId());
    }

    @Override
    @Transactional
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Optional<Tournament> findTournamentById(Integer id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        return tournament; // Simply return the found tournament Optional
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    @Transactional
    public Tournament updateTournament(Integer id, TournamentUpdateRequest updateRequest) {
        Tournament tournament = findTournamentById(id).orElseThrow(() -> new NotFoundException("Tournament with id " + id + " not found"));

        // Update fields based on request
        updateRequest.getName().ifPresent(tournament::setName);
        updateRequest.getVenue().ifPresent(tournament::setLocation);
        updateRequest.getStartDate().ifPresent(tournament::setStartDate);
        updateRequest.getEndDate().ifPresent(tournament::setEndDate);

        return tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void deleteTournamentById(Integer id) {
        // Ensure the tournament exists before deletion
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        tournamentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public String addPlayerToTournament(Integer tournamentId, Integer playerId) {
        Optional<Tournament> optionalTournament = tournamentRepository.findById(tournamentId);
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalTournament.isPresent() && optionalPlayer.isPresent()) {
            Tournament tournament = optionalTournament.get();
            Player player = optionalPlayer.get();

            // Check if player's ELO rating is within the tournament's ELO restrictions
            if (player.getEloRating() >= tournament.getMinEloRating() && player.getEloRating() <= tournament.getMaxEloRating()) {
                tournament.getPlayers().add(player);
                tournamentRepository.save(tournament);
                return "Player added successfully.";
            } else {
                return "Player's ELO rating is not within the allowed range for this tournament.";
            }
        } else {
            // Handle case where either the tournament or player is not found
            if (optionalTournament.isEmpty()) {
                throw new NotFoundException("Tournament with id " + tournamentId + " not found.");
            }
            if (optionalPlayer.isEmpty()) {
                throw new NotFoundException("Player with id " + playerId + " not found.");
            }
            return "Tournament or Player not found."; // Redundant due to exceptions
        }
    }
}
