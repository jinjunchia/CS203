package com.cs203.cs203system.utility;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class TournamentManagerImpl implements TournamentManager {

    @Autowired
    private SwissRoundManager swissRoundManager;

    @Autowired
    private DoubleEliminationManager doubleEliminationManager;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    public List<Player> getPlayersForTournament(Tournament tournament) {
        return playerRepository.findByTournamentId(tournament.getId());
    }

    @Override
    public void initializeTournament(Tournament tournament) {
        setTournamentDetails(tournament);
        startTournament(tournament);

        List<Player> players = getPlayersForTournament(tournament);
        switch (tournament.getFormat()) {
            case SWISS:
                swissRoundManager.initializeRounds(tournament);
                break;
            case DOUBLE_ELIMINATION:
                doubleEliminationManager.initializeDoubleElimination(tournament, players);
                break;
            case HYBRID:
                swissRoundManager.initializeRounds(tournament);
                break;
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }

        tournamentRepository.save(tournament);
    }


    @Override
    public void progressTournament(Tournament tournament) {
        switch (tournament.getFormat()) {
            case SWISS:
                swissRoundManager.updateStandings(tournament);
                break;
            case DOUBLE_ELIMINATION:
                doubleEliminationManager.updateStandings(tournament);
                break;
            case HYBRID:
                if (!swissRoundManager.isSwissPhaseComplete(tournament)) {
                    swissRoundManager.updateStandings(tournament);
                } else if (tournament.getRoundsCompleted() == tournament.getTotalSwissRounds()) {
                    List<Player> topPlayers = swissRoundManager.getTopPlayers(tournament);
                    doubleEliminationManager.initializeDoubleElimination(tournament, topPlayers);
                    doubleEliminationManager.updateStandings(tournament);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }

        tournamentRepository.save(tournament);
    }

    @Override
    public boolean isTournamentComplete(Tournament tournament) {
        switch (tournament.getFormat()) {
            case SWISS:
                return swissRoundManager.isSwissPhaseComplete(tournament);
            case DOUBLE_ELIMINATION:
                return doubleEliminationManager.isDoubleEliminationComplete(tournament);
            case HYBRID:
                return swissRoundManager.isSwissPhaseComplete(tournament)
                        && doubleEliminationManager.isDoubleEliminationComplete(tournament);
            default:
                throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
        }
    }

    @Override
    public Player determineWinner(Tournament tournament) {
        if (isTournamentComplete(tournament)) {
            completeTournament(tournament);
            switch (tournament.getFormat()) {
                case SWISS:
                    return swissRoundManager.determineSwissWinner(tournament);
                case DOUBLE_ELIMINATION:
                    return doubleEliminationManager.determineWinner(tournament);
                case HYBRID:
                    return doubleEliminationManager.determineWinner(tournament);
                default:
                    throw new IllegalArgumentException("Unsupported tournament format: " + tournament.getFormat());
            }
        }
        return null;
    }

    @Override
    public void setTournamentDetails(Tournament tournament) {
        // randomly set start and end dates
        LocalDate startDate = LocalDate.now().plusDays(random.nextInt(10)); // Start in 0-9 days
        LocalDate endDate = startDate.plusDays(5 + random.nextInt(5)); // Duration of 5-9 days
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);

        // randomly set a location from a list
        List<String> locations = Arrays.asList("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");
        tournament.setLocation(locations.get(random.nextInt(locations.size())));

        // set the tournament status to SCHEDULED initially
        tournament.setStatus(TournamentStatus.SCHEDULED);

        // Set a random tournament format? i will just put it here
        TournamentFormat[] formats = TournamentFormat.values();
        tournament.setFormat(formats[random.nextInt(formats.length)]);

        tournamentRepository.save(tournament);
    }

    @Override
    public void startTournament(Tournament tournament) {
        tournament.setStatus(TournamentStatus.ONGOING);
        tournamentRepository.save(tournament);
    }

    @Override
    public void completeTournament(Tournament tournament) {
        tournament.setStatus(TournamentStatus.COMPLETED);
        tournamentRepository.save(tournament);
    }
}
