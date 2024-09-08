package com.cs203.cs203system.utility;

import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import com.cs203.cs203system.repository.TeamRepository;
import com.cs203.cs203system.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

import java.util.Arrays;
import java.util.List;

@Component
public class DoubleEliminationManager {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private TeamRepository teamRepository;

    //start with double elimination phase
    public void startDoubleElimination(Tournament tournament){
        //get 16 team then seed team into winner and loser bracket then run
        List<Team> teams = teamRepository.findTop16ByTournament(tournament);
        seedTeamIntoBracket(teams);
        runDoubleElimination(tournament);
    }

    //seed team to double elimination
    public void seedTeamIntoBracket(List<Team> teams){
        int midpoint = teams.size()/2;
        List<Team> winnersBracket = teams.subList(0,midpoint);
        List<Team> loserBracket = teams.subList(midpoint,teams.size());
        //create match in winner bracket
        for(int i = 0; i < winnersBracket.size() - 1; i += 2){
            Team team1 = winnersBracket.get(i);
            Team team2 = winnersBracket.get(i+1);
            Match match = new Match();
            match.setTeams(new LinkedHashSet<>(Arrays.asList(team1,team2)));
            match.setTournament(tournamentRepository.findById(team1.getTournament().getId()).orElse(null));
            match.setRoundNumber(1);
            match.setBracket(Match.Bracket.WINNERS);
            match.setStatus(Match.Status.ONGOING);
            matchRepository.save(match);
        }

        //handle the byes in winner bracket if there are odd number
        if(winnersBracket.size() % 2 != 0){
            Team teamWithBye = winnersBracket.get(winnersBracket.size() - 1);

            Match byeMatch = new Match();
            byeMatch.setTeams(new LinkedHashSet<>(Arrays.asList(teamWithBye)));
            byeMatch.setTournament(tournamentRepository.findById(teamWithBye.getTournament().getId()).orElse(null));
            byeMatch.setRoundNumber(1);
            byeMatch.setBracket(Match.Bracket.WINNERS);
            byeMatch.setStatus(Match.Status.BYE);
            matchRepository.save(byeMatch);
        }

        for (Team team: loserBracket){
            Match initialLosersMatch = new Match();
            initialLosersMatch.setTeams(new LinkedHashSet<>(Arrays.asList(team)));
            initialLosersMatch.setTournament(tournamentRepository.findById(team.getTournament().getId()).orElse(null));
            initialLosersMatch.setRoundNumber(1);
            initialLosersMatch.setBracket(Match.Bracket.LOSERS);
            initialLosersMatch.setStatus(Match.Status.WAITING);

            matchRepository.save(initialLosersMatch);
        }
    }
    private void runDoubleElimination(Tournament tournament) {
        boolean tournamentComplete = false;

        while (!tournamentComplete) {
            //fetch ongoing matches
            List<Match> ongoingMatches = matchRepository.findByTournamentAndStatus(tournament, Match.Status.ONGOING);

            for (Match match : ongoingMatches) {
                //simulate or determine match results? i not too sure how to determine yet
                //TODO: discuss this on monday. Probably need to parseInt the loader files
                Team winner = determineWinner(match);
                Team loser = determineLoser(match);
                // mark match as completed
                match.setStatus(Match.Status.COMPLETED);
                matchRepository.save(match);

                // handle advancement in the brackets
                if (match.getBracket() == Match.Bracket.WINNERS) {
                    advanceInWinnersBracket(winner, tournament);
                    advanceToLosersBracket(loser, tournament);
                } else {
                    // eliminate or advance the losing team
                    eliminateOrAdvanceLoser(loser, match ,tournament);
                }
            }
            //see if the tournament is complete
            tournamentComplete = checkIfTournamentComplete(tournament);
        }
    }
    // determine the winner of a match
    private Team determineWinner(Match match) {
        List<Team> teamList = new ArrayList<>(match.getTeams());
        return teamList.get(0); // Replace with actual logic based on match results
    }

    // determine the loser of a match
    private Team determineLoser(Match match) {
        List<Team> teamList = new ArrayList<>(match.getTeams());
        return teamList.get(1); // Replace with actual logic based on match results
    }

    private void advanceInWinnersBracket(Team winner, Tournament tournament) {
        Match nextMatch = new Match();
        nextMatch.setTeams(new LinkedHashSet<>(Arrays.asList(winner)));
        nextMatch.setTournament(tournament);
        nextMatch.setRoundNumber(1);
        nextMatch.setBracket(Match.Bracket.WINNERS);
        nextMatch.setStatus(Match.Status.WAITING);

        matchRepository.save(nextMatch);
    }

    private void advanceToLosersBracket(Team loser, Tournament tournament) {
        Match losersMatch = new Match();
        losersMatch.setTeams(new LinkedHashSet<>(Arrays.asList(loser)));
        losersMatch.setTournament(tournament);
        losersMatch.setRoundNumber(1); // Adjust as necessary
        losersMatch.setBracket(Match.Bracket.LOSERS);
        losersMatch.setStatus(Match.Status.WAITING);

        matchRepository.save(losersMatch);
    }

    // eliminate or advance a losing team in the losers bracket
    private void eliminateOrAdvanceLoser(Team loser, Match currentMatch, Tournament tournament) {
        if (loser.hasLostTwice()) {
            // Eliminate the team after two losses
            loser.setStatus(Team.Status.ELIMINATED);
            teamRepository.save(loser);
        } else {
            // Advance the loser in the losers bracket if they haven't lost twice
            Match nextLosersMatch = new Match();
            nextLosersMatch.setTeams(new LinkedHashSet<>(Arrays.asList(loser))); // Corrected to advance the loser
            nextLosersMatch.setTournament(tournament);
            nextLosersMatch.setRoundNumber(currentMatch.getRoundNumber() + 1); // Increment round number
            nextLosersMatch.setBracket(Match.Bracket.LOSERS);
            nextLosersMatch.setStatus(Match.Status.WAITING);
            matchRepository.save(nextLosersMatch);
        }
    }

    // check if the tournament is complete
    private boolean checkIfTournamentComplete(Tournament tournament) {
        // Logic to determine if one team remains in each bracket
        List<Team> remainingWinners = teamRepository.findByTournamentAndBracket(tournament, Match.Bracket.WINNERS);
        List<Team> remainingLosers = teamRepository.findByTournamentAndBracket(tournament, Match.Bracket.LOSERS);

        return remainingWinners.size() == 1 && remainingLosers.size() == 1;
    }
}
