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

    private Tournament prepareTournament(TournamentFormat format) {
        tournament.setFormat(format);
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        assertNotNull(createdTournament, "Tournament should be created successfully");
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus(), "Tournament should be in SCHEDULED status");

        Tournament updatedTournament = tournamentManagerService.updatePlayersToTournament(createdTournament.getId(), playerIds);
        assertEquals(playerIds.size(), updatedTournament.getPlayers().size(), "All players should be added to the tournament");
        return updatedTournament;
    }

    @ParameterizedTest
    @EnumSource(TournamentFormat.class)
    @Transactional
    public void testTournamentLifecycle(TournamentFormat format) {
        // Prepare and start the tournament
        Tournament createdTournament = prepareTournament(format);
        Tournament startedTournament = tournamentManagerService.startTournament(createdTournament.getId());
        assertEquals(TournamentStatus.ONGOING, startedTournament.getStatus(), "Tournament should be ONGOING after starting");

        // Continue processing rounds until the tournament is complete
        while (startedTournament.getStatus() != TournamentStatus.COMPLETED) {
            // Fetch the latest matches for the current round
            List<Match> matchesCopy = new ArrayList<>(startedTournament.getMatches());
            System.out.println("Number of matches in this round: " + matchesCopy.size());

            for (Match match : matchesCopy) {
                // Assume Player 1 wins each match for simplicity
                match.setPlayer1Score(1);
                match.setPlayer2Score(0);
                match.setStatus(MatchStatus.PENDING);
                matchRepository.save(match);  // Save each match to mark as completed
                tournamentManagerService.inputResult(match);
            }

            // Reload the tournament to check if it progressed to the next round or completed
            startedTournament = tournamentManagerService.findTournamentById(startedTournament.getId()).orElseThrow();

            // If all rounds are completed, mark the tournament as COMPLETED
            if (tournamentIsFinalized(startedTournament)) {
                startedTournament.setStatus(TournamentStatus.COMPLETED);
                tournamentRepository.save(startedTournament);
            }
        }

        // Determine the winner and validate the result
        Player winner = tournamentManagerService.determineWinner(startedTournament.getId());
        assertNotNull(winner, "Winner should be determined once the tournament is completed");
        System.out.println("The winner for format " + format + " is: " + winner.getName());
    }

    // Helper method to check if the tournament can be finalized
    private boolean tournamentIsFinalized(Tournament tournament) {
        // Implement logic here to check if all rounds are complete.
        // For example, you might check if there are no pending matches or if the format rules are met.
        // This would vary depending on the structure of the tournament.
        return tournament.getMatches().stream().allMatch(m -> m.getStatus() == MatchStatus.COMPLETED);
    }

    @Test
    @Transactional
    public void testDeleteScheduledTournament() {
        // Create and delete a tournament with SCHEDULED status
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        assertNotNull(createdTournament, "Tournament should be created successfully");

        Long tournamentId = createdTournament.getId();
        assertEquals(TournamentStatus.SCHEDULED, createdTournament.getStatus(), "Tournament should be in SCHEDULED status");

        tournamentManagerService.deleteTournamentById(tournamentId);

        Optional<Tournament> deletedTournament = tournamentRepository.findById(tournamentId);
        assertTrue(deletedTournament.isEmpty(), "Tournament should be deleted successfully");
    }

    @Test
    @Transactional
    public void testStartTournamentWithInsufficientPlayers() {
        // Create a tournament with only one player
        Tournament createdTournament = tournamentManagerService.createTournament(tournament);
        playerIds = playerIds.subList(0, 1); // Limit to only one player
        tournamentManagerService.updatePlayersToTournament(createdTournament.getId(), playerIds);

        assertThrows(RuntimeException.class, () -> {
            tournamentManagerService.startTournament(createdTournament.getId());
        }, "Starting a tournament with less than 2 players should throw an exception");
    }

    @Test
    @Transactional
    public void testInputInvalidMatchResult() {
        Tournament createdTournament = prepareTournament(TournamentFormat.SWISS);
        Tournament startedTournament = tournamentManagerService.startTournament(createdTournament.getId());

        List<Match> matchesCopy = new ArrayList<>(startedTournament.getMatches());
        Match match = matchesCopy.get(0);
        match.setPlayer1Score(-1); // Invalid score

        assertThrows(RuntimeException.class, () -> {
            tournamentManagerService.inputResult(match);
        }, "Inputting a match result with a negative score should throw an exception");
    }
}
