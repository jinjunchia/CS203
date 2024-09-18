package com.cs203.cs203system.service;
import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.model.Tournament;

import java.util.List;
import java.util.Optional;
public interface RoundService {
    Round createRound(Round round);
    Optional<Round> findRoundById(Integer id);
    List<Round> findRoundsByTournament(Tournament tournament);

    Round updateRound(Integer id, Round roundDetails);

    void deleteRound(Integer id);

}
