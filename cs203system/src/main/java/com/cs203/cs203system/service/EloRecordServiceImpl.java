package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EloRecordServiceImpl implements EloRecordService {

    @Autowired
    private EloRecordRepository eloRecordRepository;

    @Override
    public List<EloRecord> findAllEloRecords() {
        return eloRecordRepository.findAll();
    }

    @Override
    public EloRecord findEloRecordById(Long id) {
        Optional<EloRecord> eloRecord = eloRecordRepository.findById(id);
        return eloRecord.orElseThrow(() -> new RuntimeException("Elo Record not found for id: " + id));
    }

    @Override
    @Transactional
    public void saveEloRecord(EloRecord eloRecord) {
        eloRecordRepository.save(eloRecord);
    }

    @Override
    @Transactional
    public void updateEloRecord(Long id, EloRecord eloRecordDetails) {
        EloRecord eloRecord = findEloRecordById(id);

        eloRecord.setPlayer(eloRecordDetails.getPlayer());
        eloRecord.setMatch(eloRecordDetails.getMatch());
        eloRecord.setOldRating(eloRecordDetails.getOldRating());
        eloRecord.setNewRating(eloRecordDetails.getNewRating());
        eloRecord.setChangeReason(eloRecordDetails.getChangeReason());
        eloRecord.setDate(eloRecordDetails.getDate());

        eloRecordRepository.save(eloRecord);
    }


    @Override
    @Transactional
    public void deleteEloRecord(Long id) {
        EloRecord eloRecord = findEloRecordById(id);
        eloRecordRepository.delete(eloRecord);
    }

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
