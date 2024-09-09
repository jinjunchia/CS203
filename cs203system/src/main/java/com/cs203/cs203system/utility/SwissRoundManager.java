package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.TeamRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class SwissRoundManager {

    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public SwissRoundManager(TournamentRepository tournamentRepository, MatchRepository matchRepository, TeamRepository teamRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    public void startSwissRounds(Tournament tournament) {
        List<Team> teams = teamRepository.findByTournament(tournament);
        int totalRounds = (int) Math.ceil(Math.log(teams.size()) / Math.log(2));  // Calculate rounds as log2(n)

        for (int roundNumber = 1; roundNumber <= totalRounds; roundNumber++) {
            runSwissRound(tournament, roundNumber, roundNumber == 1);
        }

        eliminateBottomHalfTeams(tournament);  // Eliminate bottom half of teams
    }

    public void runSwissRound(Tournament tournament, int roundNumber, boolean isFirstRound) {
        List<Team> teams = teamRepository.findByTournament(tournament);

        List<Match> matches = isFirstRound ? createRandomPairings(teams, roundNumber, tournament)
                : createPerformanceBasedPairings(teams, roundNumber, tournament);
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
            match.setTournament(tournament);
            match.setStatus(Match.Status.ONGOING);

            matches.add(match);
        }

        // Handle bye for odd number of teams
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
        teamWithBye.addPoints(1);

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
            match.setTournament(tournament);
            match.setStatus(Match.Status.ONGOING);

            matches.add(match);
        }

        // Handle bye for odd number of teams
        if (teams.size() % 2 != 0) {
            Team teamWithBye = teams.get(teams.size() - 1);
            assignFreeWin(teamWithBye, tournament, roundNumber);
        }
        return matches;
    }

    private void eliminateBottomHalfTeams(Tournament tournament) {
        List<Team> rankedTeams = teamRepository.findByTournamentOrderByEloRatingDesc(tournament);
        int halfSize = rankedTeams.size() / 2;  // Calculate half of the total teams

        // Keep the top half and eliminate the bottom half
        List<Team> topTeams = rankedTeams.subList(0, halfSize);
        List<Team> eliminatedTeams = rankedTeams.subList(halfSize, rankedTeams.size());

        topTeams.forEach(team -> team.setStatus(Team.Status.QUALIFIED));
        eliminatedTeams.forEach(team -> team.setStatus(Team.Status.ELIMINATED));

        teamRepository.saveAll(topTeams);
        teamRepository.saveAll(eliminatedTeams);
    }
}
