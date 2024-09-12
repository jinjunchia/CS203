package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.TournamentUpdateRequest;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.repository.PlayerRepository;
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
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Optional<Tournament> findTournamentById(Integer id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new NotFoundException("Tournament id of " + id + " does not exist");
        }
        return tournamentRepository.findById(id);
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament updateTournament(Integer id, TournamentUpdateRequest updateRequest) {
        Optional<Tournament> existingTournament = this.findTournamentById(id);

        // TODO: Add error handling
        if (existingTournament.isEmpty()) {
//            throw new PlayerNotFoundException("Player with id " + id + " not found");
            return null;
        }

        Tournament tournament = existingTournament.get();

        updateRequest.getName().ifPresent(tournament::setName);
        updateRequest.getVenue().ifPresent(tournament::setVenue);
        updateRequest.getStartDate().ifPresent(tournament::setStartDate);
        updateRequest.getEndDate().ifPresent(tournament::setEndDate);

        return tournamentRepository.save(tournament);
    }

    @Override
    public void deleteTournamentById(Integer id) {
        tournamentRepository.deleteById(id);
    }

}