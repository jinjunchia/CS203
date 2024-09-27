//package com.cs203.cs203system.utility;
//
//import com.cs203.cs203system.enums.*;
//import com.cs203.cs203system.model.Tournament;
//import com.cs203.cs203system.model.Match;
//import com.cs203.cs203system.model.Player;
//import com.cs203.cs203system.model.Round;
//import jakarta.transaction.Transactional;
//import org.springframework.data.util.Pair;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public interface DoubleEliminationManager2 {
//
//        @Transactional
//        public void initializeDoubleElimination(Tournament tournament, List<Player> players);
//
//        @Transactional
//        public void initializeRound(Tournament tournament, List<Player> players);
//
//        @Transactional
//        public int getNextRoundNumber(Tournament tournament);
//
//        @Transactional
//        public void createMatches(Tournament tournament, List<Player> players, Round round);
//
//        @Transactional
//        public List<Match> createBracketMatches(Tournament tournament, List<Player> players, Round round);
//
//        @Transactional
//        public void processNextRound(Tournament tournament);
//
//        @Transactional
//        //havent include draw
//        public void playMatches(List<Match> Match);
//
//        public Player determineWinner(Tournament tournament);
//
//        public Player determineTournamentWinner(List<Player> players);
//
//        public boolean isDoubleEliminationComplete(Tournament tournament);
//}
//
//
