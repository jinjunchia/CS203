package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Round;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.RoundRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;

    @Autowired
    public RoundServiceImpl(RoundRepository roundRepository){
        this.roundRepository = roundRepository;
    }

    @Override
    @Transactional
    public Round createRound(Round round){
        return roundRepository.save(round);
    }

    @Override
    public Optional<Round> findRoundById(Long id){
        return roundRepository.findById(id);
    }

    @Override
    public List<Round> findRoundsByTournament(Tournament tournament){
        return roundRepository.findByMatches_Tournament_Id(tournament.getId());
    }

    @Override
    @Transactional
    public Round updateRound(Long id, Round roundDetails) {
        return roundRepository.findById(id)  // Ensure the ID is passed here
                .map(round -> {
                    round.setRoundNumber(roundDetails.getRoundNumber());
//                    round.setTournament(roundDetails.getTournament());
                    round.setMatches(roundDetails.getMatches());

                    // Update RoundType if it exists in roundDetails
                    if (roundDetails.getRoundType() != null) {
                        round.setRoundType(roundDetails.getRoundType());
                    }

                    return roundRepository.save(round);
                })
                .orElseThrow(() -> new RuntimeException("Round not found with id " + id));
    }
    @Override
    @Transactional
    public void deleteRound(Long id) {
        roundRepository.deleteById(id);
    }

}
