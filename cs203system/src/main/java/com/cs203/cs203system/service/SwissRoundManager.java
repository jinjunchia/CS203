package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.springframework.stereotype.Service;


public interface SwissRoundManager {

    Tournament initializeSwiss(Tournament tournament);

    Tournament receiveMatchResult(Match match);

    Player determineWinner(Tournament tournament);
}
