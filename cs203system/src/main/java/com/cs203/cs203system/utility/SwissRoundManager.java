package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.TeamRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; // Add this line
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;

@Component // Add this line to make the class a Spring-managed component
public class SwissRoundManager {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    public void startSwissRound(Tournament tournament, int totalRounds) {
        for (int roundNumber = 1; roundNumber <= totalRounds; roundNumber++) {
            runSwissRound(tournament, roundNumber, roundNumber == 1);
        }
        eliminateBottomTeams(tournament);
    }

    public void runSwissRound(Tournament tournament, int roundNumber, boolean isFirstRound) {
        List<Team> teams = teamRepository.findByTournament(tournament);

        List<Match> matches = isFirstRound ? createRandomPairings(teams, roundNumber, tournament) : createPerformanceBasedPairings(teams, roundNumber, tournament);
        matchRepository.saveAll(matches);
    }

    private List<Match> createRandomPairings(List<Team> teams, int roundNumber, Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        Collections.shuffle(teams);

        for (int i = 0; i < teams.size() - 1; i += 2) {
            Team team1 = teams.get(i);
            Team team2 = teams.get(i + 1);

            Match match = new Match();
            match.setRoundNumber(roundNumber);
            match.setTeams(new LinkedHashSet<>(List.of(team1, team2)));
            match.setStatus(Match.Status.ONGOING);

            matches.add(match);
        }

        if (teams.size() % 2 != 0) {
            Team teamWithBye = teams.get(teams.size() - 1);
            assignFreeWin(teamWithBye, tournament, roundNumber);
        }
        return matches;
    }

    private void assignFreeWin(Team teamWithBye, Tournament tournament, int roundNumber) {
        Match byeMatch = new Match();
        byeMatch.setRoundNumber(roundNumber);
        byeMatch.setTournament(tournament);
        byeMatch.setTeams(new LinkedHashSet<>(List.of(teamWithBye)));
        byeMatch.setResult("WIN by bye");
        byeMatch.setStatus(Match.Status.COMPLETED);

        teamWithBye.setWins(teamWithBye.getWins() + 1);

        matchRepository.save(byeMatch);
        teamRepository.save(teamWithBye);
    }

    private List<Match> createPerformanceBasedPairings(List<Team> teams, int roundNumber, Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        teams.sort((team1, team2) -> Integer.compare(team2.getEloRating(), team1.getEloRating()));

        for (int i = 0; i < teams.size() - 1; i += 2) {
            Team team1 = teams.get(i);
            Team team2 = teams.get(i + 1);

            Match match = new Match();
            match.setRoundNumber(roundNumber);
            match.setTeams(new LinkedHashSet<>(List.of(team1, team2)));
            match.setStatus(Match.Status.ONGOING);

            matches.add(match);
        }

        if (teams.size() % 2 != 0) {
            Team teamWithBye = teams.get(teams.size() - 1);
            assignFreeWin(teamWithBye, tournament, roundNumber);
        }
        return matches;
    }

    private void eliminateBottomTeams(Tournament tournament) {
        List<Team> rankedTeams = teamRepository.findByTournamentOrderByEloRatingDesc(tournament);
        List<Team> topTeams = rankedTeams.subList(0, Math.min(16, rankedTeams.size()));
        List<Team> eliminatedTeams = rankedTeams.size() > 16 ? rankedTeams.subList(16, rankedTeams.size()) : new ArrayList<>();

        topTeams.forEach(team -> team.setStatus(Team.Status.QUALIFIED)); // Ensure this matches your model
        eliminatedTeams.forEach(team -> team.setStatus(Team.Status.ELIMINATED));

        teamRepository.saveAll(topTeams);
        teamRepository.saveAll(eliminatedTeams);
    }
}
