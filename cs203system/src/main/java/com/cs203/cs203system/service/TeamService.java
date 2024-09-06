package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    Team createTeam(Team team);
    List<Team> findAllTeams();
    Optional<Team> findTeamById(Integer id);
    Team updateTeam(Team team);
    void deleteTeam(Integer id);

}
