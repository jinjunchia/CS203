package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.TournamentUpdateRequest;
import com.cs203.cs203system.exceptions.TournamentNotFoundException;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository, TeamRepository teamRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
    }


    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Optional<Tournament> findTournamentById(Integer id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament id of " + id + " does not exist");
        }
        return tournamentRepository.findById(id);
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament updateTournament(Integer id, TournamentUpdateRequest updateRequest) {
        Optional<Tournament> existingTournament = this.findTournamentById(updateRequest.getId());

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

    @Override
    @Transactional
    public void addTeamToTournament(Integer tournamentId, Integer teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        // Assume TeamService or direct repository access is available
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        tournament.getTeams().add(team);
        tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void removeTeamFromTournament(Integer tournamentId, Integer teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        // Same assumption as above
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        tournament.getTeams().remove(team);
        tournamentRepository.save(tournament);
    }

    @Override
    public boolean existingTournament(Integer tournamentId) {
        return tournamentRepository.existsById(tournamentId);
    }

}