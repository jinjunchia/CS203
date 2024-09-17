package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EloRecordServiceImpl implements EloRecordService {

    @Autowired
    private EloRecordRepository eloRecordRepository;

    @Override
    @Transactional
    public void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason) {
        // Creates and saves an EloRecord entity to track rating changes
        EloRecord record = EloRecord.builder()
                .player(player)
                .match(match)
                .oldRating(oldRating)
                .newRating(newRating)
                .changeReason(reason)
                .date(LocalDateTime.now())
                .build();

        eloRecordRepository.save(record);
    }
}
