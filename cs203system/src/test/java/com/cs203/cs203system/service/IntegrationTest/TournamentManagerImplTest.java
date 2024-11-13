/**
 * Integration test class for the TournamentManagerService implementation.
 * This class verifies the functionality of creating, managing, and completing tournaments
 * using various tournament formats and scenarios.
 */
package com.cs203.cs203system.service.IntegrationTest;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.cs203.cs203system.service.TournamentManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link TournamentManagerService} covering various tournament lifecycle
 * operations like creating, updating players, starting, and finalizing tournaments.
 */
@SpringBootTest
public class TournamentManagerImplTest {

    private static final Logger logger = LoggerFactory.getLogger(TournamentManagerImplTest.class);

    @Autowired
    private TournamentManagerService tournamentManagerService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    private Tournament tournament;
    private List<Long> playerIds;

    /**
     * Initializes tournament and mock players before each test.
     * Creates a tournament with a default status of SCHEDULED and prepares
     * a list of mock players.
     */
    @BeforeEach
    public void setUp() {
        tournament = new Tournament();
        tournament.setStatus(TournamentStatus.SCHEDULED);
        playerIds = new ArrayList<>();

        // Creating mock players for testing
        for (int i = 0; i < 8; i++) {
            Player player = new Player();
            player.setName("Player " + (i + 1));
            player = playerRepository.save(player);
            playerIds.add(player.getId());
        }
    }

    /**
     * Prepares a tournament with the specified format.
     * @param format The format to set for the tournament.
     * @return The updated tournament with players added.
     */
    private Tournament prepareTournament(TournamentFormat format) {
        tournament.setFormat(format);
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        assertNotNull(createdTournament, "Tournament should be created successfully");
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus(), "Tournament should be in SCHEDULED status");

        Tournament updatedTournament = tournamentManagerService.updatePlayersToTournament(createdTournament.getId(), playerIds);
        assertEquals(playerIds.size(), updatedTournament.getPlayers().size(), "All players should be added to the tournament");
        return updatedTournament;
    }

    /**
     * Tests the entire lifecycle of a tournament for all formats.
     * Verifies that the tournament progresses through SCHEDULED, ONGOING, and COMPLETED statuses.
     * Ensures a winner is determined once the tournament is completed.
     * @param format The tournament format to test with.
     */
    @ParameterizedTest
    @EnumSource(TournamentFormat.class)
    @Transactional
    public void testTournamentLifecycle(TournamentFormat format) {
        Tournament createdTournament = prepareTournament(format);
        Tournament startedTournament = tournamentManagerService.startTournament(createdTournament.getId());
        assertEquals(TournamentStatus.ONGOING, startedTournament.getStatus(), "Tournament should be ONGOING after starting");

        while (startedTournament.getStatus() != TournamentStatus.COMPLETED) {
            List<Match> matchesCopy = new ArrayList<>(startedTournament.getMatches());
            System.out.println("Number of matches in this round: " + matchesCopy.size());

            for (Match match : matchesCopy) {
                match.setPlayer1Score(1);
                match.setPlayer2Score(0);
                match.setStatus(MatchStatus.PENDING);
                matchRepository.save(match);
                tournamentManagerService.inputResult(match);
            }

            startedTournament = tournamentManagerService.findTournamentById(startedTournament.getId()).orElseThrow();

            if (tournamentIsFinalized(startedTournament)) {
                startedTournament.setStatus(TournamentStatus.COMPLETED);
                tournamentRepository.save(startedTournament);
            }
        }

        Player winner = tournamentManagerService.determineWinner(startedTournament.getId());
        assertNotNull(winner, "Winner should be determined once the tournament is completed");
        System.out.println("The winner for format " + format + " is: " + winner.getName());
    }

    /**
     * Helper method to determine if a tournament can be finalized.
     * @param tournament The tournament to check.
     * @return True if the tournament is ready to be finalized, false otherwise.
     */
    private boolean tournamentIsFinalized(Tournament tournament) {
        return tournament.getMatches().stream().allMatch(m -> m.getStatus() == MatchStatus.COMPLETED);
    }

    /**
     * Tests the deletion of a tournament with SCHEDULED status.
     * Ensures that the tournament is deleted from the repository successfully.
     */
    @Test
    @Transactional
    public void testDeleteScheduledTournament() {
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        assertNotNull(createdTournament, "Tournament should be created successfully");

        Long tournamentId = createdTournament.getId();
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus(), "Tournament should be in SCHEDULED status");

        tournamentManagerService.deleteTournamentById(tournamentId);

        Optional<Tournament> deletedTournament = tournamentRepository.findById(tournamentId);
        assertTrue(deletedTournament.isEmpty(), "Tournament should be deleted successfully");
    }

    /**
     * Tests starting a tournament with insufficient players.
     * Ensures an exception is thrown when trying to start a tournament with less than 2 players.
     */
    @Test
    @Transactional
    public void testStartTournamentWithInsufficientPlayers() {
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        playerIds = playerIds.subList(0, 1);
        tournamentManagerService.updatePlayersToTournament(createdTournament.getId(), playerIds);

        assertThrows(RuntimeException.class, () -> {
            tournamentManagerService.startTournament(createdTournament.getId());
        }, "Starting a tournament with less than 2 players should throw an exception");
    }

    /**
     * Tests inputting an invalid match result.
     * Verifies that an exception is thrown if a match result with a negative score is provided.
     */
    @Test
    @Transactional
    public void testInputInvalidMatchResult() {
        Tournament createdTournament = prepareTournament(TournamentFormat.SWISS);
        Tournament startedTournament = tournamentManagerService.startTournament(createdTournament.getId());

        List<Match> matchesCopy = new ArrayList<>(startedTournament.getMatches());
        Match match = matchesCopy.get(0);
        match.setPlayer1Score(-1);

        assertThrows(RuntimeException.class, () -> {
            tournamentManagerService.inputResult(match);
        }, "Inputting a match result with a negative score should throw an exception");
    }
}
