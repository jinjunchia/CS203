package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Match-related operations.
 * This class provides methods to fetch matches by their ID or related to a specific tournament.
 */
@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;

    /**
     * Constructor for MatchServiceImpl.
     *
     * @param matchRepository Repository to interact with the match database.
     */
    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Find a match by its ID.
     *
     * @param id The ID of the match to be fetched.
     * @return The Match object if found.
     * @throws NotFoundException if the match with the given ID is not found.
     */
    public Match findMatchById(Long id) throws NotFoundException {
        return matchRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Tournament with id " + id + " not found"));
    }

    /**
     * Retrieve all matches from the repository.
     *
     * @return A list of all matches.
     */
    @Override
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    /**
     * Retrieve all matches related to a specific tournament by its ID.
     *
     * @param tournamentId The ID of the tournament.
     * @return A list of matches that belong to the specified tournament.
     */
    @Override
    public List<Match> findAllMatchesByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public List<Match> getMatchesOneDayBeforeMatch() {
        // Calculate start and end times for one day before now
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = now.plusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // Retrieve matches within this date range across all tournaments
        return matchRepository.findMatchesWithinDateRange(startDate, endDate);
    }
}
