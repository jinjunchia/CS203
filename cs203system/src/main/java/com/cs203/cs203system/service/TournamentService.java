package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    // TODO: Add Pagination
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    /**
     * Find Tournament by ID
     *
     * @param id
     * @return
     */
    public Optional<Tournament> getTournamentById(Integer id) {
        return tournamentRepository.findById(id);
    }

    public Optional<Tournament> getTournamentByName(String name) {
        return null;
    }
//public interface TournamentService {
//    Tournament createTournament(Tournament tournament);
//    Optional<Tournament> findTournamentById(Integer id);
//    List<Tournament> findAllTournaments();
//    Tournament updateTournament(Tournament tournament);
//    void deleteTournament(Integer id);
//    void addTeamToTournament(Integer tournamentId, Integer teamId);
//    void removeTeamFromTournament(Integer tournamentId, Integer teamId);
}