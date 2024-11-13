package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.service.MatchService;
import jakarta.transaction.Transactional;
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
                        new NotFoundException("Match with id " + id + " not found"));
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

    @Transactional
    public void updateAndSaveMatchStats(Long matchId, Integer punchesPlayer1, Integer punchesPlayer2,
                                        Integer dodgesPlayer1, Integer dodgesPlayer2,
                                        boolean koByPlayer1, boolean koByPlayer2) {
        Match match = findMatchById(matchId); // Assumes findMatchById throws NotFoundException if not found

        // Update punches and dodges
        match.setPunchesPlayer1(match.getPunchesPlayer1() + (punchesPlayer1 != null ? punchesPlayer1 : 0));
        match.setPunchesPlayer2(match.getPunchesPlayer2() + (punchesPlayer2 != null ? punchesPlayer2 : 0));
        match.setDodgesPlayer1(match.getDodgesPlayer1() + (dodgesPlayer1 != null ? dodgesPlayer1 : 0));
        match.setDodgesPlayer2(match.getDodgesPlayer2() + (dodgesPlayer2 != null ? dodgesPlayer2 : 0));

        // Update KOs based on boolean flags
        match.setKoByPlayer1(koByPlayer1);
        match.setKoByPlayer2(koByPlayer2);

        matchRepository.save(match);

    }

    @Override
    public List<Match> findMatchesByPlayerId(Long playerId) {
        return matchRepository.findByPlayerId(playerId);
    }

    @Override
    public Match updateMatchDetails(Long id, Match updatedMatch) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match with id " + id + " not found"));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new NotFoundException("Match with id " + id + " already completed");
        }

        match.setDescription(updatedMatch.getDescription());
        match.setMatchDate(updatedMatch.getMatchDate());

        return matchRepository.save(match);
    }
}
