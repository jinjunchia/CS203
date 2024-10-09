package com.cs203.cs203system.service;

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
        return playerRepository.findByTournaments_Id(tournament.getId());
    }

    @Override
    @Transactional
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Optional<Tournament> findTournamentById(Long id) {
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
    public Tournament updateTournament(Long id, TournamentUpdateRequest updateRequest) {
        Tournament tournament = tournamentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Tournament with id " + id + " not found"));

        updateRequest.getName().ifPresent(tournament::setName);
        updateRequest.getVenue().ifPresent(tournament::setLocation);
        updateRequest.getStartDate().ifPresent(tournament::setStartDate);
        updateRequest.getEndDate().ifPresent(tournament::setEndDate);

        return tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void deleteTournamentById(Long id) {
        // Ensure the tournament exists before deletion
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        tournamentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public String addPlayerToTournament(Long tournamentId, List<Long> playerId) {
        Optional<Tournament> optionalTournament = tournamentRepository.findById(tournamentId);
        Tournament tournament = optionalTournament.orElseThrow(() ->
                new NotFoundException("Tournament with id " + tournamentId + " not found."));
        System.out.println(tournament);

        for (Long id : playerId) {
            System.out.println("Player ID: " + id);
            Optional<Player> optionalPlayer = playerRepository.findById(id);
            Player player = optionalPlayer.orElseThrow(() ->
                    new NotFoundException("Player with id " + id + " not found."));
            System.out.println(player.getName());
            tournament.getPlayers().add(player);
        }
        tournamentRepository.save(tournament);
        return "";
    }
}
