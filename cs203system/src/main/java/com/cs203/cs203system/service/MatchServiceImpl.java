package com.cs203.cs203system.service;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchServiceImpl implements MatchService{

    @Autowired
    private MatchRepository matchRepository;

    public Match findMatchById(long id) {
        match = matchRepository.findById(id).orElseThrow(() -> new NotFoundException("Tournament with id " + id + " not found"));
        return match;
    }


}
