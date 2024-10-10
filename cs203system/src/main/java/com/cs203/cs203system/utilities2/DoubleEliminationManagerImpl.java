package com.cs203.cs203system.utilities2;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class DoubleEliminationManagerImpl implements DoubleEliminationManager {

    private HashMap<Integer, List<Integer>> dependencyGraph8;
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public DoubleEliminationManagerImpl(TournamentRepository tournamentRepository, MatchRepository matchRepository,
                                        PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    // To start the DoubleElimination tournament
    // Will only be used at the start of the double elimination
    // Will randomise and pair players in the tournament
    // All players will be in the WinnerBracket in the Tournament Entity at the start
    //
    @Override
    @Transactional
    public Tournament initializeDoubleElimination(Tournament tournament) {
        log.info("Initializing Double Elimination");

        tournament.getPlayers().forEach(player -> tournament.getWinnersBracket().add(player));

        Match[] upperMatches = new Match[tournament.getPlayers().size() - 1];
        Match[] lowerMatches = new Match[tournament.getPlayers().size() - 1];

        // Create Upper Bracket and Lower Bracket
        for (int i = 0; i < tournament.getPlayers().size() - 1; i++) {
            Match upperMatch = Match.builder()
                    .tournament(tournament)
                    .matchDate(LocalDate.now())
                    .status(MatchStatus.WAITING)
                    .bracket(MatchBracket.UPPER)
                    .build();

            Match lowerMatch = Match.builder()
                    .tournament(tournament)
                    .matchDate(LocalDate.now())
                    .status(MatchStatus.WAITING)
                    .bracket(MatchBracket.LOWER)
                    .build();


            tournament.getMatches().addAll(List.of(upperMatch, lowerMatch));
            upperMatches[i] = upperMatch;
            lowerMatches[i] = lowerMatch;
        }

        DependencyGraph dependencyGraph = DependencyGraphMapper.run();
        Map<String, List<Integer>> players8 = dependencyGraph.getPlayers8();

        ArrayList<Player> shuffled = new ArrayList<>(tournament.getPlayers());
        Collections.shuffle(shuffled);

        Map<String, Match> map = new HashMap<>();
        for (int i = 1; i <= tournament.getMatches().size(); i++) {
            map.put(Integer.toString(i), tournament.getMatches().get(i - 1));
        }

        for (Map.Entry<String, Match> entry : map.entrySet()) {
            String key = entry.getKey();
            Match match = entry.getValue();

            List<Integer> dependencies = players8.get(key);
            dependencies.forEach(dependency -> {
                Match matchDependency = map.get(Integer.toString(dependency));
                match.getDependencies().add(matchDependency);
                matchDependency.getDependentMatches().add(match);
            });

            if (dependencies.isEmpty()) {
                match.setPlayer1(shuffled.get(0));
                match.setPlayer2(shuffled.get(1));
                match.setStatus(MatchStatus.SCHEDULED);
                shuffled.remove(0);
                shuffled.remove(0);
            }
        }

        matchRepository.saveAll(Arrays.asList(upperMatches));
        matchRepository.saveAll(Arrays.asList(lowerMatches));

        return tournamentRepository.save(tournament);  // Save tournament at the end
    }

    // This method will receive the results of a completed match
    // It will keep the winner in the same bracket, but move the loser to the loser bracket
    // It will update winner's and loser's stats
    // It will check the next opponent (Which is the closest player to the right in the arraylist)
    //      a) if the next opponent is still completing previous match, still create a match that has status of WAITING
    //      b) if the next opponent is already done (there should be a match in DB), the new match will be SCHEDULED
    // Note that the above is done for both the winner and the loser
    @Override
    @Transactional
    public Tournament receiveMatchResult(Match match) {
        Tournament tournament = tournamentRepository
                .findById(match
                        .getTournament()
                        .getId())
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        Player winner = match.getWinner(), loser = match.getLoser();
        tournament.getWinnersBracket().remove(loser);
        tournament.getLosersBracket().add(loser);

        // Here lies all the methods to update the stats of the player (both winner and loser)

        // I will move the winner into a next match and the loser into the next match.
        // I need to find the next match and determine if UPPER OR LOWER
        Set<Match> nextMatches = match.getDependentMatches();

        Match upperMatch = nextMatches.iterator().next();
        nextMatches.remove(upperMatch);
        Match lowerMatch = nextMatches.iterator().next();
        nextMatches.remove(lowerMatch);

        // Move all winner to the next upper match

        tournamentRepository.save(tournament);
        return null;

    }


}

class DependencyGraphMapper {
    public static DependencyGraph run() {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyGraph dependencyGraph = null;
        try {
            dependencyGraph = objectMapper.readValue(new File(System.getProperty("user.dir") + "/src/main/resources/data.json"), DependencyGraph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dependencyGraph;
    }
}

@Setter
@Getter
@ToString
class DependencyGraph {
    private Map<String, List<Integer>> players8;
    private Map<String, List<Integer>> players16;


}



